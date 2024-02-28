package net.minecraft.scoreboard;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;

class Scores {
	private final Reference2ObjectOpenHashMap<ScoreboardObjective, ScoreboardScore> scores = new Reference2ObjectOpenHashMap<>(16, 0.5F);

	@Nullable
	public ScoreboardScore get(ScoreboardObjective objective) {
		return this.scores.get(objective);
	}

	public ScoreboardScore getOrCreate(ScoreboardObjective objective, Consumer<ScoreboardScore> scoreConsumer) {
		return this.scores.computeIfAbsent(objective, objective2 -> {
			ScoreboardScore scoreboardScore = new ScoreboardScore();
			scoreConsumer.accept(scoreboardScore);
			return scoreboardScore;
		});
	}

	public boolean remove(ScoreboardObjective objective) {
		return this.scores.remove(objective) != null;
	}

	public boolean hasScores() {
		return !this.scores.isEmpty();
	}

	public Object2IntMap<ScoreboardObjective> getScoresAsIntMap() {
		Object2IntMap<ScoreboardObjective> object2IntMap = new Object2IntOpenHashMap<>();
		this.scores.forEach((objective, score) -> object2IntMap.put(objective, score.getScore()));
		return object2IntMap;
	}

	void put(ScoreboardObjective objective, ScoreboardScore score) {
		this.scores.put(objective, score);
	}

	Map<ScoreboardObjective, ScoreboardScore> getScores() {
		return Collections.unmodifiableMap(this.scores);
	}
}
