package poker.texasholdem.hand;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import poker.texasholdem.cards.Cards;
import poker.texasholdem.cards.CommunityCards;
import poker.texasholdem.cards.HoleCards;
import poker.texasholdem.deck.Card;

/**
 * Hand represents a read-only 5-card hand. The cards are always sorted
 * according to the hand classification.
 */
public class Hand extends Cards implements Comparable<Hand> {

	@Getter
	@Setter(AccessLevel.PRIVATE)
	private HandScore score;

	public Hand(List<Card> cards) {
		if (cards.size() != 5) {
			throw new RuntimeException("Only 5 cards allowed.");
		}

		HandClassification classification = HandScorer.classifyHand(cards);
		List<Card> sortedCards = CardSorter.sort(classification, cards);
		HandScore score = HandScorer.scoreHand(classification, sortedCards);

		setCards(sortedCards);
		setScore(score);
	}

	@Override
	public int compareTo(Hand other) {
		return score.compareTo(other.score);
	}

	@Override
	public String toString() {
		return getCards().toString() + " - " + score.toString();
	}

	public static Hand of(CommunityCards communityCards, HoleCards holeCards) {
		// Put all of the cards in a single list
		List<Card> allCards = new ArrayList<>();
		allCards.addAll(communityCards.getCards());
		allCards.addAll(holeCards.getCards());

		// Loop through the different combinations and keep the best hand
		Hand bestHand = null;
		for (int i1 = 0; i1 < allCards.size(); i1++) {
			for (int i2 = i1 + 1; i2 < allCards.size(); i2++) {
				for (int i3 = i2 + 1; i3 < allCards.size(); i3++) {
					for (int i4 = i3 + 1; i4 < allCards.size(); i4++) {
						for (int i5 = i4 + 1; i5 < allCards.size(); i5++) {
							List<Card> handCards = List.of(allCards.get(i1), allCards.get(i2), allCards.get(i3),
									allCards.get(i4), allCards.get(i5));
							Hand hand = new Hand(handCards);
							if (bestHand == null || hand.compareTo(bestHand) > 0) {
								bestHand = hand;
							}
						}
					}
				}
			}
		}

		return bestHand;
	}
}
