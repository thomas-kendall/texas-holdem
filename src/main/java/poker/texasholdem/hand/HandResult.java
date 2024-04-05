package poker.texasholdem.hand;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class HandResult {

	@Getter
	private List<PotResult> potResults;
}
