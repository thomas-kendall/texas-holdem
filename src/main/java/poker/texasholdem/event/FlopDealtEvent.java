package poker.texasholdem.event;

import java.util.List;

import lombok.Getter;
import poker.texasholdem.deck.Card;

public class FlopDealtEvent extends TexasHoldemEvent {

	@Getter
	private List<Card> flopCards;

	public FlopDealtEvent(List<Card> flopCards) {
		super(TexasHoldemEventType.FLOP_DEALT);
		this.flopCards = flopCards;
	}
}
