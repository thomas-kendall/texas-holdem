package poker.texasholdem.event;

import lombok.Getter;
import poker.texasholdem.player.Player;

public class SmallBlindCollectedEvent extends TexasHoldemEvent {

	@Getter
	private Player player;

	@Getter
	private int chips;

	public SmallBlindCollectedEvent(Player player, int chips) {
		super(TexasHoldemEventType.SMALL_BLIND_COLLECTED);
		this.player = player;
		this.chips = chips;
	}
}
