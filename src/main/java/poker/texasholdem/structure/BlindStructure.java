package poker.texasholdem.structure;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class BlindStructure {
	private List<BlindLevel> blindLevels;

	public BlindStructure() {
		blindLevels = new ArrayList<>();
	}

	public void addBlindLevel(BlindLevel blindLevel) {
		blindLevels.add(blindLevel);
	}
}
