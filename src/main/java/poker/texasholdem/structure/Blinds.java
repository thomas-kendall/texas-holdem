package poker.texasholdem.structure;

import lombok.Getter;

public class Blinds {
	@Getter
	private int bigBlind;

	@Getter
	private int smallBlind;

	// TODO: Add ante, and supporting logic

	public Blinds(int bigBlind, int smallBlind) {
		if (bigBlind < 1) {
			throw new RuntimeException("Invalid big blind: " + bigBlind);
		}
		if (smallBlind < 1) {
			throw new RuntimeException("Invalid small blind: " + smallBlind);
		}

		this.bigBlind = bigBlind;
		this.smallBlind = smallBlind;
	}
}
