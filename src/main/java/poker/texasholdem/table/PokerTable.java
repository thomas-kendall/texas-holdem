package poker.texasholdem.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

import lombok.Getter;
import poker.texasholdem.player.Player;

public class PokerTable {
	@Getter
	private int id;

	@Getter
	private Player[] seats;

	@Getter
	private int buttonIndex;

	public PokerTable(int id, int numberOfSeats) {
		this.seats = new Player[numberOfSeats];
		this.buttonIndex = -1;
	}

	public List<Player> getPlayers() {
		return Stream.of(seats).filter(p -> p != null).toList();
	}

	public List<Player> getPlayersSmallBlindFirst() {
		List<Player> players = new ArrayList<>();
		int index = buttonIndex + 1;
		int numPlayers = getNumberOfSeatedPlayers();
		for (int i = 0; i < numPlayers; i++) {
			if (index >= seats.length) {
				index = 0;
			}
			players.add(seats[index]);
			index++;
		}
		return players;
	}

	public int getNumberOfSeats() {
		return seats.length;
	}

	public int getNumberOfAvailableSeats() {
		return (int) Stream.of(seats).filter(p -> p == null).count();
	}

	public int getNumberOfSeatedPlayers() {
		return (int) Stream.of(seats).filter(p -> p != null).count();
	}

	public boolean containsPlayer(Player player) {
		return Stream.of(seats).anyMatch(p -> player.equals(p));
	}

	public void seatPlayer(Player player) {
		List<Integer> openSeatIndices = new ArrayList<>();
		for (int i = 0; i < seats.length; i++) {
			if (seats[i] == null) {
				openSeatIndices.add(i);
			}
		}
		if (openSeatIndices.isEmpty()) {
			throw new RuntimeException("No seats available.");
		}

		int seatIndex = openSeatIndices.get(new Random().nextInt(openSeatIndices.size()));
		seats[seatIndex] = player;
	}

	public void unseatPlayer(Player player) {
		for (int i = 0; i < seats.length; i++) {
			if (player.equals(seats[i])) {
				seats[i] = null;
				break;
			}
		}
	}

	public void randomizeButton() {
		List<Integer> filledSeatIndices = new ArrayList<>();
		for (int i = 0; i < seats.length; i++) {
			if (seats[i] != null) {
				filledSeatIndices.add(i);
			}
		}
		if (filledSeatIndices.isEmpty()) {
			throw new RuntimeException("No players sitting at this table.");
		}
		buttonIndex = filledSeatIndices.get(new Random().nextInt(filledSeatIndices.size()));
	}

	public void moveButton() {
		buttonIndex++;
		if (buttonIndex >= seats.length) {
			buttonIndex = 0;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PokerTable other = (PokerTable) obj;
		return id == other.id;
	}

}
