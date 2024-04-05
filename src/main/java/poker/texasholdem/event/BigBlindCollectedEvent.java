package poker.texasholdem.event;

import lombok.Getter;
import poker.texasholdem.player.Player;

public class BigBlindCollectedEvent extends TexasHoldemEvent {

	@Getter
	private Player player;

	@Getter
	private int chips;

	public BigBlindCollectedEvent(Player player, int chips) {
		super(TexasHoldemEventType.BIG_BLIND_COLLECTED);
		this.player = player;
		this.chips = chips;
	}
}
