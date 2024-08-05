package poker.texasholdem.hand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.junit.jupiter.api.Test;

import poker.texasholdem.cards.CommunityCards;
import poker.texasholdem.cards.HoleCards;
import poker.texasholdem.deck.Card;
import poker.texasholdem.deck.Deck;
import poker.texasholdem.player.Player;
import poker.texasholdem.structure.Blinds;
import poker.texasholdem.utility.CardSerializer;
import poker.texasholdem.utility.PotAssertions;

public class HandControllerTests {

	@Test
	public void test_Hand1() {
		Player sb = new Player("SB", 100);
		Player bb = new Player("BB", 100);
		Player mp = new Player("MP", 100);
		Player co = new Player("CO", 100);
		Player btn = new Player("BTN", 100);
		List<HoleCards> holeCardsList = new ArrayList<>();
		holeCardsList.add(CardSerializer.toHoleCards("Js", "Jd")); // SB
		holeCardsList.add(CardSerializer.toHoleCards("7h", "2c")); // BB
		holeCardsList.add(CardSerializer.toHoleCards("Ah", "Tc")); // MP
		holeCardsList.add(CardSerializer.toHoleCards("9d", "3d")); // CO
		holeCardsList.add(CardSerializer.toHoleCards("8c", "6d")); // BTN
		CommunityCards communityCards = CardSerializer.toCommunityCards("Td", "6h", "5c", "4s", "Th");
		Deck deck = setupDeck(communityCards, holeCardsList);
		HandController controller = new HandController(new Blinds(2, 1), Arrays.asList(sb, bb, mp, co, btn), deck);
		controller.raise(mp, 6);
		controller.fold(co);
		controller.fold(btn);
		controller.raise(sb, 24);
		controller.fold(bb);
		controller.call(mp);
		// end of preflop betting
		PotAssertions.assertPot(50, Arrays.asList(sb, bb, mp), controller.getPots().getPots().get(0));
		// flop dealt
		controller.bet(sb, 35);
		controller.raise(mp, 76);
		controller.call(sb);
		// all in, so no more action
		// turn dealt
		// river dealt
		// hand complete
		HandResult result = controller.getHandResult();

		assertNotNull(result);
		assertEquals(1, result.getPotResults().size());
		assertEquals(mp, result.getPotResults().get(0).getPlayer());
		assertEquals(202, result.getPotResults().get(0).getChipsWon());

		assertEquals(0, sb.getChipStack().getChips());
		assertEquals(98, bb.getChipStack().getChips());
		assertEquals(202, mp.getChipStack().getChips());
		assertEquals(100, co.getChipStack().getChips());
		assertEquals(100, btn.getChipStack().getChips());
	}

	@Test
	public void test_allInInTheBlindsSB() {
		Player sb = new Player("SB", 100);
		Player bb = new Player("BB", 900);
		List<HoleCards> holeCardsList = new ArrayList<>();
		// When heads up, sb and bb are in reverse order
		holeCardsList.add(CardSerializer.toHoleCards("7h", "2c")); // BB
		holeCardsList.add(CardSerializer.toHoleCards("Js", "Jd")); // SB
		CommunityCards communityCards = CardSerializer.toCommunityCards("Td", "6h", "5c", "4s", "Th");
		Deck deck = setupDeck(communityCards, holeCardsList);
		HandController controller = new HandController(new Blinds(200, 100), Arrays.asList(bb, sb), deck);
		// all in, so no more action
		// flop dealt
		// turn dealt
		// river dealt
		// hand complete
		HandResult result = controller.getHandResult();

		assertNotNull(result);
		assertEquals(2, result.getPotResults().size());
		assertEquals(sb, result.getPotResults().get(0).getPlayer());
		assertEquals(200, result.getPotResults().get(0).getChipsWon());
		assertEquals(bb, result.getPotResults().get(1).getPlayer());
		assertEquals(100, result.getPotResults().get(1).getChipsWon());

		assertEquals(200, sb.getChipStack().getChips());
		assertEquals(800, bb.getChipStack().getChips());
	}

	@Test
	public void test_allInInTheBlindsBB() {
		Player sb = new Player("SB", 900);
		Player bb = new Player("BB", 100);
		List<HoleCards> holeCardsList = new ArrayList<>();
		// When heads up, sb and bb are in reverse order
		holeCardsList.add(CardSerializer.toHoleCards("7h", "2c")); // BB
		holeCardsList.add(CardSerializer.toHoleCards("Js", "Jd")); // SB
		CommunityCards communityCards = CardSerializer.toCommunityCards("Td", "6h", "5c", "4s", "Th");
		Deck deck = setupDeck(communityCards, holeCardsList);
		HandController controller = new HandController(new Blinds(200, 100), Arrays.asList(bb, sb), deck);
		// all in, so no more action
		// flop dealt
		// turn dealt
		// river dealt
		// hand complete
		HandResult result = controller.getHandResult();

		assertNotNull(result);
		assertEquals(1, result.getPotResults().size());
		assertEquals(sb, result.getPotResults().get(0).getPlayer());
		assertEquals(200, result.getPotResults().get(0).getChipsWon());

		assertEquals(1000, sb.getChipStack().getChips());
		assertEquals(0, bb.getChipStack().getChips());
	}

	private Deck setupDeck(CommunityCards communityCards, List<HoleCards> holeCardsList) {
		Deck source = new Deck();
		Stack<Card> target = new Stack<>();

		// Load the top of the deck in reverse order
		Stack<Card> topOfDeckInReverseOrder = new Stack<>();
		for (int i = 0; i < 2; i++) {
			for (HoleCards holeCards : holeCardsList) {
				topOfDeckInReverseOrder.push(holeCards.getCards().get(i));
			}
		}
		for (Card card : communityCards.getCards()) {
			topOfDeckInReverseOrder.push(card);
		}

		// Stack the bottom of the deck
		while (!source.isEmpty()) {
			Card card = source.draw();
			if (!topOfDeckInReverseOrder.contains(card)) {
				target.push(card);
			}
		}

		// Stack the top of the deck
		while (!topOfDeckInReverseOrder.isEmpty()) {
			target.push(topOfDeckInReverseOrder.pop());
		}

		return new Deck(target);
	}
}
