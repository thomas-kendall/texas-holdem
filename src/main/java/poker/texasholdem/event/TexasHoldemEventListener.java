package poker.texasholdem.event;

public interface TexasHoldemEventListener {

	void onSmallBlindCollected(SmallBlindCollectedEvent e);

	void onBigBlindCollected(BigBlindCollectedEvent e);

	void onHoleCardsDealt(HoleCardsDealtEvent e);

	void onAwaitingPlayerAction(AwaitingPlayerActionEvent e);

	void onPlayerChecked(PlayerCheckedEvent e);

	void onPlayerBet(PlayerBetEvent e);

	void onPlayerCalled(PlayerCalledEvent e);

	void onPlayerRaised(PlayerRaisedEvent e);

	void onPlayerFolded(PlayerFoldedEvent e);

	void onBetsPulledIn(BetsPulledInEvent e);

	void onFlopDealt(FlopDealtEvent e);

	void onTurnDealt(TurnDealtEvent e);

	void onRiverDealt(RiverDealtEvent e);

	void onHandCompleted(HandCompletedEvent e);
}
