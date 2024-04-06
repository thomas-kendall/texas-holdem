package poker.texasholdem.chips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import poker.texasholdem.player.Player;

public class Pot {
	@Getter
	private String name; // "Main Pot", "Side Pot #1", etc.

	private Map<Player, Integer> playerContributions;

	@Getter
	private int maxContribution; // 0 means no max

	public Pot(String name) {
		super();
		this.name = name;
		this.playerContributions = new HashMap<>();
		this.maxContribution = 0;
	}

	public int getSize() {
		return playerContributions.values().stream().reduce(0, Integer::sum);
	}

	public int getPlayerContribution(Player player) {
		return playerContributions.containsKey(player) ? playerContributions.get(player) : 0;
	}

	public void contribute(Player player, int chips) {
		int totalContribution = chips + getPlayerContribution(player);
		playerContributions.put(player, totalContribution);
	}

	public void setMaxContribution() {
		if (playerContributions.isEmpty()) {
			throw new RuntimeException("Cannot set max contribution without any contributions present.");
		}
		this.maxContribution = playerContributions.values().stream().mapToInt(Integer::intValue).max().getAsInt();
	}

	public List<Player> getPlayers() {
		return new ArrayList<>(playerContributions.keySet());
	}
}
