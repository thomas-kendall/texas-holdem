package poker.texasholdem.event;

import lombok.Getter;
import poker.texasholdem.hand.HandResult;

public class HandCompletedEvent extends TexasHoldemEvent {

	@Getter
	private HandResult handResult;

	public HandCompletedEvent(HandResult handResult) {
		super(TexasHoldemEventType.HAND_COMPLETE);
		this.handResult = handResult;
	}
}
