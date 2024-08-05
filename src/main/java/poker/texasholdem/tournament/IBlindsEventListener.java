package poker.texasholdem.tournament;

import poker.texasholdem.structure.BlindLevel;

public interface IBlindsEventListener {

	void onBlindsChanged(BlindLevel newBlindLevel);
}
