package poker.texasholdem.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import poker.texasholdem.chips.Pot;
import poker.texasholdem.player.Player;

public class PotAssertions {
	public static void assertPot(int expectedPotSize, List<Player> expectedPlayers, Pot actualPot) {
		assertEquals(expectedPotSize, actualPot.getSize());
		assertEquals(expectedPlayers.size(), actualPot.getPlayers().size());
		for (Player expectedPlayer : expectedPlayers) {
			assertTrue(actualPot.getPlayers().contains(expectedPlayer));
		}
	}
}
