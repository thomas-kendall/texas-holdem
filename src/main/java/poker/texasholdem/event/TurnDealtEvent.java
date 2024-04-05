package poker.texasholdem.event;

import lombok.Getter;
import poker.texasholdem.deck.Card;

public class TurnDealtEvent extends TexasHoldemEvent {

	@Getter
	private Card turnCard;

	public TurnDealtEvent(Card turnCard) {
		super(TexasHoldemEventType.TURN_DEALT);
		this.turnCard = turnCard;
	}
}
