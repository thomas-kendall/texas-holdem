package poker.texasholdem.cards;

import poker.texasholdem.deck.Card;

public class HoleCards extends Cards {

	@Override
	public void addCard(Card card) {
		if (getCards().size() >= 2) {
			throw new RuntimeException("Only 2 hole cards allowed.");
		}
		super.addCard(card);
	}
}
