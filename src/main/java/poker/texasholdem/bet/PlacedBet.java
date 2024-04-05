package poker.texasholdem.bet;

import lombok.Getter;
import lombok.Setter;
import poker.texasholdem.player.Player;

/**
 * PlacedBet represents a bet that has been placed, but not added to a pot yet.
 */
public class PlacedBet {

	@Getter
	private Player player;

	@Getter
	@Setter
	private int bet;

	/**
	 * PlacedBet represents a bet that has been placed, but not added to a pot yet.
	 *
	 * @param player - The player making the bet.
	 * @param bet    - The amount of the bet.
	 */
	public PlacedBet(Player player, int bet) {
		this.player = player;
		this.bet = bet;
	}
}
