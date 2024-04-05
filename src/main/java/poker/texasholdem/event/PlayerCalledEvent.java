package poker.texasholdem.event;

import lombok.Getter;
import poker.texasholdem.player.Player;

public class PlayerCalledEvent extends TexasHoldemEvent {

	@Getter
	private Player player;

	@Getter
	private int chips;

	public PlayerCalledEvent(Player player, int chips) {
		super(TexasHoldemEventType.PLAYER_CALLED);
		this.player = player;
		this.chips = chips;
	}
}
