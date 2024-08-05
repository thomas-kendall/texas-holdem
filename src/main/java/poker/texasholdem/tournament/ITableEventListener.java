package poker.texasholdem.tournament;

import poker.texasholdem.player.Player;
import poker.texasholdem.table.PokerTable;

public interface ITableEventListener {
	void onTableCreated(PokerTable table);

	void onTableRemoved(PokerTable table);

	void onPlayerSeated(PokerTable table, Player player);

	void onPlayerUnseated(PokerTable table, Player player);
}
