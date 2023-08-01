package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import org.slf4j.Logger;

public class Scoreboard {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Map<String, ScoreboardObjective> objectives = Maps.<String, ScoreboardObjective>newHashMap();
	private final Map<ScoreboardCriterion, List<ScoreboardObjective>> objectivesByCriterion = Maps.<ScoreboardCriterion, List<ScoreboardObjective>>newHashMap();
	private final Map<String, Map<ScoreboardObjective, ScoreboardPlayerScore>> playerObjectives = Maps.<String, Map<ScoreboardObjective, ScoreboardPlayerScore>>newHashMap();
	private final Map<ScoreboardDisplaySlot, ScoreboardObjective> objectiveSlots = new EnumMap(ScoreboardDisplaySlot.class);
	private final Map<String, Team> teams = Maps.<String, Team>newHashMap();
	private final Map<String, Team> teamsByPlayer = Maps.<String, Team>newHashMap();

	@Nullable
	public ScoreboardObjective getNullableObjective(@Nullable String name) {
		return (ScoreboardObjective)this.objectives.get(name);
	}

	public ScoreboardObjective addObjective(String name, ScoreboardCriterion criterion, Text displayName, ScoreboardCriterion.RenderType renderType) {
		if (this.objectives.containsKey(name)) {
			throw new IllegalArgumentException("An objective with the name '" + name + "' already exists!");
		} else {
			ScoreboardObjective scoreboardObjective = new ScoreboardObjective(this, name, criterion, displayName, renderType);
			((List)this.objectivesByCriterion.computeIfAbsent(criterion, criterionx -> Lists.newArrayList())).add(scoreboardObjective);
			this.objectives.put(name, scoreboardObjective);
			this.updateObjective(scoreboardObjective);
			return scoreboardObjective;
		}
	}

	public final void forEachScore(ScoreboardCriterion criterion, String player, Consumer<ScoreboardPlayerScore> action) {
		((List)this.objectivesByCriterion.getOrDefault(criterion, Collections.emptyList()))
			.forEach(objective -> action.accept(this.getPlayerScore(player, objective)));
	}

	public boolean playerHasObjective(String playerName, ScoreboardObjective objective) {
		Map<ScoreboardObjective, ScoreboardPlayerScore> map = (Map<ScoreboardObjective, ScoreboardPlayerScore>)this.playerObjectives.get(playerName);
		if (map == null) {
			return false;
		} else {
			ScoreboardPlayerScore scoreboardPlayerScore = (ScoreboardPlayerScore)map.get(objective);
			return scoreboardPlayerScore != null;
		}
	}

	public ScoreboardPlayerScore getPlayerScore(String playerName, ScoreboardObjective objective) {
		Map<ScoreboardObjective, ScoreboardPlayerScore> map = (Map<ScoreboardObjective, ScoreboardPlayerScore>)this.playerObjectives
			.computeIfAbsent(playerName, name -> Maps.newHashMap());
		return (ScoreboardPlayerScore)map.computeIfAbsent(objective, objectivex -> {
			ScoreboardPlayerScore scoreboardPlayerScore = new ScoreboardPlayerScore(this, objectivex, playerName);
			scoreboardPlayerScore.setScore(0);
			return scoreboardPlayerScore;
		});
	}

	public Collection<ScoreboardPlayerScore> getAllPlayerScores(ScoreboardObjective objective) {
		List<ScoreboardPlayerScore> list = Lists.<ScoreboardPlayerScore>newArrayList();

		for (Map<ScoreboardObjective, ScoreboardPlayerScore> map : this.playerObjectives.values()) {
			ScoreboardPlayerScore scoreboardPlayerScore = (ScoreboardPlayerScore)map.get(objective);
			if (scoreboardPlayerScore != null) {
				list.add(scoreboardPlayerScore);
			}
		}

		list.sort(ScoreboardPlayerScore.COMPARATOR);
		return list;
	}

	public Collection<ScoreboardObjective> getObjectives() {
		return this.objectives.values();
	}

	public Collection<String> getObjectiveNames() {
		return this.objectives.keySet();
	}

	public Collection<String> getKnownPlayers() {
		return Lists.<String>newArrayList(this.playerObjectives.keySet());
	}

