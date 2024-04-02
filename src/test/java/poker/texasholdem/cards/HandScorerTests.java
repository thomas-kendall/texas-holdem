package poker.texasholdem.cards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import poker.texasholdem.hand.HandClassification;
import poker.texasholdem.hand.HandScore;
import poker.texasholdem.hand.HandScorer;
import poker.texasholdem.utility.CardSerializer;

public class HandScorerTests {

	@Test
	public void test_classifyHand() {
		assertEquals(HandClassification.STRAIGHT_FLUSH,
				HandScorer.classifyHand(CardSerializer.toCards("Ad", "Kd", "Qd", "Jd", "Td")));

		assertEquals(HandClassification.STRAIGHT_FLUSH,
				HandScorer.classifyHand(CardSerializer.toCards("5d", "4d", "3d", "2d", "Ad")));

		assertEquals(HandClassification.FOUR_OF_A_KIND,
				HandScorer.classifyHand(CardSerializer.toCards("7d", "4d", "7h", "7c", "7s")));

		assertEquals(HandClassification.FULL_HOUSE,
				HandScorer.classifyHand(CardSerializer.toCards("7d", "4d", "7h", "7c", "4s")));

		assertEquals(HandClassification.FLUSH,
				HandScorer.classifyHand(CardSerializer.toCards("5d", "Kd", "3d", "2d", "Ad")));

		assertEquals(HandClassification.STRAIGHT,
				HandScorer.classifyHand(CardSerializer.toCards("5d", "4d", "3s", "2s", "6d")));

		assertEquals(HandClassification.STRAIGHT,
				HandScorer.classifyHand(CardSerializer.toCards("5d", "4d", "3s", "2s", "Ad")));

		assertEquals(HandClassification.STRAIGHT,
				HandScorer.classifyHand(CardSerializer.toCards("Qd", "Jc", "Kd", "As", "Th")));

		assertEquals(HandClassification.THREE_OF_A_KIND,
				HandScorer.classifyHand(CardSerializer.toCards("7d", "4d", "7h", "7c", "Qs")));

		assertEquals(HandClassification.TWO_PAIR,
				HandScorer.classifyHand(CardSerializer.toCards("7d", "4d", "7h", "4c", "Qs")));

		assertEquals(HandClassification.ONE_PAIR,
				HandScorer.classifyHand(CardSerializer.toCards("7d", "4d", "7h", "Jc", "Qs")));

		assertEquals(HandClassification.HIGH_CARD,
				HandScorer.classifyHand(CardSerializer.toCards("5c", "Qd", "3c", "9s", "Ah")));
	}

	@Test
	public void test_scoreHand() {
		assertHandScores(
				HandScorer.scoreHand(HandClassification.STRAIGHT_FLUSH,
						CardSerializer.toCards("Ad", "Kd", "Qd", "Jd", "Td")),
				HandScorer.scoreHand(HandClassification.STRAIGHT_FLUSH,
						CardSerializer.toCards("6d", "5d", "4d", "3d", "2d")),
				HandScorer.scoreHand(HandClassification.STRAIGHT_FLUSH,
						CardSerializer.toCards("5d", "4d", "3d", "2d", "Ad")),
				HandScorer.scoreHand(HandClassification.FOUR_OF_A_KIND,
						CardSerializer.toCards("9c", "9d", "9h", "9s", "3c")),
				HandScorer.scoreHand(HandClassification.FOUR_OF_A_KIND,
						CardSerializer.toCards("8c", "8d", "8h", "8s", "Kc")),
				HandScorer.scoreHand(HandClassification.FOUR_OF_A_KIND,
						CardSerializer.toCards("8c", "8d", "8h", "8s", "2c")),
				HandScorer.scoreHand(HandClassification.FULL_HOUSE,
						CardSerializer.toCards("9d", "9d", "9h", "2s", "2c")),
				HandScorer.scoreHand(HandClassification.FULL_HOUSE,
						CardSerializer.toCards("8d", "8d", "8h", "7s", "7c")),
				HandScorer.scoreHand(HandClassification.FULL_HOUSE,
						CardSerializer.toCards("8d", "8d", "8h", "3s", "3c")),
				HandScorer.scoreHand(HandClassification.FLUSH, CardSerializer.toCards("Ac", "Qc", "Tc", "3c", "2c")),
				HandScorer.scoreHand(HandClassification.FLUSH, CardSerializer.toCards("Ac", "Qc", "9c", "5c", "3c")),
				HandScorer.scoreHand(HandClassification.STRAIGHT, CardSerializer.toCards("As", "Kd", "Qd", "Jc", "Th")),
				HandScorer.scoreHand(HandClassification.STRAIGHT, CardSerializer.toCards("6d", "5d", "4d", "3s", "2s")),
				HandScorer.scoreHand(HandClassification.STRAIGHT, CardSerializer.toCards("5d", "4d", "3s", "2s", "Ad")),
				HandScorer.scoreHand(HandClassification.THREE_OF_A_KIND,
						CardSerializer.toCards("8c", "8d", "8h", "3s", "2c")),
				HandScorer.scoreHand(HandClassification.THREE_OF_A_KIND,
						CardSerializer.toCards("7c", "7d", "7h", "Qs", "5c")),
				HandScorer.scoreHand(HandClassification.THREE_OF_A_KIND,
						CardSerializer.toCards("7c", "7d", "7h", "Qs", "4c")),
				HandScorer.scoreHand(HandClassification.TWO_PAIR, CardSerializer.toCards("7c", "7d", "4h", "4c", "Qs")),
				HandScorer.scoreHand(HandClassification.TWO_PAIR, CardSerializer.toCards("7c", "7d", "2h", "2c", "Qs")),
				HandScorer.scoreHand(HandClassification.TWO_PAIR, CardSerializer.toCards("6c", "6d", "5h", "5c", "Ks")),
				HandScorer.scoreHand(HandClassification.ONE_PAIR, CardSerializer.toCards("Jd", "Jh", "4d", "3s", "2c")),
				HandScorer.scoreHand(HandClassification.ONE_PAIR, CardSerializer.toCards("7d", "7h", "Kc", "Qs", "4d")),
				HandScorer.scoreHand(HandClassification.ONE_PAIR, CardSerializer.toCards("7d", "7h", "Qs", "Jc", "4d")),
				HandScorer.scoreHand(HandClassification.HIGH_CARD,
						CardSerializer.toCards("Ah", "Qd", "9s", "5c", "4c")),
				HandScorer.scoreHand(HandClassification.HIGH_CARD,
						CardSerializer.toCards("Ah", "Qd", "9s", "5c", "3c")));
	}

	private void assertHandScores(HandScore... expectedScoresBestToWorst) {
		for (int i = 1; i < expectedScoresBestToWorst.length; i++) {
			int compareResult = expectedScoresBestToWorst[i - 1].compareTo(expectedScoresBestToWorst[i]);
			assertTrue(compareResult > 0);
			compareResult = expectedScoresBestToWorst[i].compareTo(expectedScoresBestToWorst[i - 1]);
			assertTrue(compareResult < 0);
		}
	}
}
