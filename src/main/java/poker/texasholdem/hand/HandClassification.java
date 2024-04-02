package poker.texasholdem.hand;

import lombok.Getter;

public enum HandClassification {
	HIGH_CARD(0), ONE_PAIR(1), TWO_PAIR(2), THREE_OF_A_KIND(3), STRAIGHT(4), FLUSH(5), FULL_HOUSE(6), FOUR_OF_A_KIND(7),
	STRAIGHT_FLUSH(8);

	@Getter
	private int value;

	private HandClassification(int value) {
		this.value = value;
	}
}
