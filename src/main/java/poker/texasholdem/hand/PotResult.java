package poker.texasholdem.hand;

import lombok.AllArgsConstructor;
import lombok.Getter;
import poker.texasholdem.player.Player;

@AllArgsConstructor
public class PotResult {

	@Getter
	private String potName;

	@Getter
	private Player player;

	@Getter
	private int chipsWon;
}
