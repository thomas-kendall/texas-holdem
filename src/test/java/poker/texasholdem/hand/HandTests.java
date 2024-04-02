package poker.texasholdem.hand;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import poker.texasholdem.cards.CommunityCards;
import poker.texasholdem.cards.HoleCards;
import poker.texasholdem.utility.CardSerializer;

public class HandTests {

	@Test
	public void test_of() {
		HoleCards holeCards = CardSerializer.toHoleCards("9d", "8d");
		CommunityCards communityCards = CardSerializer.toCommunityCards("As", "9s", "6d", "7c", "5h");
		Hand hand = Hand.of(communityCards, holeCards);
		assertEquals(HandClassification.STRAIGHT, hand.getScore().getHandClassification());
	}

}
