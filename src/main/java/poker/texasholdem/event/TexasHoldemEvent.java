package poker.texasholdem.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class TexasHoldemEvent {

	@Getter
	private TexasHoldemEventType eventType;
}
