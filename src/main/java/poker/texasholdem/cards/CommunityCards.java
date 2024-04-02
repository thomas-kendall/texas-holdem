package poker.texasholdem.cards;

import poker.texasholdem.deck.Card;

public class CommunityCards extends Cards {
	@Override
	public void addCard(Card card) {
		if (getCards().size() >= 5) {
			throw new RuntimeException("Only 5 community cards allowed.");
		}
		super.addCard(card);
	}
}
