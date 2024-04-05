package poker.texasholdem.bet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import poker.texasholdem.player.Player;

/**
 * BettingRound keeps track of bets that have been made by players for a round
 * of betting.
 */
public class BettingRound {

	@Getter
	private List<PlacedBet> placedBets;

	public BettingRound() {
		this.placedBets = new ArrayList<>();
	}

	/**
	 * placeBet() sets the bet that has been placed by player. It does not remove
	 * chips from the chip stack of the player.
	 *
	 * @param player - the player making the bet
	 * @param bet    - the amount of the bet
	 */
	public void placeBet(Player player, int bet) {
		PlacedBet placedBet = null;
		Optional<PlacedBet> optionalPlacedBet = placedBets.stream().filter(pb -> pb.getPlayer().equals(player))
				.findFirst();
		if (optionalPlacedBet.isPresent()) {
			placedBet = optionalPlacedBet.get();
			placedBet.setBet(bet);
		} else {
			placedBet = new PlacedBet(player, bet);
			placedBets.add(placedBet);
		}
	}

	/**
	 * getPlacedBet() returns the amount of the bet placed by the player.
	 *
	 * @param player - the Player of reference
	 * @return - the total amount of the bets placed by the player
	 */
	public int getPlacedBet(Player player) {
		Optional<PlacedBet> optionalPlacedBet = placedBets.stream().filter(pb -> pb.getPlayer().equals(player))
				.findFirst();
		if (optionalPlacedBet.isPresent()) {
			return optionalPlacedBet.get().getBet();
		}
		return 0;
	}

}
