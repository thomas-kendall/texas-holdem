package poker.texasholdem.utility;

import java.util.ArrayList;
import java.util.List;

import poker.texasholdem.cards.CommunityCards;
import poker.texasholdem.cards.HoleCards;
import poker.texasholdem.deck.Card;
import poker.texasholdem.hand.Hand;

public class CardSerializer {
	public static Hand toHand(String... cardStrings) {
		if (cardStrings.length != 5) {
			throw new RuntimeException("Expected 5 cards, got " + cardStrings.length);
		}
		return new Hand(toCards(cardStrings));
	}

	public static List<Card> toCards(String... cardStrings) {
		List<Card> cards = new ArrayList<>();
		for (String cardString : cardStrings) {
			cards.add(Card.of(cardString));
		}
		return cards;
	}

	public static CommunityCards toCommunityCards(String... cardStrings) {
		if (cardStrings.length != 5) {
			throw new RuntimeException("Expected 5 cards, got " + cardStrings.length);
		}
		CommunityCards communityCards = new CommunityCards();
		for (String cardString : cardStrings) {
			communityCards.addCard(Card.of(cardString));
		}
		return communityCards;
	}

	public static HoleCards toHoleCards(String... cardStrings) {
		if (cardStrings.length != 2) {
			throw new RuntimeException("Expected 2 cards, got " + cardStrings.length);
		}
		HoleCards holeCards = new HoleCards();
		for (String cardString : cardStrings) {
			holeCards.addCard(Card.of(cardString));
		}
		return holeCards;
	}
}
