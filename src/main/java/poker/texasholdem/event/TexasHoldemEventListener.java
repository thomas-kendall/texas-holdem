package poker.texasholdem.event;

import poker.texasholdem.hand.HandController;

public interface TexasHoldemEventListener {

	void onSmallBlindCollected(HandController handController, SmallBlindCollectedEvent e);

	void onBigBlindCollected(HandController handController, BigBlindCollectedEvent e);

	void onHoleCardsDealt(HandController handController, HoleCardsDealtEvent e);

	void onAwaitingPlayerAction(HandController handController, AwaitingPlayerActionEvent e);

	void onPlayerChecked(HandController handController, PlayerCheckedEvent e);

	void onPlayerBet(HandController handController, PlayerBetEvent e);

	void onPlayerCalled(HandController handController, PlayerCalledEvent e);

	void onPlayerRaised(HandController handController, PlayerRaisedEvent e);

	void onPlayerFolded(HandController handController, PlayerFoldedEvent e);

	void onBetsPulledIn(HandController handController, BetsPulledInEvent e);

	void onFlopDealt(HandController handController, FlopDealtEvent e);

	void onTurnDealt(HandController handController, TurnDealtEvent e);

	void onRiverDealt(HandController handController, RiverDealtEvent e);

	void onHandCompleted(HandController handController, HandCompletedEvent e);
}
