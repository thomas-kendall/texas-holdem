package poker.texasholdem.tournament;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import lombok.Getter;
import poker.texasholdem.player.Player;
import poker.texasholdem.table.PokerTable;

public class TableManager {
	@Getter
	private List<PokerTable> tables;

	private List<ITableEventListener> listeners;
	private int maxPlayersPerTable;
	private List<Player> unseatedPlayers;
	private Random random;

	public TableManager(int maxPlayersPerTable) {
		this.tables = new ArrayList<>();
		this.listeners = new ArrayList<>();
		this.maxPlayersPerTable = maxPlayersPerTable;
		this.unseatedPlayers = new ArrayList<>();
		this.random = new Random();
	}

	public void addEventListener(ITableEventListener listener) {
		if (listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeEventListener(ITableEventListener listener) {
		listeners.remove(listener);
	}

	public void addPlayer(Player player) {
		unseatedPlayers.add(player);
	}

	public void createTables() {
		int numberOfTables = unseatedPlayers.size() / maxPlayersPerTable
				+ (unseatedPlayers.size() % maxPlayersPerTable == 0 ? 0 : 1);
		for (int i = 0; i < numberOfTables; i++) {
			// TODO: should this be a global id instead of local to a TableManager?
			int tableId = i + 1;
			PokerTable table = new PokerTable(tableId, maxPlayersPerTable);
			tables.add(table);

			// Notify listeners
			listeners.forEach(listener -> listener.onTableCreated(table));
		}

		seatUnseatedPlayers();
	}

	/**
	 * This callback gets called after the completion of a hand. The TableManager
	 * will balance tables by potentially moving players off of the given table.
	 *
	 * @param table - The PokerTable where the hand was completed
	 * @return - true if the table is still in existence, false if the table is
	 *         destroyed
	 */
	public boolean onHandComplete(PokerTable table) {
		boolean tableStillExists = true;

		// Unseat any players that no longer have chips
		for (Player player : table.getPlayers()) {
			if (!player.hasChips()) {
				unseatPlayer(table, player);
			}
		}

		// Can this table be broken up?
		int numberOfOpenSeats = tables.stream().filter(t -> !table.equals(t)).map(t -> t.getNumberOfAvailableSeats())
				.collect(Collectors.summingInt(Integer::intValue));
		if (numberOfOpenSeats >= table.getNumberOfSeatedPlayers()) {
			// Move each player to a different table
			for (Player player : table.getPlayers()) {
				unseatPlayer(table, player);
			}

			// Remove the table
			tables.remove(table);
			tableStillExists = false;

			// Notify listeners
			listeners.forEach(listener -> listener.onTableRemoved(table));
		} else {

			// Move players from this table until some balance is achieved
			while (true) {
				int leastPlayersPerTable = tables.stream().map(PokerTable::getNumberOfSeatedPlayers).sorted()
						.findFirst().get();

				int mostPlayersPerTable = tables.stream().map(PokerTable::getNumberOfSeatedPlayers)
						.sorted(Comparator.reverseOrder()).findFirst().get();

				// Does this table have the most players of any table, and is the difference at
				// least 2?
				if (mostPlayersPerTable - leastPlayersPerTable > 1
						&& table.getNumberOfSeatedPlayers() == mostPlayersPerTable) {
					// Unseat random player
					List<Player> seatedPlayers = table.getPlayers();
					Player player = seatedPlayers.get(random.nextInt(seatedPlayers.size()));
					unseatPlayer(table, player);

					// Seat any unseated players
					seatUnseatedPlayers();
				} else {
					// Balance is achieved, as far as this table is concerned
					break;
				}
			}
		}

		// Seat any unseated players
		seatUnseatedPlayers();

		return tableStillExists;
	}

	private void seatUnseatedPlayers() {
		while (!unseatedPlayers.isEmpty()) {
			// Find the first table with the most open seats
			PokerTable table = tables.stream().sorted(Comparator.comparingInt(PokerTable::getNumberOfAvailableSeats))
					.findFirst().get();

			// Seat a random player
			Player player = unseatedPlayers.get(random.nextInt(unseatedPlayers.size()));
			seatPlayer(table, player);
		}
	}

	private void seatPlayer(PokerTable table, Player player) {
		// Seat the player
		table.seatPlayer(player);
		unseatedPlayers.remove(player);

		// Notify listeners
		listeners.forEach(listener -> listener.onPlayerSeated(table, player));
	}

	private void unseatPlayer(PokerTable table, Player player) {
		// Unseat the player
		table.unseatPlayer(player);
		unseatedPlayers.add(player);

		// Notify listeners
		listeners.forEach(listener -> listener.onPlayerUnseated(table, player));
	}
}
