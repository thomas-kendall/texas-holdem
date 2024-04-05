package poker.texasholdem.player;

import lombok.Getter;
import poker.texasholdem.cards.HoleCards;
import poker.texasholdem.chips.ChipStack;
import poker.texasholdem.deck.Card;

public class Player {

	@Getter
	private String name;

	@Getter
	private ChipStack chipStack;

	@Getter
	private HoleCards holeCards;

	public Player(String name, int chips) {
		this.name = name;
		this.chipStack = new ChipStack(chips);
		this.holeCards = new HoleCards();
	}

	public void addCard(Card card) {
		holeCards.addCard(card);
	}

	public boolean hasChips() {
		return !getChipStack().isEmpty();
	}
}
