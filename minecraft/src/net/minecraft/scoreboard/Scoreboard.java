package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;

public class Scoreboard {
	public static final String field_47542 = "#";
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Map<String, ScoreboardObjective> objectives = Maps.<String, ScoreboardObjective>newHashMap();
	private final Map<ScoreboardCriterion, List<ScoreboardObjective>> objectivesByCriterion = Maps.<ScoreboardCriterion, List<ScoreboardObjective>>newHashMap();
	private final Map<String, Scores> scores = Maps.<String, Scores>newHashMap();
	private final Map<ScoreboardDisplaySlot, ScoreboardObjective> objectiveSlots = new EnumMap(ScoreboardDisplaySlot.class);
	private final Map<String, Team> teams = Maps.<String, Team>newHashMap();
	private final Map<String, Team> teamsByScoreHolder = Maps.<String, Team>newHashMap();

	@Nullable
	public ScoreboardObjective getNullableObjective(@Nullable String name) {
		return (ScoreboardObjective)this.objectives.get(name);
	}

	public ScoreboardObjective addObjective(
		String name,
		ScoreboardCriterion criterion,
		Text displayName,
		ScoreboardCriterion.RenderType renderType,
		boolean displayAutoUpdate,
		@Nullable NumberFormat numberFormat
	) {
		if (this.objectives.containsKey(name)) {
			throw new IllegalArgumentException("An objective with the name '" + name + "' already exists!");
		} else {
			ScoreboardObjective scoreboardObjective = new ScoreboardObjective(this, name, criterion, displayName, renderType, displayAutoUpdate, numberFormat);
			((List)this.objectivesByCriterion.computeIfAbsent(criterion, criterionx -> Lists.newArrayList())).add(scoreboardObjective);
			this.objectives.put(name, scoreboardObjective);
			this.updateObjective(scoreboardObjective);
			return scoreboardObjective;
		}
	}

	public final void forEachScore(ScoreboardCriterion criterion, ScoreHolder scoreHolder, Consumer<ScoreAccess> action) {
		((List)this.objectivesByCriterion.getOrDefault(criterion, Collections.emptyList()))
			.forEach(objective -> action.accept(this.getOrCreateScore(scoreHolder, objective, true)));
	}

	private Scores getScores(String scoreHolderName) {
		return (Scores)this.scores.computeIfAbsent(scoreHolderName, string -> new Scores());
	}

	public ScoreAccess getOrCreateScore(ScoreHolder scoreHolder, ScoreboardObjective objective) {
		return this.getOrCreateScore(scoreHolder, objective, false);
	}

	public ScoreAccess getOrCreateScore(ScoreHolder scoreHolder, ScoreboardObjective objective, boolean forceWritable) {
		final boolean bl = forceWritable || !objective.getCriterion().isReadOnly();
		Scores scores = this.getScores(scoreHolder.getNameForScoreboard());
		final MutableBoolean mutableBoolean = new MutableBoolean();
		final ScoreboardScore scoreboardScore = scores.getOrCreate(objective, scoreboardScorex -> mutableBoolean.setTrue());
		return new ScoreAccess() {
			@Override
			public int getScore() {
				return scoreboardScore.getScore();
			}

			@Override
			public void setScore(int score) {
				if (!bl) {
					throw new IllegalStateException("Cannot modify read-only score");
				} else {
					boolean bl = mutableBoolean.isTrue();
					if (objective.shouldDisplayAutoUpdate()) {
						Text text = scoreHolder.getDisplayName();
						if (text != null && !text.equals(scoreboardScore.getDisplayText())) {
							scoreboardScore.setDisplayText(text);
							bl = true;
						}
					}

					if (score != scoreboardScore.getScore()) {
						scoreboardScore.setScore(score);
						bl = true;
					}

					if (bl) {
						this.update();
					}
				}
			}

			@Nullable
			@Override
			public Text getDisplayText() {
				return scoreboardScore.getDisplayText();
			}

			@Override
			public void setDisplayText(@Nullable Text text) {
				if (mutableBoolean.isTrue() || !Objects.equals(text, scoreboardScore.getDisplayText())) {
					scoreboardScore.setDisplayText(text);
					this.update();
				}
			}

			@Override
			public void setNumberFormat(@Nullable NumberFormat numberFormat) {
				scoreboardScore.setNumberFormat(numberFormat);
				this.update();
			}

			@Override
			public boolean isLocked() {
				return scoreboardScore.isLocked();
			}

			@Override
			public void unlock() {
				this.setLocked(false);
			}

			@Override
			public void lock() {
				this.setLocked(true);
			}

			private void setLocked(boolean locked) {
				scoreboardScore.setLocked(locked);
				if (mutableBoolean.isTrue()) {
					this.update();
				}

				Scoreboard.this.resetScore(scoreHolder, objective);
			}

			private void update() {
				Scoreboard.this.updateScore(scoreHolder, objective, scoreboardScore);
				mutableBoolean.setFalse();
			}
		};
	}

