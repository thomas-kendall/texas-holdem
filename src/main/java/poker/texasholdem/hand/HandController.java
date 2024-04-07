package poker.texasholdem.hand;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import poker.texasholdem.bet.BettingRound;
import poker.texasholdem.cards.CommunityCards;
import poker.texasholdem.chips.Pot;
import poker.texasholdem.chips.Pots;
import poker.texasholdem.deck.Deck;
import poker.texasholdem.event.AwaitingPlayerActionEvent;
import poker.texasholdem.event.BetsPulledInEvent;
import poker.texasholdem.event.BigBlindCollectedEvent;
import poker.texasholdem.event.FlopDealtEvent;
import poker.texasholdem.event.HandCompletedEvent;
import poker.texasholdem.event.HoleCardsDealtEvent;
import poker.texasholdem.event.PlayerBetEvent;
import poker.texasholdem.event.PlayerCalledEvent;
import poker.texasholdem.event.PlayerCheckedEvent;
import poker.texasholdem.event.PlayerFoldedEvent;
import poker.texasholdem.event.PlayerRaisedEvent;
import poker.texasholdem.event.RiverDealtEvent;
import poker.texasholdem.event.SmallBlindCollectedEvent;
import poker.texasholdem.event.TexasHoldemEventListener;
import poker.texasholdem.event.TurnDealtEvent;
import poker.texasholdem.player.Player;
import poker.texasholdem.structure.BlindLevel;

/**
 * Controls the playing of a single hand
 */
public class HandController {

	private BlindLevel blindLevel;

	private List<Player> players; // first player is the small blind, last player is the button
	private List<Player> activePlayers; // players that have not folded

	private Deck deck;

	@Getter
	private Pots pots;

	private BettingRound bettingRound;

	int currentBet;

	int previousBet;

	private CommunityCards communityCards;

	@Getter
	private HandResult handResult;

	private List<TexasHoldemEventListener> eventListeners;

	private Player currentPlayer;

	public HandController(BlindLevel blindLevel, List<Player> players, Deck deck) {
		if (players.size() < 2 || players.size() > 10) {
			throw new RuntimeException("Must have between 2 and 10 players.");
		}
		if (players.stream().anyMatch(p -> p.getChipStack().getChips() < 1)) {
			throw new RuntimeException("All players must have chips.");
		}

		this.eventListeners = new ArrayList<>();
		this.deck = deck;
		this.blindLevel = blindLevel;
		this.players = new ArrayList<>(players);
		this.activePlayers = new ArrayList<>(players);
		this.pots = new Pots();
		communityCards = new CommunityCards();
		handResult = null;

		// Initialize for preflop
		this.bettingRound = new BettingRound();
		collectBlinds();
		dealHoleCards();
		previousBet = 0;
		currentBet = blindLevel.getBigBlind();

		// If there is less than two players that have chips, then we just deal the
		// whole hand
		if (players.stream().filter(p -> p.hasChips()).count() < 2) {
			pots.pullInBets(bettingRound);

			if (!eventListeners.isEmpty()) {
				BetsPulledInEvent e = new BetsPulledInEvent(pots.getPots());
				eventListeners.forEach(el -> el.onBetsPulledIn(e));
			}

			dealFlop();
			dealTurn();
			dealRiver();
			onHandComplete();
		} else {
			currentPlayer = players.size() == 2 ? players.get(1) : players.get(2);

			if (!eventListeners.isEmpty()) {
				AwaitingPlayerActionEvent e = new AwaitingPlayerActionEvent(currentPlayer);
				eventListeners.forEach(el -> el.onAwaitingPlayerAction(e));
			}
		}
	}

	public HandController(BlindLevel blindLevel, List<Player> players) {
		this(blindLevel, players, new Deck());
	}

	public void check(Player player) {
		validateHandNotComplete();
		validateCurrentPlayer(player);
		if (currentBet > 0) {
			throw new RuntimeException("Checking not allowed when there is a bet.");
		}
		if (!eventListeners.isEmpty()) {
			PlayerCheckedEvent e = new PlayerCheckedEvent(player);
			eventListeners.forEach(el -> el.onPlayerChecked(e));
		}
	}

	public void bet(Player player, int betSize) {
		validateHandNotComplete();
		validateCurrentPlayer(player);
		if (currentBet > 0) {
			throw new RuntimeException("Betting is not allowed when there is a bet.");
		}
		if (getTotalChips(player) < betSize) {
			throw new RuntimeException("Insufficient chip stack.");
		}
		if (betSize < blindLevel.getBigBlind() && getTotalChips(player) > betSize) {
			throw new RuntimeException("Invalid bet size.");
		}
		int intendedBet = Math.max(betSize, blindLevel.getBigBlind());
		placeBet(player, intendedBet);

		if (!eventListeners.isEmpty()) {
			PlayerBetEvent e = new PlayerBetEvent(player, betSize);
			eventListeners.forEach(el -> el.onPlayerBet(e));
		}

		onPlayerActionCompleted();
	}

