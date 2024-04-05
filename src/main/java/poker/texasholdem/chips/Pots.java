package poker.texasholdem.chips;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;
import poker.texasholdem.bet.BettingRound;
import poker.texasholdem.bet.PlacedBet;
import poker.texasholdem.player.Player;

public class Pots {

	@Getter
	private List<Pot> pots;

	public Pots(List<Player> eligiblePlayers) {
		pots = new ArrayList<>();
		pots.add(new Pot("Main Pot", eligiblePlayers));
	}

	public void pullInBets(BettingRound bettingRound) {
		// Loop through the placed bets ordered by bet size
		bettingRound.getPlacedBets().stream().sorted(Comparator.comparing(PlacedBet::getBet)).forEach(pb -> {
			// Move chips to the pots in order (main pot first, then first side pot, etc.
			int chips = pb.getBet();
			while (chips > 0) {
				// Find the first pot where this player has not met the max player contribution
				for (Pot pot : pots) {
					int contribution;
					if (pot.getMaxContribution() == 0) {
						// Put all of the chips from this bet in this pot
						contribution = chips;
					} else if (pot.getPlayerContribution(pb.getPlayer()) < pot.getMaxContribution()) {
						// Put some of the chips from this bet, up to the max contribution
						contribution = Math.min(chips, pot.getMaxContribution());
					} else {
						// Player has already contributed the max to this pot
						contribution = 0;
					}
					if (contribution > 0) {
						// Contribute to the pot
						pot.contribute(pb.getPlayer(), contribution);
						chips -= contribution;
					}
				}
			}

			// Is the player all in?
			if (getCurrentPot().getEligiblePlayers().contains(pb.getPlayer()) && !pb.getPlayer().hasChips()) {
				// Set the max contribution of the current pot
				getCurrentPot().setMaxContribution();

				// Create the side pot
				createSidePot(getCurrentPot().getEligiblePlayers().stream().filter(ep -> !ep.equals(pb.getPlayer()))
						.toList());
			}

		});
	}

	public void removeEligiblePlayerFromAllPots(Player player) {
		for (Pot pot : pots) {
			pot.removeEligiblePlayer(player);
		}
	}

	public Pot getCurrentPot() {
		return pots.get(pots.size() - 1);
	}

	private void createSidePot(List<Player> eligiblePlayers) {
		pots.add(new Pot("Side Pot #" + pots.size(), eligiblePlayers));
	}

}
