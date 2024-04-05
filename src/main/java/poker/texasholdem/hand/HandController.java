package poker.texasholdem.hand;

import java.util.ArrayList;
import java.util.List;

import poker.texasholdem.bet.BettingRound;
import poker.texasholdem.cards.CommunityCards;
import poker.texasholdem.chips.Pots;
import poker.texasholdem.deck.Deck;
import poker.texasholdem.player.Player;
import poker.texasholdem.structure.BlindLevel;

/**
 * Controls the playing of a single hand
 */
public class HandController {

	private BlindLevel blindLevel;

	private List<Player> players; // first player is the small blind, last player is the button

	private Deck deck;

	private Pots pots;

	private BettingRound bettingRound;

	private int currentPlayerIndex; // the index of the player that the action is on

	int currentBet;

	int previousBet;

	private CommunityCards communityCards;

	public HandController(BlindLevel blindLevel, List<Player> players) {
		if (players.size() < 2 || players.size() > 10) {
			throw new RuntimeException("Must have between 2 and 10 players.");
		}
		if (players.stream().anyMatch(p -> p.getChipStack().getChips() < 1)) {
			throw new RuntimeException("All players must have chips.");
		}

		this.deck = new Deck();
		this.blindLevel = blindLevel;
		this.players = new ArrayList<>(players);
		this.pots = new Pots(players);
		communityCards = new CommunityCards();

		// Initialize for preflop
		this.bettingRound = new BettingRound();
		collectBlinds();
		dealHoleCards();
		previousBet = 0;
		currentBet = blindLevel.getBigBlind();
		currentPlayerIndex = players.size() == 2 ? 1 : 2;
	}

	public void check(Player player) {
		validateCurrentPlayer(player);
		if (currentBet > 0) {
			throw new RuntimeException("Checking not allowed when there is a bet.");
		}
	}

	public void bet(Player player, int betSize) {
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
		onPlayerActionCompleted();
	}

	public void call(Player player) {
		validateCurrentPlayer(player);
		if (currentBet == 0) {
			throw new RuntimeException("Calling not allowed when there is not a bet.");
		}
		if (player.getChipStack().getChips() == 0) {
			throw new RuntimeException("Insufficient chip stack.");
		}
		int intendedBet = currentBet;
		placeBet(player, intendedBet);
		onPlayerActionCompleted();
	}

	public void raise(Player player, int betSize) {
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
		onPlayerActionCompleted();
	}

	public void fold(Player player) {
		validateCurrentPlayer(player);
		players.remove(player);
		pots.removeEligiblePlayerFromAllPots(player);
		onPlayerActionCompleted();
	}

	private void onPlayerActionCompleted() {
		// Either put the action on the next player or process the end of the betting
		// round
		if (isBettingRoundComplete()) {
			// Pull in the bets
			pots.pullInBets(bettingRound);
			bettingRound = new BettingRound();

			if (players.size() == 1) {
				// TODO: The hand is over, declare the result
			} else if (communityCards.getCards().isEmpty()) {
				// Deal the flop
				communityCards.addCard(deck.draw());
				communityCards.addCard(deck.draw());
				communityCards.addCard(deck.draw());
				currentPlayerIndex = 0;
			} else if (communityCards.getCards().size() == 3) {
				// Deal the turn
				communityCards.addCard(deck.draw());
				currentPlayerIndex = 0;
			} else if (communityCards.getCards().size() == 4) {
				// Deal the river
				communityCards.addCard(deck.draw());
				currentPlayerIndex = 0;
			} else {
				// TODO: The hand is over, declare the result
			}
		} else {
			// Action is on the next player
			currentPlayerIndex++;
			if (currentPlayerIndex >= players.size()) {
				currentPlayerIndex = 0;
			}
		}
	}

	private boolean isBettingRoundComplete() {
		// Is there only one player left?
		if (players.size() == 1) {
			return true;
		}

		// Is there any player that has not put in the bet amount?
		if (currentBet > 0) {
			for (Player player : players) {
				if (player.hasChips() && bettingRound.getPlacedBet(player) < currentBet) {
					return false;
				}
			}
			return true;
		} else {
			// Did it check around?
			if (currentPlayerIndex == players.size() - 1) {
				return true;
			} else {
				// Nobody has bet yet, but not everyone has acted
				return false;
			}
		}
	}

	/**
	 * @return the amount of additional chips the current player must place in order
	 *         to call
	 */
	public int getCurrentPlayerCallAmount() {
		return currentBet - bettingRound.getPlacedBet(getCurrentPlayer());
	}

	/**
	 * @return the minimum bet size for a raise
	 */
	public int getMinimumRaiseBetSize() {
		return 2 * currentBet - previousBet;
	}

	private Player getCurrentPlayer() {
		return players.get(currentPlayerIndex);
	}

	private void validateCurrentPlayer(Player player) {
		if (!player.equals(getCurrentPlayer())) {
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
	}

	private void collectBigBlind() {
		Player bigBlindPlayer = players.size() == 2 ? players.get(0) : players.get(1);
		int chips = Math.min(blindLevel.getBigBlind(), bigBlindPlayer.getChipStack().getChips());
		bettingRound.placeBet(bigBlindPlayer, chips);
		bigBlindPlayer.getChipStack().removeChips(chips);
	}

	private void dealHoleCards() {
		for (int i = 0; i < 2; i++) {
			for (Player player : players) {
				player.getHoleCards().addCard(deck.draw());
			}
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

}
