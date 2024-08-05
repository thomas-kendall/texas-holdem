package poker.texasholdem.tournament;

/**
 * Represents a tournament with 9-handed tables, a starting stack of 3000 chips
 * and suitable blind levels.
 */
public class StandardTournament extends Tournament {

	public StandardTournament(String name) {
		// @formatter:off
		super(name, 3000, new BlindsManager(new StructureBuilder()
				.withBlindLevel(10, 5, 600)
				.withBlindLevel(20, 10, 600)
				.withBlindLevel(30, 15, 600)
				.withBlindLevel(50, 25, 600)
				.withBlindLevel(80, 40, 600)
				.withBlindLevel(120, 60, 600)
				.withBlindLevel(160, 80, 600)
				.withBlindLevel(200, 100, 600)
				.withBlindLevel(300, 150, 600)
				.withBlindLevel(500, 250, 600)
				.withBlindLevel(800, 400, 600)
				.withBlindLevel(1200, 600, 600)
				.withBlindLevel(1600, 800, 600)
				.withBlindLevel(2000, 1000, -1)
				.build()), new TableManager(9));
		// @formatter:on
	}
}
