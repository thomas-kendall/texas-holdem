package poker.texasholdem.event;

import lombok.Getter;
import poker.texasholdem.player.Player;

public class PlayerFoldedEvent extends TexasHoldemEvent {

	@Getter
	private Player player;

	public PlayerFoldedEvent(Player player) {
		super(TexasHoldemEventType.PLAYER_FOLDED);
		this.player = player;
	}
}