	public void resetPlayerScore(String playerName, @Nullable ScoreboardObjective objective) {
		if (objective == null) {
			Map<ScoreboardObjective, ScoreboardPlayerScore> map = (Map<ScoreboardObjective, ScoreboardPlayerScore>)this.playerObjectives.remove(playerName);
			if (map != null) {
				this.updatePlayerScore(playerName);
			}
		} else {
			Map<ScoreboardObjective, ScoreboardPlayerScore> map = (Map<ScoreboardObjective, ScoreboardPlayerScore>)this.playerObjectives.get(playerName);
			if (map != null) {
				ScoreboardPlayerScore scoreboardPlayerScore = (ScoreboardPlayerScore)map.remove(objective);
				if (map.size() < 1) {
					Map<ScoreboardObjective, ScoreboardPlayerScore> map2 = (Map<ScoreboardObjective, ScoreboardPlayerScore>)this.playerObjectives.remove(playerName);
					if (map2 != null) {
						this.updatePlayerScore(playerName);
					}
				} else if (scoreboardPlayerScore != null) {
					this.updatePlayerScore(playerName, objective);
				}
			}
		}
	}

	public Map<ScoreboardObjective, ScoreboardPlayerScore> getPlayerObjectives(String playerName) {
		Map<ScoreboardObjective, ScoreboardPlayerScore> map = (Map<ScoreboardObjective, ScoreboardPlayerScore>)this.playerObjectives.get(playerName);
		if (map == null) {
			map = Maps.<ScoreboardObjective, ScoreboardPlayerScore>newHashMap();
		}

		return map;
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

		for (Map<ScoreboardObjective, ScoreboardPlayerScore> map : this.playerObjectives.values()) {
			map.remove(objective);
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
			this.teamsByPlayer.remove(string);
		}

		this.updateRemovedTeam(team);
	}

	public boolean addPlayerToTeam(String playerName, Team team) {
		if (this.getPlayerTeam(playerName) != null) {
			this.clearPlayerTeam(playerName);
		}

		this.teamsByPlayer.put(playerName, team);
		return team.getPlayerList().add(playerName);
	}

	public boolean clearPlayerTeam(String playerName) {
		Team team = this.getPlayerTeam(playerName);
		if (team != null) {
			this.removePlayerFromTeam(playerName, team);
			return true;
		} else {
			return false;
		}
	}

	public void removePlayerFromTeam(String playerName, Team team) {
		if (this.getPlayerTeam(playerName) != team) {
			throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + team.getName() + "'.");
		} else {
			this.teamsByPlayer.remove(playerName);
			team.getPlayerList().remove(playerName);
		}
	}

	public Collection<String> getTeamNames() {
		return this.teams.keySet();
	}

	public Collection<Team> getTeams() {
		return this.teams.values();
	}

	@Nullable
	public Team getPlayerTeam(String playerName) {
		return (Team)this.teamsByPlayer.get(playerName);
	}

	public void updateObjective(ScoreboardObjective objective) {
	}

	public void updateExistingObjective(ScoreboardObjective objective) {
	}

	public void updateRemovedObjective(ScoreboardObjective objective) {
	}

	public void updateScore(ScoreboardPlayerScore score) {
	}

	public void updatePlayerScore(String playerName) {
	}

	public void updatePlayerScore(String playerName, ScoreboardObjective objective) {
	}

	public void updateScoreboardTeamAndPlayers(Team team) {
	}

	public void updateScoreboardTeam(Team team) {
	}

	public void updateRemovedTeam(Team team) {
	}

	public void resetEntityScore(Entity entity) {
		if (!(entity instanceof PlayerEntity) && !entity.isAlive()) {
			String string = entity.getUuidAsString();
			this.resetPlayerScore(string, null);
			this.clearPlayerTeam(string);
		}
	}

	protected NbtList toNbt() {
		NbtList nbtList = new NbtList();
		this.playerObjectives.values().stream().map(Map::values).forEach(scores -> scores.forEach(score -> {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putString("Name", score.getPlayerName());
				nbtCompound.putString("Objective", score.getObjective().getName());
				nbtCompound.putInt("Score", score.getScore());
				nbtCompound.putBoolean("Locked", score.isLocked());
				nbtList.add(nbtCompound);
			}));
		return nbtList;
	}

	protected void readNbt(NbtList list) {
		for (int i = 0; i < list.size(); i++) {
			NbtCompound nbtCompound = list.getCompound(i);
			String string = nbtCompound.getString("Name");
			String string2 = nbtCompound.getString("Objective");
			ScoreboardObjective scoreboardObjective = this.getNullableObjective(string2);
			if (scoreboardObjective == null) {
				LOGGER.error("Unknown objective {} for name {}, ignoring", string2, string);
			} else {
				ScoreboardPlayerScore scoreboardPlayerScore = this.getPlayerScore(string, scoreboardObjective);
				scoreboardPlayerScore.setScore(nbtCompound.getInt("Score"));
				if (nbtCompound.contains("Locked")) {
					scoreboardPlayerScore.setLocked(nbtCompound.getBoolean("Locked"));
				}
			}
		}
	}
}
