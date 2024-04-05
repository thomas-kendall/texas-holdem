package poker.texasholdem.chips;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import poker.texasholdem.player.Player;

public class Pot {
	@Getter
	private String name; // "Main Pot", "Side Pot #1", etc.

	@Getter
	private List<Player> eligiblePlayers;

	private Map<Player, Integer> playerContributions;

	@Getter
	private int maxContribution; // 0 means no max

	public Pot(String name, Collection<Player> eligiblePlayers) {
		super();
		this.name = name;
		this.eligiblePlayers = new ArrayList<>(eligiblePlayers);
		this.playerContributions = new HashMap<>();
		this.maxContribution = 0;
	}

	public void removeEligiblePlayer(Player player) {
		if (eligiblePlayers.contains(player)) {
			if (eligiblePlayers.size() == 1) {
				throw new RuntimeException("The last eligible player cannot be removed from the pot.");
			}
			eligiblePlayers.remove(player);
		}
	}

	public int getSize() {
		return playerContributions.values().stream().reduce(0, Integer::sum);
	}

	public int getPlayerContribution(Player player) {
		return playerContributions.containsKey(player) ? playerContributions.get(player) : 0;
	}

	public void contribute(Player player, int chips) {
		playerContributions.put(player, chips);
	}

	public void setMaxContribution() {
		if (playerContributions.isEmpty()) {
			throw new RuntimeException("Cannot set max contribution without any contributions present.");
		}
		this.maxContribution = playerContributions.values().stream().mapToInt(Integer::intValue).max().getAsInt();
	}
}
