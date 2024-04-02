package poker.texasholdem.cards;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import poker.texasholdem.deck.Card;
import poker.texasholdem.hand.CardSorter;
import poker.texasholdem.hand.HandClassification;
import poker.texasholdem.utility.CardSerializer;

public class CardSorterTests {

	@Test
	public void test_sort_highCard() {
		List<Card> cards = CardSerializer.toCards("5c", "Qd", "3c", "9s", "Ah");
		List<Card> expectedCards = CardSerializer.toCards("Ah", "Qd", "9s", "5c", "3c");
		List<Card> sortedCards = CardSorter.sort(HandClassification.HIGH_CARD, cards);
		assertEquals(expectedCards, sortedCards);
	}

	@Test
	public void test_sort_onePair() {
		List<Card> cards = CardSerializer.toCards("5c", "Qd", "3c", "9s", "5h");
		List<Card> expectedCards = CardSerializer.toCards("5c", "5h", "Qd", "9s", "3c");
		List<Card> sortedCards = CardSorter.sort(HandClassification.ONE_PAIR, cards);
		assertEquals(expectedCards, sortedCards);
	}

	@Test
	public void test_sort_twoPair() {
		List<Card> cards = CardSerializer.toCards("3c", "5c", "Qd", "3s", "5h");
		List<Card> expectedCards = CardSerializer.toCards("5c", "5h", "3c", "3s", "Qd");
		List<Card> sortedCards = CardSorter.sort(HandClassification.TWO_PAIR, cards);
		assertEquals(expectedCards, sortedCards);
	}

	@Test
	public void test_sort_threeOfAKind() {
		List<Card> cards = CardSerializer.toCards("3c", "5c", "5d", "Qs", "5h");
		List<Card> expectedCards = CardSerializer.toCards("5c", "5d", "5h", "Qs", "3c");
		List<Card> sortedCards = CardSorter.sort(HandClassification.THREE_OF_A_KIND, cards);
		assertEquals(expectedCards, sortedCards);
	}

	@Test
	public void test_sort_straight() {
		List<Card> cards = CardSerializer.toCards("3c", "5c", "4d", "7s", "6h");
		List<Card> expectedCards = CardSerializer.toCards("7s", "6h", "5c", "4d", "3c");
		List<Card> sortedCards = CardSorter.sort(HandClassification.STRAIGHT, cards);
		assertEquals(expectedCards, sortedCards);
	}

	@Test
	public void test_sort_straightWithAceHigh() {
		List<Card> cards = CardSerializer.toCards("Tc", "Kc", "Jd", "As", "Qh");
		List<Card> expectedCards = CardSerializer.toCards("As", "Kc", "Qh", "Jd", "Tc");
		List<Card> sortedCards = CardSorter.sort(HandClassification.STRAIGHT, cards);
		assertEquals(expectedCards, sortedCards);
	}

	@Test
	public void test_sort_straightWithAceLow() {
		List<Card> cards = CardSerializer.toCards("3c", "Ac", "4d", "2s", "5h");
		List<Card> expectedCards = CardSerializer.toCards("5h", "4d", "3c", "2s", "Ac");
		List<Card> sortedCards = CardSorter.sort(HandClassification.STRAIGHT, cards);
		assertEquals(expectedCards, sortedCards);
	}

	@Test
	public void test_sort_flush() {
		List<Card> cards = CardSerializer.toCards("3c", "Jc", "Ac", "9c", "Kc");
		List<Card> expectedCards = CardSerializer.toCards("Ac", "Kc", "Jc", "9c", "3c");
		List<Card> sortedCards = CardSorter.sort(HandClassification.FLUSH, cards);
		assertEquals(expectedCards, sortedCards);
	}

	@Test
	public void test_sort_fullHouse() {
		List<Card> cards = CardSerializer.toCards("3c", "5c", "5d", "3s", "5h");
		List<Card> expectedCards = CardSerializer.toCards("5c", "5d", "5h", "3c", "3s");
		List<Card> sortedCards = CardSorter.sort(HandClassification.FULL_HOUSE, cards);
		assertEquals(expectedCards, sortedCards);
	}

	@Test
	public void test_sort_fourOfAKind() {
		List<Card> cards = CardSerializer.toCards("3c", "5c", "5d", "5s", "5h");
		List<Card> expectedCards = CardSerializer.toCards("5c", "5d", "5s", "5h", "3c");
		List<Card> sortedCards = CardSorter.sort(HandClassification.FOUR_OF_A_KIND, cards);
		assertEquals(expectedCards, sortedCards);
	}

	@Test
	public void test_sort_straightFlush() {
		List<Card> cards = CardSerializer.toCards("3c", "5c", "4c", "7c", "6c");
		List<Card> expectedCards = CardSerializer.toCards("7c", "6c", "5c", "4c", "3c");
		List<Card> sortedCards = CardSorter.sort(HandClassification.STRAIGHT_FLUSH, cards);
		assertEquals(expectedCards, sortedCards);
	}

	@Test
	public void test_sort_straightFlushWithAceHigh() {
		List<Card> cards = CardSerializer.toCards("Tc", "Kc", "Jc", "Ac", "Qc");
		List<Card> expectedCards = CardSerializer.toCards("Ac", "Kc", "Qc", "Jc", "Tc");
		List<Card> sortedCards = CardSorter.sort(HandClassification.STRAIGHT_FLUSH, cards);
		assertEquals(expectedCards, sortedCards);
	}

	@Test
	public void test_sort_straightFlushWithAceLow() {
		List<Card> cards = CardSerializer.toCards("3c", "Ac", "4c", "2c", "5c");
		List<Card> expectedCards = CardSerializer.toCards("5c", "4c", "3c", "2c", "Ac");
		List<Card> sortedCards = CardSorter.sort(HandClassification.STRAIGHT_FLUSH, cards);
		assertEquals(expectedCards, sortedCards);
	}
}
