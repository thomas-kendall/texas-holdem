package poker.texasholdem.chips;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import poker.texasholdem.bet.BettingRound;
import poker.texasholdem.player.Player;

public class PotsTests {

	@Test
	public void test_pullInBets_checkedAround() {
		List<Player> players = Arrays.asList(new Player("SB", 100), new Player("BB", 100), new Player("MP", 100),
				new Player("CO", 100), new Player("BTN", 100));
		Pots pots = new Pots(players);
		BettingRound bettingRound = new BettingRound();

		// Checked around, no bets

		// Pull in the bets
		pots.pullInBets(bettingRound);

		assertEquals(1, pots.getPots().size());
		assertTrue(pots.getCurrentPot().getSize() == 0);
	}

	@Test
	public void test_pullInBets_oneBetEveryoneCalls() {
		List<Player> players = Arrays.asList(new Player("SB", 100), new Player("BB", 100), new Player("MP", 100),
				new Player("CO", 100), new Player("BTN", 100));
		Pots pots = new Pots(players);
		BettingRound bettingRound = new BettingRound();

		// One bet, everyone calls
		for (Player player : players) {
			placeBet(bettingRound, player, 10);
		}

		// Pull in the bets
		pots.pullInBets(bettingRound);

		assertEquals(1, pots.getPots().size());
		assertEquals(players.size() * 10, pots.getCurrentPot().getSize());
	}

	@Test
	public void test_pullInBets_betFoldRaiseCallFoldFold() {
		List<Player> players = Arrays.asList(new Player("SB", 100), new Player("BB", 100), new Player("MP", 100),
				new Player("CO", 100), new Player("BTN", 100));
		Pots pots = new Pots(players);
		BettingRound bettingRound = new BettingRound();

		// Bet, fold, raise, call, fold, fold
		placeBet(bettingRound, players.get(0), 10); // bet
		pots.removeEligiblePlayerFromAllPots(players.get(1)); // fold
		placeBet(bettingRound, players.get(2), 30); // raise
		placeBet(bettingRound, players.get(3), 30); // call
		pots.removeEligiblePlayerFromAllPots(players.get(4)); // fold
		pots.removeEligiblePlayerFromAllPots(players.get(0)); // fold

		// Pull in the bets
		pots.pullInBets(bettingRound);

		assertEquals(1, pots.getPots().size());
		assertEquals(70, pots.getCurrentPot().getSize());
	}

	@Test
	public void test_pullInBets_drySidePot() {
		List<Player> players = Arrays.asList(new Player("SB", 100), new Player("BB", 100), new Player("BTN", 60));
		Pots pots = new Pots(players);
		BettingRound bettingRound = new BettingRound();

		// Bet, call, raise all in, call, call
		placeBet(bettingRound, players.get(0), 10); // bet
		placeBet(bettingRound, players.get(1), 10); // call
		placeBet(bettingRound, players.get(2), 60); // raise all in
		placeBet(bettingRound, players.get(0), 60); // call
		placeBet(bettingRound, players.get(1), 60); // call

		// Pull in the bets
		pots.pullInBets(bettingRound);

		assertEquals(2, pots.getPots().size());
		assertEquals(180, pots.getPots().get(0).getSize());
		assertTrue(pots.getPots().get(1).getSize() == 0);
	}

	@Test
	public void test_pullInBets_twoSidePots() {
		List<Player> players = Arrays.asList(new Player("SB", 80), new Player("BB", 100), new Player("MP", 40),
				new Player("CO", 200), new Player("BTN", 1));
		Pots pots = new Pots(players);
		BettingRound bettingRound = new BettingRound();

		// Bet, raise all in, all call
		placeBet(bettingRound, players.get(0), 30); // bet
		placeBet(bettingRound, players.get(1), 100); // raise all in
		placeBet(bettingRound, players.get(2), 40); // call
		placeBet(bettingRound, players.get(3), 100); // call
		placeBet(bettingRound, players.get(4), 1); // call
		placeBet(bettingRound, players.get(0), 80); // call

		// Pull in the bets
		pots.pullInBets(bettingRound);

		assertEquals(5, pots.getPots().size());
		// BTN all in and calls
		assertPot(5, players, pots.getPots().get(0));
		// MP all in and calls
		assertPot(156, Arrays.asList(players.get(0), players.get(1), players.get(2), players.get(3)),
				pots.getPots().get(1));
		// SB all in and calls
		assertPot(120, Arrays.asList(players.get(0), players.get(1), players.get(3)), pots.getPots().get(2));
		// BB all in and calls
		assertPot(40, Arrays.asList(players.get(1), players.get(3)), pots.getPots().get(3));
		// Empty side pot
		assertPot(0, Arrays.asList(players.get(3)), pots.getPots().get(4));
	}

	private void assertPot(int expectedPotSize, List<Player> expectedEligiblePlayers, Pot actualPot) {
		assertEquals(expectedPotSize, actualPot.getSize());
		assertEquals(expectedEligiblePlayers.size(), actualPot.getEligiblePlayers().size());
		for (Player expectedEligiblePlayer : expectedEligiblePlayers) {
			assertTrue(actualPot.getEligiblePlayers().contains(expectedEligiblePlayer));
		}
	}

	private void placeBet(BettingRound bettingRound, Player player, int bet) {
		player.getChipStack().removeChips(bet - bettingRound.getPlacedBet(player));
		bettingRound.placeBet(player, bet);
	}
}
