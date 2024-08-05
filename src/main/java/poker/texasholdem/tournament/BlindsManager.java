package poker.texasholdem.tournament;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import poker.texasholdem.structure.BlindLevel;
import poker.texasholdem.structure.BlindStructure;

public class BlindsManager {
	private List<IBlindsEventListener> listeners;
	private BlindStructure structure;
	private int currentBlindLevelIndex;
	private LocalDateTime levelStartTime;
	private Timer timer;

	public BlindsManager(BlindStructure structure) {
		this.structure = structure;
		this.currentBlindLevelIndex = -1;
		this.levelStartTime = null;
		this.timer = new Timer("BlindLevelTimer");
	}

	public void addEventListener(IBlindsEventListener listener) {
		if (listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeEventListener(IBlindsEventListener listener) {
		listeners.remove(listener);
	}

	public BlindLevel getCurrentBlindLevel() {
		return currentBlindLevelIndex < 0 ? null : structure.getBlindLevels().get(currentBlindLevelIndex);
	}

	public void start() {
		incrementBlinds();
	}

	public void stop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public Duration durationLeftInCurrentBlindLevel() {
		return levelStartTime == null ? Duration.ZERO : Duration.between(levelStartTime, LocalDateTime.now());
	}

	private void incrementBlinds() {
		if (currentBlindLevelIndex < structure.getBlindLevels().size() - 1) {
			currentBlindLevelIndex++;
			levelStartTime = LocalDateTime.now();

			// Notify listeners
			listeners.forEach(listener -> listener.onBlindsChanged(getCurrentBlindLevel()));
		}

		if (getCurrentBlindLevel().getDurationSeconds() > 0) {
			TimerTask timerTask = new TimerTask() {

				@Override
				public void run() {
					incrementBlinds();
				}
			};
			timer.schedule(timerTask, 1000 * getCurrentBlindLevel().getDurationSeconds());
		}
	}
}
