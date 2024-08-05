package poker.texasholdem.tournament;

import java.util.ArrayList;
import java.util.List;

import poker.texasholdem.structure.BlindLevel;
import poker.texasholdem.structure.BlindStructure;
import poker.texasholdem.structure.Blinds;

public class StructureBuilder {

	private List<BlindLevel> blindLevels;

	public StructureBuilder() {
		this.blindLevels = new ArrayList<>();
	}

	public StructureBuilder withBlindLevel(int bigBlind, int smallBlind, int durationSeconds) {
		this.blindLevels.add(new BlindLevel(new Blinds(bigBlind, smallBlind), durationSeconds));
		return this;
	}

	public BlindStructure build() {
		BlindStructure structure = new BlindStructure();
		for (BlindLevel blindLevel : blindLevels) {
			structure.addBlindLevel(blindLevel);
		}
		return structure;
	}

}
