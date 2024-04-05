package poker.texasholdem.event;

import lombok.Getter;
import poker.texasholdem.player.Player;

public class PlayerRaisedEvent extends TexasHoldemEvent {

	@Getter
	private Player player;

	@Getter
	private int chips;

	public PlayerRaisedEvent(Player player, int chips) {
		super(TexasHoldemEventType.PLAYER_RAISED);
		this.player = player;
		this.chips = chips;
	}
}
