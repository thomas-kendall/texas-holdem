package poker.texasholdem.chips;

import lombok.Getter;

public class ChipStack {

	@Getter
	private int chips;

	public ChipStack() {
		this.chips = 0;
	}

	public ChipStack(int chips) {
		if (chips < 0) {
			throw new RuntimeException("Invalid chip amount: " + chips);
		}
		this.chips = chips;
	}

	public void addChips(int chips) {
		if (chips < 1) {
			throw new RuntimeException("Cannot add chip amount: " + chips);
		}
		this.chips += chips;
	}

	public void removeChips(int chips) {
		if (chips > this.chips) {
			throw new RuntimeException("Cannot remove chip amount: " + chips);
		}
		this.chips -= chips;
	}

	public boolean isEmpty() {
		return this.chips == 0;
	}
}