	public void call(Player player) {
		validateHandNotComplete();
		validateCurrentPlayer(player);
		if (currentBet == 0) {
			throw new RuntimeException("Calling not allowed when there is not a bet.");
		}
		if (player.getChipStack().getChips() == 0) {
			throw new RuntimeException("Insufficient chip stack.");
		}
		int intendedBet = currentBet;
		int actualBet = Math.min(intendedBet, player.getChipStack().getChips());
		placeBet(player, intendedBet);

		if (!eventListeners.isEmpty()) {
			PlayerCalledEvent e = new PlayerCalledEvent(player, actualBet);
			eventListeners.forEach(el -> el.onPlayerCalled(e));
		}

		onPlayerActionCompleted();
	}

	public void raise(Player player, int betSize) {
		validateHandNotComplete();
		validateCurrentPlayer(player);
		if (currentBet == 0) {
			throw new RuntimeException("Raising is not allowed when there is a bet.");
		}
		if (getTotalChips(player) < betSize) {
			throw new RuntimeException("Insufficient chip stack.");
		}
		if (betSize < getMinimumRaiseBetSize() && getTotalChips(player) > betSize) {
			throw new RuntimeException("Invalid raise size.");
		}
		int intendedBet = Math.max(betSize, getMinimumRaiseBetSize());
		placeBet(player, intendedBet);

		if (!eventListeners.isEmpty()) {
			PlayerRaisedEvent e = new PlayerRaisedEvent(player, betSize);
			eventListeners.forEach(el -> el.onPlayerRaised(e));
		}

		onPlayerActionCompleted();
	}

	public void fold(Player player) {
		validateHandNotComplete();
		validateCurrentPlayer(player);
		activePlayers.remove(player);

		if (!eventListeners.isEmpty()) {
			PlayerFoldedEvent e = new PlayerFoldedEvent(player);
			eventListeners.forEach(el -> el.onPlayerFolded(e));
		}

		onPlayerActionCompleted();
	}

	/**
	 * @return the amount of additional chips the current player must place in order
	 *         to call
	 */
	public int getCurrentPlayerCallAmount() {
		validateHandNotComplete();
		return currentBet - bettingRound.getPlacedBet(currentPlayer);
	}

	/**
	 * @return the minimum bet size for a raise
	 */
	public int getMinimumRaiseBetSize() {
		validateHandNotComplete();
		return 2 * currentBet - previousBet;
	}

	public void addEventListener(TexasHoldemEventListener listener) {
		eventListeners.add(listener);
	}

	public void removeEventListener(TexasHoldemEventListener listener) {
		eventListeners.remove(listener);
	}

	private void onPlayerActionCompleted() {
		// Either put the action on the next player or process the end of the betting
		// round
		if (isBettingRoundComplete()) {
			// Pull in the bets
			pots.pullInBets(bettingRound);

			if (!eventListeners.isEmpty()) {
				BetsPulledInEvent e = new BetsPulledInEvent(pots.getPots());
				eventListeners.forEach(el -> el.onBetsPulledIn(e));
			}

			if (activePlayers.size() == 1 || communityCards.getCards().size() == 5) {
				// The hand is over, declare the result
				onHandComplete();
			} else if (communityCards.getCards().isEmpty()) {
				// Deal the flop
				dealFlop();
				bettingRound = new BettingRound();
				previousBet = 0;
				currentBet = 0;
				currentPlayer = null;
				setNextCurrentPlayer();

				if (!eventListeners.isEmpty()) {
					AwaitingPlayerActionEvent e = new AwaitingPlayerActionEvent(currentPlayer);
					eventListeners.forEach(el -> el.onAwaitingPlayerAction(e));
				}
			} else if (communityCards.getCards().size() == 3) {
				// Deal the turn
				dealTurn();
				bettingRound = new BettingRound();
				previousBet = 0;
				currentBet = 0;
				currentPlayer = null;
				setNextCurrentPlayer();
				if (currentPlayer == null) {
					dealRiver();
					onHandComplete();
				} else {
					if (!eventListeners.isEmpty()) {
						AwaitingPlayerActionEvent e = new AwaitingPlayerActionEvent(currentPlayer);
						eventListeners.forEach(el -> el.onAwaitingPlayerAction(e));
					}
				}
			} else if (communityCards.getCards().size() == 4) {
				// Deal the river
				dealRiver();
				bettingRound = new BettingRound();
				previousBet = 0;
				currentBet = 0;
				currentPlayer = null;
				setNextCurrentPlayer();
				if (currentPlayer == null) {
					onHandComplete();
				} else {

					if (!eventListeners.isEmpty()) {
						AwaitingPlayerActionEvent e = new AwaitingPlayerActionEvent(currentPlayer);
						eventListeners.forEach(el -> el.onAwaitingPlayerAction(e));
					}
				}
			} else {
				throw new RuntimeException("Invalid state, should never reach this point.");
			}
		} else {
			// Action is on the next player
			setNextCurrentPlayer();
			if (currentPlayer == null) {
				// No more action, finish dealing the hand
				if (communityCards.getCards().size() == 0) {
					dealFlop();
				}
				if (communityCards.getCards().size() == 3) {
					dealTurn();
				}
				if (communityCards.getCards().size() == 4) {
					dealRiver();
				}
				onHandComplete();
			}

			if (!eventListeners.isEmpty()) {
				AwaitingPlayerActionEvent e = new AwaitingPlayerActionEvent(currentPlayer);
				eventListeners.forEach(el -> el.onAwaitingPlayerAction(e));
			}
		}
	}

