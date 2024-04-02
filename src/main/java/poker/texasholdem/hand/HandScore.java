package poker.texasholdem.hand;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class HandScore implements Comparable<HandScore> {

	@Getter
	HandClassification handClassification;

	// The score is used to break ties when they are the same classification
	int score;

	@Override
	public int compareTo(HandScore hs) {
		if (this.getHandClassification() == hs.getHandClassification()) {
			return Integer.compare(this.score, hs.score);
		}
		return Integer.compare(this.getHandClassification().getValue(), hs.getHandClassification().getValue());
	}

	@Override
	public String toString() {
		return handClassification.toString() + " (" + score + ")";
	}
}
