package poker.texasholdem.deck;

import java.util.Optional;
import java.util.stream.Stream;

import lombok.Getter;

public enum Suit {
	CLUBS("c"), DIAMONDS("d"), HEARTS("h"), SPADES("s");

	@Getter
	private String label;

	private Suit(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return label;
	}

	public static Suit of(String label) {
		Optional<Suit> optional = Stream.of(Suit.values()).filter(suit -> suit.label.equals(label)).findFirst();
		if (optional.isEmpty()) {
			throw new RuntimeException("Invalid suit label: " + label);
		}
		return optional.get();
	}
}
