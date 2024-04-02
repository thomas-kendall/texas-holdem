package poker.texasholdem.cards;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import poker.texasholdem.deck.Card;

public class Cards {

	@Getter
	private List<Card> cards;

	public Cards() {
		cards = new ArrayList<>();
	}

	protected void setCards(List<Card> cards) {
		this.cards.clear();
		this.cards.addAll(cards);
	}

	protected void addCard(Card card) {
		cards.add(card);
	}

}
