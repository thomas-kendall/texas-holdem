package poker.texasholdem.event;

import lombok.Getter;
import poker.texasholdem.player.Player;

public class PlayerCheckedEvent extends TexasHoldemEvent {

	@Getter
	private Player player;

	public PlayerCheckedEvent(Player player) {
		super(TexasHoldemEventType.PLAYER_CHECKED);
		this.player = player;
	}
}
