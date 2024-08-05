package poker.texasholdem.structure;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BlindLevel {
	private Blinds blinds;
	private int durationSeconds; // -1 means it goes until the tournament is over
}
