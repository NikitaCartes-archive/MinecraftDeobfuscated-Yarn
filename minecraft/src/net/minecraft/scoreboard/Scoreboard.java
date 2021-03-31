package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Scoreboard {
	public static final int field_31886 = 0;
	public static final int field_31887 = 1;
	public static final int field_31888 = 2;
	public static final int field_31889 = 3;
	public static final int field_31890 = 18;
	public static final int field_31891 = 19;
	public static final int field_31892 = 40;
	private final Map<String, ScoreboardObjective> objectives = Maps.<String, ScoreboardObjective>newHashMap();
	private final Map<ScoreboardCriterion, List<ScoreboardObjective>> objectivesByCriterion = Maps.<ScoreboardCriterion, List<ScoreboardObjective>>newHashMap();
	private final Map<String, Map<ScoreboardObjective, ScoreboardPlayerScore>> playerObjectives = Maps.<String, Map<ScoreboardObjective, ScoreboardPlayerScore>>newHashMap();
	private final ScoreboardObjective[] objectiveSlots = new ScoreboardObjective[19];
	private final Map<String, Team> teams = Maps.<String, Team>newHashMap();
	private final Map<String, Team> teamsByPlayer = Maps.<String, Team>newHashMap();
	private static String[] displaySlotNames;

	public boolean containsObjective(String name) {
		return this.objectives.containsKey(name);
	}

	public ScoreboardObjective getObjective(String name) {
		return (ScoreboardObjective)this.objectives.get(name);
	}

	@Nullable
	public ScoreboardObjective getNullableObjective(@Nullable String name) {
		return (ScoreboardObjective)this.objectives.get(name);
	}

	public ScoreboardObjective addObjective(String name, ScoreboardCriterion criterion, Text displayName, ScoreboardCriterion.RenderType renderType) {
		if (name.length() > 16) {
			throw new IllegalArgumentException("The objective name '" + name + "' is too long!");
		} else if (this.objectives.containsKey(name)) {
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

	public ScoreboardPlayerScore getPlayerScore(String player, ScoreboardObjective objective) {
		if (player.length() > 40) {
			throw new IllegalArgumentException("The player name '" + player + "' is too long!");
		} else {
			Map<ScoreboardObjective, ScoreboardPlayerScore> map = (Map<ScoreboardObjective, ScoreboardPlayerScore>)this.playerObjectives
				.computeIfAbsent(player, string -> Maps.newHashMap());
			return (ScoreboardPlayerScore)map.computeIfAbsent(objective, objectivex -> {
				ScoreboardPlayerScore scoreboardPlayerScore = new ScoreboardPlayerScore(this, objectivex, player);
				scoreboardPlayerScore.setScore(0);
				return scoreboardPlayerScore;
			});
		}
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

	public Map<ScoreboardObjective, ScoreboardPlayerScore> getPlayerObjectives(String string) {
		Map<ScoreboardObjective, ScoreboardPlayerScore> map = (Map<ScoreboardObjective, ScoreboardPlayerScore>)this.playerObjectives.get(string);
		if (map == null) {
			map = Maps.<ScoreboardObjective, ScoreboardPlayerScore>newHashMap();
		}

		return map;
	}

	public void removeObjective(ScoreboardObjective objective) {
		this.objectives.remove(objective.getName());

		for (int i = 0; i < 19; i++) {
			if (this.getObjectiveForSlot(i) == objective) {
				this.setObjectiveSlot(i, null);
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

	public void setObjectiveSlot(int slot, @Nullable ScoreboardObjective objective) {
		this.objectiveSlots[slot] = objective;
	}

	@Nullable
	public ScoreboardObjective getObjectiveForSlot(int slot) {
		return this.objectiveSlots[slot];
	}

	public Team getTeam(String name) {
		return (Team)this.teams.get(name);
	}

	public Team addTeam(String name) {
		if (name.length() > 16) {
			throw new IllegalArgumentException("The team name '" + name + "' is too long!");
		} else {
			Team team = this.getTeam(name);
			if (team != null) {
				throw new IllegalArgumentException("A team with the name '" + name + "' already exists!");
			} else {
				team = new Team(this, name);
				this.teams.put(name, team);
				this.updateScoreboardTeamAndPlayers(team);
				return team;
			}
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
		if (playerName.length() > 40) {
			throw new IllegalArgumentException("The player name '" + playerName + "' is too long!");
		} else {
			if (this.getPlayerTeam(playerName) != null) {
				this.clearPlayerTeam(playerName);
			}

			this.teamsByPlayer.put(playerName, team);
			return team.getPlayerList().add(playerName);
		}
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

	public static String getDisplaySlotName(int slotId) {
		switch (slotId) {
			case 0:
				return "list";
			case 1:
				return "sidebar";
			case 2:
				return "belowName";
			default:
				if (slotId >= 3 && slotId <= 18) {
					Formatting formatting = Formatting.byColorIndex(slotId - 3);
					if (formatting != null && formatting != Formatting.RESET) {
						return "sidebar.team." + formatting.getName();
					}
				}

				return null;
		}
	}

	public static int getDisplaySlotId(String slotName) {
		if ("list".equalsIgnoreCase(slotName)) {
			return 0;
		} else if ("sidebar".equalsIgnoreCase(slotName)) {
			return 1;
		} else if ("belowName".equalsIgnoreCase(slotName)) {
			return 2;
		} else {
			if (slotName.startsWith("sidebar.team.")) {
				String string = slotName.substring("sidebar.team.".length());
				Formatting formatting = Formatting.byName(string);
				if (formatting != null && formatting.getColorIndex() >= 0) {
					return formatting.getColorIndex() + 3;
				}
			}

			return -1;
		}
	}

	public static String[] getDisplaySlotNames() {
		if (displaySlotNames == null) {
			displaySlotNames = new String[19];

			for (int i = 0; i < 19; i++) {
				displaySlotNames[i] = getDisplaySlotName(i);
			}
		}

		return displaySlotNames;
	}

	public void resetEntityScore(Entity entity) {
		if (entity != null && !(entity instanceof PlayerEntity) && !entity.isAlive()) {
			String string = entity.getUuidAsString();
			this.resetPlayerScore(string, null);
			this.clearPlayerTeam(string);
		}
	}

	protected NbtList toNbt() {
		NbtList nbtList = new NbtList();
		this.playerObjectives
			.values()
			.stream()
			.map(Map::values)
			.forEach(collection -> collection.stream().filter(score -> score.getObjective() != null).forEach(score -> {
					NbtCompound nbtCompound = new NbtCompound();
					nbtCompound.putString("Name", score.getPlayerName());
					nbtCompound.putString("Objective", score.getObjective().getName());
					nbtCompound.putInt("Score", score.getScore());
					nbtCompound.putBoolean("Locked", score.isLocked());
					nbtList.add(nbtCompound);
				}));
		return nbtList;
	}

	protected void readNbt(NbtList nbtList) {
		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			ScoreboardObjective scoreboardObjective = this.getObjective(nbtCompound.getString("Objective"));
			String string = nbtCompound.getString("Name");
			if (string.length() > 40) {
				string = string.substring(0, 40);
			}

			ScoreboardPlayerScore scoreboardPlayerScore = this.getPlayerScore(string, scoreboardObjective);
			scoreboardPlayerScore.setScore(nbtCompound.getInt("Score"));
			if (nbtCompound.contains("Locked")) {
				scoreboardPlayerScore.setLocked(nbtCompound.getBoolean("Locked"));
			}
		}
	}
}