	/**
	 * Set the currentPlayer to the next player, or null if there is no action left
	 * to be had.
	 */
	private void setNextCurrentPlayer() {
		List<Player> activePlayersInOrder = currentPlayer == null
				? activePlayersInOrder = intersection(players, activePlayers)
				: intersection(getOtherPlayersRelativeToPlayer(currentPlayer), activePlayers);

		for (Player player : activePlayersInOrder) {
			if (player.hasChips()) {
				currentPlayer = player;
				break;
			}
		}

		// If currentPlayer is still null, we did not find a next player, meaning there
		// is no action left for the hand
	}

	private boolean isBettingRoundComplete() {
		// Is there only one player left?
		if (activePlayers.size() == 1) {
			return true;
		}

		// Is there any player that has not put in the bet amount?
		if (currentBet > 0) {
			for (Player player : activePlayers) {
				if (player.hasChips() && bettingRound.getPlacedBet(player) < currentBet) {
					return false;
				}
			}
			return true;
		} else {
			// Did it check around?
			if (currentPlayer.equals(activePlayers.get(activePlayers.size() - 1))) {
				return true;
			} else {
				// Nobody has bet yet, but not everyone has acted
				return false;
			}
		}
	}

	private void validateHandNotComplete() {
		if (handResult != null) {
			throw new RuntimeException("Hand is complete.");
		}
	}

	private void validateCurrentPlayer(Player player) {
		if (!player.equals(currentPlayer)) {
			throw new RuntimeException("Incorrect player.");
		}
	}

	private void collectBlinds() {
		// Collect the blinds
		collectSmallBlind();
		collectBigBlind();
	}

	private void collectSmallBlind() {
		Player smallBlindPlayer = players.size() == 2 ? players.get(1) : players.get(0);
		int chips = Math.min(blindLevel.getSmallBlind(), smallBlindPlayer.getChipStack().getChips());
		bettingRound.placeBet(smallBlindPlayer, chips);
		smallBlindPlayer.getChipStack().removeChips(chips);
		if (!eventListeners.isEmpty()) {
			SmallBlindCollectedEvent e = new SmallBlindCollectedEvent(smallBlindPlayer, chips);
			eventListeners.forEach(el -> el.onSmallBlindCollected(e));
		}
	}

	private void collectBigBlind() {
		Player bigBlindPlayer = players.size() == 2 ? players.get(0) : players.get(1);
		int chips = Math.min(blindLevel.getBigBlind(), bigBlindPlayer.getChipStack().getChips());
		bettingRound.placeBet(bigBlindPlayer, chips);
		bigBlindPlayer.getChipStack().removeChips(chips);
		if (!eventListeners.isEmpty()) {
			BigBlindCollectedEvent e = new BigBlindCollectedEvent(bigBlindPlayer, chips);
			eventListeners.forEach(el -> el.onBigBlindCollected(e));
		}
	}

	private void dealHoleCards() {
		for (int i = 0; i < 2; i++) {
			for (Player player : players) {
				player.getHoleCards().addCard(deck.draw());
			}
		}

		if (!eventListeners.isEmpty()) {
			HoleCardsDealtEvent e = new HoleCardsDealtEvent();
			eventListeners.forEach(el -> el.onHoleCardsDealt(e));
		}
	}

	private void dealFlop() {
		communityCards.addCard(deck.draw());
		communityCards.addCard(deck.draw());
		communityCards.addCard(deck.draw());

		if (!eventListeners.isEmpty()) {
			FlopDealtEvent e = new FlopDealtEvent(communityCards.getCards());
			eventListeners.forEach(el -> el.onFlopDealt(e));
		}
	}

