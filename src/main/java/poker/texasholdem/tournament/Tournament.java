package poker.texasholdem.tournament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import poker.texasholdem.event.AwaitingPlayerActionEvent;
import poker.texasholdem.event.BetsPulledInEvent;
import poker.texasholdem.event.BigBlindCollectedEvent;
import poker.texasholdem.event.FlopDealtEvent;
import poker.texasholdem.event.HandCompletedEvent;
import poker.texasholdem.event.HoleCardsDealtEvent;
import poker.texasholdem.event.PlayerBetEvent;
import poker.texasholdem.event.PlayerCalledEvent;
import poker.texasholdem.event.PlayerCheckedEvent;
import poker.texasholdem.event.PlayerFoldedEvent;
import poker.texasholdem.event.PlayerRaisedEvent;
import poker.texasholdem.event.RiverDealtEvent;
import poker.texasholdem.event.SmallBlindCollectedEvent;
import poker.texasholdem.event.TexasHoldemEventListener;
import poker.texasholdem.event.TurnDealtEvent;
import poker.texasholdem.hand.HandController;
import poker.texasholdem.player.Player;
import poker.texasholdem.structure.BlindLevel;
import poker.texasholdem.table.PokerTable;

@Getter
public class Tournament implements IBlindsEventListener, ITableEventListener {
	private String name;
	private TournamentStatus status;
	private List<Player> registeredPlayers;
	private int startingStackSize;
	private BlindsManager blindsManager;
	private TableManager tableManager;
	private Map<PokerTable, TexasHoldemEventListener> tableHandListeners;

	public Tournament(String name, int startingStackSize, BlindsManager blindsManager, TableManager tableManager) {
		this.name = name;
		this.startingStackSize = startingStackSize;

		this.blindsManager = blindsManager;
		blindsManager.addEventListener(this);

		this.tableManager = tableManager;
		tableManager.addEventListener(this);

		this.status = TournamentStatus.OPEN;
		this.registeredPlayers = new ArrayList<>();
		this.tableHandListeners = new HashMap<>();
	}

	public void registerPlayer(Player player) {
		registeredPlayers.add(player);
		tableManager.addPlayer(player);
	}

	public void start() {
		// Create tables and seat the players
		tableManager.createTables();

		// Start the blinds
		blindsManager.start();

		// Create the hand controller at each table and subscribe to events
		for (PokerTable table : tableManager.getTables()) {
			HandController handController = new HandController(blindsManager.getCurrentBlindLevel().getBlinds(),
					table.getPlayersSmallBlindFirst());
			handController.addEventListener(tableHandListeners.get(table));
		}
	}

	@Override
	public void onBlindsChanged(BlindLevel newBlindLevel) {
	}

	@Override
	public void onTableCreated(PokerTable table) {
		tableHandListeners.put(table, new TexasHoldemEventListener() {

			@Override
			public void onTurnDealt(HandController handController, TurnDealtEvent e) {
			}

			@Override
			public void onSmallBlindCollected(HandController handController, SmallBlindCollectedEvent e) {
			}

			@Override
			public void onRiverDealt(HandController handController, RiverDealtEvent e) {
			}

			@Override
			public void onPlayerRaised(HandController handController, PlayerRaisedEvent e) {
			}

			@Override
			public void onPlayerFolded(HandController handController, PlayerFoldedEvent e) {
			}

			@Override
			public void onPlayerChecked(HandController handController, PlayerCheckedEvent e) {
			}

			@Override
			public void onPlayerCalled(HandController handController, PlayerCalledEvent e) {
			}

			@Override
			public void onPlayerBet(HandController handController, PlayerBetEvent e) {
			}

			@Override
			public void onHoleCardsDealt(HandController handController, HoleCardsDealtEvent e) {
			}

			@Override
			public void onHandCompleted(HandController handController, HandCompletedEvent e) {
				handController.removeEventListener(this);
				boolean tableStillExists = tableManager.onHandComplete(table);
				if (tableStillExists) {
					// Move the button
					table.moveButton();

					// Start the next hand
					HandController nextHand = new HandController(blindsManager.getCurrentBlindLevel().getBlinds(),
							table.getPlayersSmallBlindFirst());
					handController.addEventListener(tableHandListeners.get(table));
				}
			}

			@Override
			public void onFlopDealt(HandController handController, FlopDealtEvent e) {
			}

			@Override
			public void onBigBlindCollected(HandController handController, BigBlindCollectedEvent e) {
			}

			@Override
			public void onBetsPulledIn(HandController handController, BetsPulledInEvent e) {
			}

			@Override
			public void onAwaitingPlayerAction(HandController handController, AwaitingPlayerActionEvent e) {
			}
		});
	}

	@Override
	public void onTableRemoved(PokerTable table) {
		tableHandListeners.remove(table);
	}

	@Override
	public void onPlayerSeated(PokerTable table, Player player) {
	}

	@Override
	public void onPlayerUnseated(PokerTable table, Player player) {
	}
}
