package poker.texasholdem.hand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import poker.texasholdem.deck.Card;
import poker.texasholdem.deck.CardValue;
import poker.texasholdem.utility.CardUtility;

public class CardSorter {

	public static List<Card> sort(HandClassification classification, List<Card> cards) {
		switch (classification) {
		case STRAIGHT_FLUSH:
			return sortForStraightFlush(cards);
		case FOUR_OF_A_KIND:
			return sortForFourOfAKind(cards);
		case FULL_HOUSE:
			return sortForFullHouse(cards);
		case FLUSH:
			return sortForFlush(cards);
		case STRAIGHT:
			return sortForStraight(cards);
		case THREE_OF_A_KIND:
			return sortForThreeOfAKind(cards);
		case TWO_PAIR:
			return sortForTwoPair(cards);
		case ONE_PAIR:
			return sortForOnePair(cards);
		case HIGH_CARD:
			return sortForHighCard(cards);
		default:
			throw new RuntimeException("Hand classification not handled: " + classification);
		}
	}

	private static List<Card> sortForStraightFlush(List<Card> cards) {
		return sortForStraight(cards);
	}

	private static List<Card> sortForFourOfAKind(List<Card> cards) {
		// Extract the pairs and other card into separate lists
		Map<CardValue, List<Card>> cardsByValue = CardUtility.mapCardsByCardValue(cards);
		List<Card> quads = cardsByValue.values().stream().filter(cl -> cl.size() == 4).findFirst().get();
		Card otherCard = cardsByValue.values().stream().filter(cl -> cl.size() == 1).flatMap(List::stream).findFirst()
				.get();

		List<Card> sortedCards = new ArrayList<>(quads);
		sortedCards.add(otherCard);
		return sortedCards;
	}

	private static List<Card> sortForFullHouse(List<Card> cards) {
		// Extract the pairs and other card into separate lists
		Map<CardValue, List<Card>> cardsByValue = CardUtility.mapCardsByCardValue(cards);
		List<Card> trips = cardsByValue.values().stream().filter(cl -> cl.size() == 3).findFirst().get();
		List<Card> pair = cardsByValue.values().stream().filter(cl -> cl.size() == 2).findFirst().get();
		return Stream.concat(trips.stream(), pair.stream()).toList();
	}

	private static List<Card> sortForFlush(List<Card> cards) {
		return sortForHighCard(cards);
	}

	private static List<Card> sortForStraight(List<Card> cards) {
		List<Card> sortedCards = new ArrayList<>(sortDesc(cards));
		if (sortedCards.get(0).getValue() == CardValue.ACE && sortedCards.get(1).getValue() == CardValue.FIVE) {
			// Move the ace to the last card of a low straight
			sortedCards.add(sortedCards.remove(0));
		}
		return sortedCards;
	}

	private static List<Card> sortForThreeOfAKind(List<Card> cards) {
		// Extract the trips and other cards into separate lists
		Map<CardValue, List<Card>> cardsByValue = CardUtility.mapCardsByCardValue(cards);
		List<Card> trips = cardsByValue.values().stream().filter(cl -> cl.size() == 3).findFirst().get();
		List<Card> otherCards = cardsByValue.values().stream().filter(cl -> cl.size() == 1).flatMap(List::stream)
				.toList();

		// Sort the rest of the cards desc
		otherCards = sortDesc(otherCards);

		return Stream.concat(trips.stream(), otherCards.stream()).toList();
	}

	private static List<Card> sortForTwoPair(List<Card> cards) {
		// Extract the pairs and other card into separate lists
		Map<CardValue, List<Card>> cardsByValue = CardUtility.mapCardsByCardValue(cards);
		List<Card> pair1 = cardsByValue.values().stream().filter(cl -> cl.size() == 2)
				.sorted(Collections.reverseOrder(Comparator.comparing(cl -> cl.get(0).getValue()))).findFirst().get();
		List<Card> pair2 = cardsByValue.values().stream().filter(cl -> cl.size() == 2)
				.sorted(Collections.reverseOrder(Comparator.comparing(cl -> cl.get(0).getValue()))).skip(1).findFirst()
				.get();
		Card otherCard = cardsByValue.values().stream().filter(cl -> cl.size() == 1).flatMap(List::stream).findFirst()
				.get();

		List<Card> sortedCards = new ArrayList<>();
		sortedCards.addAll(pair1);
		sortedCards.addAll(pair2);
		sortedCards.add(otherCard);
		return sortedCards;
	}

	private static List<Card> sortForOnePair(List<Card> cards) {
		// Extract the pair and other cards into separate lists
		Map<CardValue, List<Card>> cardsByValue = CardUtility.mapCardsByCardValue(cards);
		List<Card> pair = cardsByValue.values().stream().filter(cl -> cl.size() == 2).findFirst().get();
		List<Card> otherCards = cardsByValue.values().stream().filter(cl -> cl.size() == 1).flatMap(List::stream)
				.toList();

		// Sort the rest of the cards desc
		otherCards = sortDesc(otherCards);

		return Stream.concat(pair.stream(), otherCards.stream()).toList();
	}

	private static List<Card> sortForHighCard(List<Card> cards) {
		return sortDesc(cards);
	}

	private static List<Card> sortDesc(List<Card> cards) {
		return cards.stream().sorted(Collections.reverseOrder(Comparator.comparing(c -> c.getValue().getValue())))
				.toList();
	}
}