	private void dealTurn() {
		communityCards.addCard(deck.draw());

		if (!eventListeners.isEmpty()) {
			TurnDealtEvent e = new TurnDealtEvent(communityCards.getCards().get(3));
			eventListeners.forEach(el -> el.onTurnDealt(e));
		}
	}

	private void dealRiver() {
		communityCards.addCard(deck.draw());

		if (!eventListeners.isEmpty()) {
			RiverDealtEvent e = new RiverDealtEvent(communityCards.getCards().get(4));
			eventListeners.forEach(el -> el.onRiverDealt(e));
		}
	}

	/**
	 * Returns the total amount of chips for the player, including chips in play
	 * (bets already placed this betting round).
	 *
	 * @param player - the referenced Player
	 * @return the total amount of chips for the player, including chips in play
	 *         (bets already placed this betting round)
	 */
	private int getTotalChips(Player player) {
		return player.getChipStack().getChips() + bettingRound.getPlacedBet(player);
	}

	/**
	 * placeBet() moves chips from the player to the BettingRound.
	 *
	 * @param player      - the Player placing the bet
	 * @param intendedBet - the bet size that is intended, even if the player does
	 *                    not have that many chips. For example, a player calling an
	 *                    all-in for less.
	 */
	private void placeBet(Player player, int intendedBet) {
		// Calculate the total bet size
		int actualBet = Math.min(intendedBet, getTotalChips(player));
		int chipsToRemove = actualBet - bettingRound.getPlacedBet(player);

		// Remove the chips from the player
		player.getChipStack().removeChips(chipsToRemove);

		// Set the bet to the PlacedBet of the player
		bettingRound.placeBet(player, actualBet);

		// Does this represent an initial bet or raise?
		if (intendedBet > this.currentBet) {
			this.previousBet = this.currentBet;
			this.currentBet = intendedBet;
		}
	}

	private void onHandComplete() {
		// Build the hand result
		List<PotResult> potResults = new ArrayList<>();
		for (Pot pot : pots.getPots()) {
			if (pot.getSize() > 0) {
				List<Player> potWinners = getWinners(pot);
				if (potWinners.size() == 1) {
					potResults.add(new PotResult(pot.getName(), potWinners.get(0), pot.getSize()));
				} else {
					// Split the pot among the winners
					List<Integer> winnings = new ArrayList<>();
					int chipsPerWinner = pot.getSize() / potWinners.size();
					int chipsLeftOver = pot.getSize() % potWinners.size();
					for (int i = 0; i < potWinners.size(); i++) {
						int chips = chipsPerWinner;
						if (chipsLeftOver > 0) {
							chips++;
							chipsLeftOver--;
						}
						winnings.add(chips);
					}
					for (int i = 0; i < potWinners.size(); i++) {
						potResults.add(new PotResult(pot.getName(), potWinners.get(i), winnings.get(i)));
					}
				}
			}
		}

		// Move chips to winners
		for (PotResult potResult : potResults) {
			potResult.getPlayer().getChipStack().addChips(potResult.getChipsWon());
		}

		// Set the hand result
		this.handResult = new HandResult(potResults);

		if (!eventListeners.isEmpty()) {
			HandCompletedEvent e = new HandCompletedEvent(this.handResult);
			eventListeners.forEach(el -> el.onHandCompleted(e));
		}
	}

	private List<Player> getWinners(Pot pot) {
		List<Player> eligibleWinners = intersection(activePlayers, pot.getPlayers());
		if (eligibleWinners.size() == 1) {
			return eligibleWinners;
		} else {
			Map<Player, Hand> hands = eligibleWinners.stream()
					.collect(Collectors.toMap(p -> p, p -> Hand.of(communityCards, p.getHoleCards())));
			Hand winningHand = hands.values().stream().sorted(Comparator.reverseOrder()).findFirst().get();
			return eligibleWinners.stream().filter(p -> hands.get(p).compareTo(winningHand) == 0).toList();
		}
	}

	private List<Player> intersection(List<Player> list1, List<Player> list2) {
		return list1.stream().filter(p -> list2.contains(p)).toList();
	}

	private List<Player> getOtherPlayersRelativeToPlayer(Player player) {
		List<Player> others = new ArrayList<>();

		int i = players.indexOf(player);
		int playersToCheck = players.size() - 1;
		for (int j = 0; j < playersToCheck; j++) {
			i++;
			if (i == players.size()) {
				i = 0;
			}
			others.add(players.get(i));
		}

		return others;
	}
}
