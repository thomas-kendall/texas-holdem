package poker.texasholdem.deck;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class Deck {

	private Stack<Card> cards;

	public Deck() {
		cards = new Stack<>();

		// Populate the list with all of the cards
		List<Card> list = new ArrayList<>();
		for (Suit suit : Suit.values()) {
			for (CardValue value : CardValue.values()) {
				list.add(new Card(suit, value));
			}
		}

		// Randomly add cards to the stack
		Random random = new Random();
		while (!list.isEmpty()) {
			cards.add(list.remove(random.nextInt(list.size())));
		}
	}

	public Card draw() {
		return cards.pop();
	}
}
