package poker.texasholdem.event;

public enum TexasHoldemEventType {
// @formatter:off
	SMALL_BLIND_COLLECTED,
	BIG_BLIND_COLLECTED,
	HOLE_CARDS_DEALT,
	AWAITING_PLAYER_ACTION,
	PLAYER_CHECKED,
	PLAYER_BET,
	PLAYER_CALLED,
	PLAYER_RAISED,
	PLAYER_FOLDED,
	BETS_PULLED_IN,
	FLOP_DEALT,
	TURN_DEALT,
	RIVER_DEALT,
	HAND_COMPLETE,
// @formatter:on
}