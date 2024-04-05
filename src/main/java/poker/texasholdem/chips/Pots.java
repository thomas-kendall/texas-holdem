package poker.texasholdem.chips;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;
import poker.texasholdem.bet.BettingRound;
import poker.texasholdem.bet.PlacedBet;

public class Pots {

	@Getter
	private List<Pot> pots;

	public Pots() {
		pots = new ArrayList<>();
		pots.add(new Pot("Main Pot"));
	}

	public void pullInBets(BettingRound bettingRound) {
		// Loop through the placed bets ordered by bet size
		bettingRound.getPlacedBets().stream().sorted(Comparator.comparing(PlacedBet::getBet)).forEach(pb -> {
			// Move chips to the pots in order (main pot first, then first side pot, etc.
			int chips = pb.getBet();

			// Contribute to the pots in order
			for (int i = 0; i < pots.size(); i++) {
				Pot pot = pots.get(i);

				// Calculate the contribution to this pot
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

				// Contribute
				if (contribution > 0) {
					// Contribute to the pot
					pot.contribute(pb.getPlayer(), contribution);
					chips -= contribution;
				}

				// If player is all in, set the max contribution
				if (pot.getMaxContribution() == 0 && !pb.getPlayer().hasChips()) {
					pot.setMaxContribution();
				}

				if (chips == 0) {
					break;
				}
			}

			// If the player still has chips but there is no pot to contribute to, create a
			// side pot
			if (chips > 0) {
				// Create the side pot
				createSidePot();

				// Contribute the rest to it
				getCurrentPot().contribute(pb.getPlayer(), chips);

				// If player is all in, set the max contribution
				if (getCurrentPot().getMaxContribution() == 0 && !pb.getPlayer().hasChips()) {
					getCurrentPot().setMaxContribution();
				}
			}
		});
	}

	public Pot getCurrentPot() {
		return pots.get(pots.size() - 1);
	}

	private void createSidePot() {
		pots.add(new Pot("Side Pot #" + pots.size()));
	}

}
