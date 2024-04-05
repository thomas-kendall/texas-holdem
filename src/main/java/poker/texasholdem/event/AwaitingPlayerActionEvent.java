package poker.texasholdem.event;

import lombok.Getter;
import poker.texasholdem.player.Player;

public class AwaitingPlayerActionEvent extends TexasHoldemEvent {

	@Getter
	private Player player;

	public AwaitingPlayerActionEvent(Player player) {
		super(TexasHoldemEventType.AWAITING_PLAYER_ACTION);
		this.player = player;
	}
}
