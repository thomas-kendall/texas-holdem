package poker.texasholdem.event;

import java.util.List;

import lombok.Getter;
import poker.texasholdem.chips.Pot;

public class BetsPulledInEvent extends TexasHoldemEvent {

	@Getter
	private List<Pot> pots;

	public BetsPulledInEvent(List<Pot> pots) {
		super(TexasHoldemEventType.BETS_PULLED_IN);
		this.pots = pots;
	}
}
