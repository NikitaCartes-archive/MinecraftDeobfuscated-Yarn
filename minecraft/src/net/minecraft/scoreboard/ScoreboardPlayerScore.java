package net.minecraft.scoreboard;

import java.util.Comparator;
import javax.annotation.Nullable;

public class ScoreboardPlayerScore {
	public static final Comparator<ScoreboardPlayerScore> COMPARATOR = (scoreboardPlayerScore, scoreboardPlayerScore2) -> {
		if (scoreboardPlayerScore.getScore() > scoreboardPlayerScore2.getScore()) {
			return 1;
		} else {
			return scoreboardPlayerScore.getScore() < scoreboardPlayerScore2.getScore()
				? -1
				: scoreboardPlayerScore2.getPlayerName().compareToIgnoreCase(scoreboardPlayerScore.getPlayerName());
		}
	};
	private final Scoreboard field_1407;
	@Nullable
	private final ScoreboardObjective objective;
	private final String playerName;
	private int score;
	private boolean locked;
	private boolean forceUpdate;

	public ScoreboardPlayerScore(Scoreboard scoreboard, ScoreboardObjective scoreboardObjective, String string) {
		this.field_1407 = scoreboard;
		this.objective = scoreboardObjective;
		this.playerName = string;
		this.locked = true;
		this.forceUpdate = true;
	}

	public void incrementScore(int i) {
		if (this.objective.method_1116().isReadOnly()) {
			throw new IllegalStateException("Cannot modify read-only score");
		} else {
			this.setScore(this.getScore() + i);
		}
	}

	public void incrementScore() {
		this.incrementScore(1);
	}

	public int getScore() {
		return this.score;
	}

	public void clearScore() {
		this.setScore(0);
	}

	public void setScore(int i) {
		int j = this.score;
		this.score = i;
		if (j != i || this.forceUpdate) {
			this.forceUpdate = false;
			this.method_1122().updateScore(this);
		}
	}

	@Nullable
	public ScoreboardObjective getObjective() {
		return this.objective;
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public Scoreboard method_1122() {
		return this.field_1407;
	}

	public boolean isLocked() {
		return this.locked;
	}

	public void setLocked(boolean bl) {
		this.locked = bl;
	}
}
