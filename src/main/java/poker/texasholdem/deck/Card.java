package poker.texasholdem.deck;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class Card {
	@Getter
	private Suit suit;

	@Getter
	private CardValue value;

	public Card(Suit suit, CardValue value) {
		this.suit = suit;
		this.value = value;
	}

	@Override
	public String toString() {
		return value.toString() + suit.toString();
	}

	public static Card of(String s) {
		if (s.length() != 2) {
			throw new RuntimeException("Invalid card representation: " + s);
		}
		CardValue value = CardValue.of(s.substring(0, 1));
		Suit suit = Suit.of(s.substring(1, 2));
		return new Card(suit, value);
	}
}
