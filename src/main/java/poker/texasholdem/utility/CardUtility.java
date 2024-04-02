package poker.texasholdem.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import poker.texasholdem.deck.Card;
import poker.texasholdem.deck.CardValue;

public class CardUtility {
	public static Map<CardValue, List<Card>> mapCardsByCardValue(List<Card> cards) {
		Map<CardValue, List<Card>> map = new HashMap<>();
		for (Card card : cards) {
			if (!map.containsKey(card.getValue())) {
				map.put(card.getValue(), new ArrayList<>());
			}
			map.get(card.getValue()).add(card);
		}
		return map;
	}
}
