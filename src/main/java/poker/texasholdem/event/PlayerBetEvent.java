package poker.texasholdem.event;

import lombok.Getter;
import poker.texasholdem.player.Player;

public class PlayerBetEvent extends TexasHoldemEvent {

	@Getter
	private Player player;

	@Getter
	private int chips;

	public PlayerBetEvent(Player player, int chips) {
		super(TexasHoldemEventType.PLAYER_BET);
		this.player = player;
		this.chips = chips;
	}
}
