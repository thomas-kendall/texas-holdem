package poker.texasholdem.event;

public class HoleCardsDealtEvent extends TexasHoldemEvent {

	public HoleCardsDealtEvent() {
		super(TexasHoldemEventType.HOLE_CARDS_DEALT);
	}
}
