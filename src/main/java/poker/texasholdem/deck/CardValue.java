package poker.texasholdem.deck;

import java.util.Optional;
import java.util.stream.Stream;

import lombok.Getter;

public enum CardValue {
	TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10, "T"), JACK(11, "J"),
	QUEEN(12, "Q"), KING(13, "K"), ACE(14, "A"),;

	@Getter
	private String label;

	@Getter
	private int value;

	private CardValue(int value, String label) {
		this.value = value;
		this.label = label;
	}

	private CardValue(int value) {
		this(value, String.valueOf(value));
	}

	@Override
	public String toString() {
		return label;
	}

	public static CardValue of(String label) {
		Optional<CardValue> optional = Stream.of(CardValue.values()).filter(cardValue -> cardValue.label.equals(label))
				.findFirst();
		if (optional.isEmpty()) {
			throw new RuntimeException("Invalid CardValue label: " + label);
		}
		return optional.get();
	}
}
