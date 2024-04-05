package poker.texasholdem.event;

import lombok.Getter;
import poker.texasholdem.deck.Card;

public class RiverDealtEvent extends TexasHoldemEvent {

	@Getter
	private Card riverCard;

	public RiverDealtEvent(Card riverCard) {
		super(TexasHoldemEventType.RIVER_DEALT);
		this.riverCard = riverCard;
	}
}
