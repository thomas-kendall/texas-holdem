package poker.texasholdem.hand;

import java.util.List;
import java.util.Map;

import poker.texasholdem.deck.Card;
import poker.texasholdem.deck.CardValue;
import poker.texasholdem.utility.CardUtility;

public class HandScorer {
	public static HandClassification classifyHand(List<Card> cards) {
		if (cards.size() != 5) {
			throw new RuntimeException("Must have exactly 5 cards to classify.");
		}

		if (isStraightFlush(cards)) {
			return HandClassification.STRAIGHT_FLUSH;
		}
		if (isFourOfAKind(cards)) {
			return HandClassification.FOUR_OF_A_KIND;
		}
		if (isFullHouse(cards)) {
			return HandClassification.FULL_HOUSE;
		}
		if (isFlush(cards)) {
			return HandClassification.FLUSH;
		}
		if (isStraight(cards)) {
			return HandClassification.STRAIGHT;
		}
		if (isThreeOfAKind(cards)) {
			return HandClassification.THREE_OF_A_KIND;
		}
		if (isTwoPair(cards)) {
			return HandClassification.TWO_PAIR;
		}
		if (isOnePair(cards)) {
			return HandClassification.ONE_PAIR;
		}

		return HandClassification.HIGH_CARD;
	}

	public static HandScore scoreHand(HandClassification classification, List<Card> cards) {
		if (cards.size() != 5) {
			throw new RuntimeException("Must have exactly 5 cards to score.");
		}

		// Sort cards according to classification
		List<Card> sortedCards = CardSorter.sort(classification, cards);

		// Score the hand
		switch (classification) {
		case STRAIGHT_FLUSH:
			return new HandScore(HandClassification.STRAIGHT_FLUSH, scoreStraightFlushHand(sortedCards));
		case FOUR_OF_A_KIND:
			return new HandScore(HandClassification.FOUR_OF_A_KIND, scoreFourOfAKindHand(sortedCards));
		case FULL_HOUSE:
			return new HandScore(HandClassification.FULL_HOUSE, scoreFullHouseHand(sortedCards));
		case FLUSH:
			return new HandScore(HandClassification.FLUSH, scoreFlushHand(sortedCards));
		case STRAIGHT:
			return new HandScore(HandClassification.STRAIGHT, scoreStraightHand(sortedCards));
		case THREE_OF_A_KIND:
			return new HandScore(HandClassification.THREE_OF_A_KIND, scoreThreeOfAKindHand(sortedCards));
		case TWO_PAIR:
			return new HandScore(HandClassification.TWO_PAIR, scoreTwoPairHand(sortedCards));
		case ONE_PAIR:
			return new HandScore(HandClassification.ONE_PAIR, scoreOnePairHand(sortedCards));
		case HIGH_CARD:
			return new HandScore(HandClassification.HIGH_CARD, scoreHighCardHand(sortedCards));
		default:
			throw new RuntimeException("Hand classification not handled: " + classification);
		}
	}

	public static HandScore scoreHand(List<Card> cards) {
		return scoreHand(classifyHand(cards), cards);
	}

	private static boolean isStraightFlush(List<Card> cards) {
		return isFlush(cards) && isStraight(cards);
	}

	private static boolean isFourOfAKind(List<Card> cards) {
		Map<CardValue, List<Card>> cardsByValue = CardUtility.mapCardsByCardValue(cards);
		return cardsByValue.values().stream().filter(cl -> cl.size() == 4).count() == 1;
	}

	private static boolean isFullHouse(List<Card> cards) {
		Map<CardValue, List<Card>> cardsByValue = CardUtility.mapCardsByCardValue(cards);
		return cardsByValue.values().stream().filter(cl -> cl.size() == 3).count() == 1
				&& cardsByValue.values().stream().filter(cl -> cl.size() == 2).count() == 1;
	}

	private static boolean isFlush(List<Card> cards) {
		return cards.stream().map(c -> c.getSuit()).distinct().count() == 1;
	}

	private static boolean isStraight(List<Card> cards) {
		// Check with ace as high
		List<Integer> values = cards.stream().map(c -> c.getValue().getValue()).toList();
		if (isInARow(values)) {
			return true;
		}
		// Check with ace as low
		if (values.contains(CardValue.ACE.getValue())) {
			values = cards.stream().map(c -> c.getValue() == CardValue.ACE ? 1 : c.getValue().getValue()).toList();
			if (isInARow(values)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isThreeOfAKind(List<Card> cards) {
		Map<CardValue, List<Card>> cardsByValue = CardUtility.mapCardsByCardValue(cards);
		return cardsByValue.values().stream().filter(cl -> cl.size() == 3).count() == 1;
	}

	private static boolean isTwoPair(List<Card> cards) {
		Map<CardValue, List<Card>> cardsByValue = CardUtility.mapCardsByCardValue(cards);
		return cardsByValue.values().stream().filter(cl -> cl.size() == 2).count() == 2;
	}

	private static boolean isOnePair(List<Card> cards) {
		Map<CardValue, List<Card>> cardsByValue = CardUtility.mapCardsByCardValue(cards);
		return cardsByValue.values().stream().filter(cl -> cl.size() == 2).count() == 1;
	}

	private static boolean isInARow(List<Integer> values) {
		List<Integer> sortedValues = values.stream().sorted().toList();
		for (int i = 1; i < sortedValues.size(); i++) {
			if (sortedValues.get(i) != sortedValues.get(i - 1) + 1) {
				return false;
			}
		}
		return true;
	}

	private static int scoreHighCardHand(List<Card> sortedCards) {
		int score = 0;
		for (int i = 0; i < 5; i++) {
			score *= 100;
			score += sortedCards.get(i).getValue().getValue();
		}
		return score;
	}

	private static int scoreOnePairHand(List<Card> sortedCards) {
		int score = sortedCards.get(0).getValue().getValue();
		for (int i = 2; i < 5; i++) {
			score *= 100;
			score += sortedCards.get(i).getValue().getValue();
		}
		return score;
	}

	private static int scoreTwoPairHand(List<Card> sortedCards) {
		int score = sortedCards.get(0).getValue().getValue();
		score *= 100;
		score += sortedCards.get(2).getValue().getValue();
		score *= 100;
		score += sortedCards.get(4).getValue().getValue();
		return score;
	}

	private static int scoreThreeOfAKindHand(List<Card> sortedCards) {
		int score = sortedCards.get(0).getValue().getValue();
		for (int i = 3; i < 5; i++) {
			score *= 100;
			score += sortedCards.get(i).getValue().getValue();
		}
		return score;
	}

	private static int scoreStraightHand(List<Card> sortedCards) {
		int score = sortedCards.get(0).getValue().getValue();
		return score;
	}

	private static int scoreFlushHand(List<Card> sortedCards) {
		int score = 0;
		for (int i = 0; i < 5; i++) {
			score *= 100;
			score += sortedCards.get(i).getValue().getValue();
		}
		return score;
	}

	private static int scoreFullHouseHand(List<Card> sortedCards) {
		int score = sortedCards.get(0).getValue().getValue();
		score *= 100;
		score += sortedCards.get(3).getValue().getValue();
		return score;
	}

	private static int scoreFourOfAKindHand(List<Card> sortedCards) {
		int score = sortedCards.get(0).getValue().getValue();
		score *= 100;
		score += sortedCards.get(4).getValue().getValue();
		return score;
	}

	private static int scoreStraightFlushHand(List<Card> sortedCards) {
		int score = sortedCards.get(0).getValue().getValue();
		return score;
	}

}