	@Nullable
	public ReadableScoreboardScore getScore(ScoreHolder scoreHolder, ScoreboardObjective objective) {
		Scores scores = (Scores)this.scores.get(scoreHolder.getNameForScoreboard());
		return scores != null ? scores.get(objective) : null;
	}

	public Collection<ScoreboardEntry> getScoreboardEntries(ScoreboardObjective objective) {
		List<ScoreboardEntry> list = new ArrayList();
		this.scores.forEach((scoreHolderName, scores) -> {
			ScoreboardScore scoreboardScore = scores.get(objective);
			if (scoreboardScore != null) {
				list.add(new ScoreboardEntry(scoreHolderName, scoreboardScore.getScore(), scoreboardScore.getDisplayText(), scoreboardScore.getNumberFormat()));
			}
		});
		return list;
	}

	public Collection<ScoreboardObjective> getObjectives() {
		return this.objectives.values();
	}

	public Collection<String> getObjectiveNames() {
		return this.objectives.keySet();
	}

	public Collection<ScoreHolder> getKnownScoreHolders() {
		return this.scores.keySet().stream().map(ScoreHolder::fromName).toList();
	}

	public void removeScores(ScoreHolder scoreHolder) {
		Scores scores = (Scores)this.scores.remove(scoreHolder.getNameForScoreboard());
		if (scores != null) {
			this.onScoreHolderRemoved(scoreHolder);
		}
	}

	public void removeScore(ScoreHolder scoreHolder, ScoreboardObjective objective) {
		Scores scores = (Scores)this.scores.get(scoreHolder.getNameForScoreboard());
		if (scores != null) {
			boolean bl = scores.hasScore(objective);
			if (!scores.hasScores()) {
				Scores scores2 = (Scores)this.scores.remove(scoreHolder.getNameForScoreboard());
				if (scores2 != null) {
					this.onScoreHolderRemoved(scoreHolder);
				}
			} else if (bl) {
				this.onScoreRemoved(scoreHolder, objective);
			}
		}
	}

	public Object2IntMap<ScoreboardObjective> getScoreHolderObjectives(ScoreHolder scoreHolder) {
		Scores scores = (Scores)this.scores.get(scoreHolder.getNameForScoreboard());
		return scores != null ? scores.getScoresAsIntMap() : Object2IntMaps.emptyMap();
	}

	public void removeObjective(ScoreboardObjective objective) {
		this.objectives.remove(objective.getName());

		for (ScoreboardDisplaySlot scoreboardDisplaySlot : ScoreboardDisplaySlot.values()) {
			if (this.getObjectiveForSlot(scoreboardDisplaySlot) == objective) {
				this.setObjectiveSlot(scoreboardDisplaySlot, null);
			}
		}

		List<ScoreboardObjective> list = (List<ScoreboardObjective>)this.objectivesByCriterion.get(objective.getCriterion());
		if (list != null) {
			list.remove(objective);
		}

		for (Scores scores : this.scores.values()) {
			scores.hasScore(objective);
		}

		this.updateRemovedObjective(objective);
	}

	public void setObjectiveSlot(ScoreboardDisplaySlot slot, @Nullable ScoreboardObjective objective) {
		this.objectiveSlots.put(slot, objective);
	}

	@Nullable
	public ScoreboardObjective getObjectiveForSlot(ScoreboardDisplaySlot slot) {
		return (ScoreboardObjective)this.objectiveSlots.get(slot);
	}

	@Nullable
	public Team getTeam(String name) {
		return (Team)this.teams.get(name);
	}

	public Team addTeam(String name) {
		Team team = this.getTeam(name);
		if (team != null) {
			LOGGER.warn("Requested creation of existing team '{}'", name);
			return team;
		} else {
			team = new Team(this, name);
			this.teams.put(name, team);
			this.updateScoreboardTeamAndPlayers(team);
			return team;
		}
	}

	public void removeTeam(Team team) {
		this.teams.remove(team.getName());

		for (String string : team.getPlayerList()) {
			this.teamsByScoreHolder.remove(string);
		}

		this.updateRemovedTeam(team);
	}

	public boolean addScoreHolderToTeam(String scoreHolderName, Team team) {
		if (this.getScoreHolderTeam(scoreHolderName) != null) {
			this.clearTeam(scoreHolderName);
		}

		this.teamsByScoreHolder.put(scoreHolderName, team);
		return team.getPlayerList().add(scoreHolderName);
	}

	public boolean clearTeam(String scoreHolderName) {
		Team team = this.getScoreHolderTeam(scoreHolderName);
		if (team != null) {
			this.removeScoreHolderFromTeam(scoreHolderName, team);
			return true;
		} else {
			return false;
		}
	}

	public void removeScoreHolderFromTeam(String scoreHolderName, Team team) {
		if (this.getScoreHolderTeam(scoreHolderName) != team) {
			throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + team.getName() + "'.");
		} else {
			this.teamsByScoreHolder.remove(scoreHolderName);
			team.getPlayerList().remove(scoreHolderName);
		}
	}

	public Collection<String> getTeamNames() {
		return this.teams.keySet();
	}

	public Collection<Team> getTeams() {
		return this.teams.values();
	}

	@Nullable
	public Team getScoreHolderTeam(String scoreHolderName) {
		return (Team)this.teamsByScoreHolder.get(scoreHolderName);
	}

	public void updateObjective(ScoreboardObjective objective) {
	}

	public void updateExistingObjective(ScoreboardObjective objective) {
	}

	public void updateRemovedObjective(ScoreboardObjective objective) {
	}

	protected void updateScore(ScoreHolder scoreHolder, ScoreboardObjective objective, ScoreboardScore score) {
	}

	protected void resetScore(ScoreHolder scoreHolder, ScoreboardObjective objective) {
	}

	public void onScoreHolderRemoved(ScoreHolder scoreHolder) {
	}

	public void onScoreRemoved(ScoreHolder scoreHolder, ScoreboardObjective objective) {
	}

	public void updateScoreboardTeamAndPlayers(Team team) {
	}

	public void updateScoreboardTeam(Team team) {
	}

	public void updateRemovedTeam(Team team) {
	}

	public void clearDeadEntity(Entity entity) {
		if (!(entity instanceof PlayerEntity) && !entity.isAlive()) {
			this.removeScores(entity);
			this.clearTeam(entity.getNameForScoreboard());
		}
	}

	protected NbtList toNbt() {
		NbtList nbtList = new NbtList();
		this.scores.forEach((name, scores) -> scores.getScores().forEach((objective, score) -> {
				NbtCompound nbtCompound = score.toNbt();
				nbtCompound.putString("Name", name);
				nbtCompound.putString("Objective", objective.getName());
				nbtList.add(nbtCompound);
			}));
		return nbtList;
	}

	protected void readNbt(NbtList list) {
		for (int i = 0; i < list.size(); i++) {
			NbtCompound nbtCompound = list.getCompound(i);
			ScoreboardScore scoreboardScore = ScoreboardScore.fromNbt(nbtCompound);
			String string = nbtCompound.getString("Name");
			String string2 = nbtCompound.getString("Objective");
			ScoreboardObjective scoreboardObjective = this.getNullableObjective(string2);
			if (scoreboardObjective == null) {
				LOGGER.error("Unknown objective {} for name {}, ignoring", string2, string);
			} else {
				this.getScores(string).put(scoreboardObjective, scoreboardScore);
			}
		}
	}
}
