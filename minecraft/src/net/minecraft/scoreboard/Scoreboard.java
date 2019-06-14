package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Scoreboard {
	private final Map<String, ScoreboardObjective> objectives = Maps.<String, ScoreboardObjective>newHashMap();
	private final Map<ScoreboardCriterion, List<ScoreboardObjective>> objectivesByCriterion = Maps.<ScoreboardCriterion, List<ScoreboardObjective>>newHashMap();
	private final Map<String, Map<ScoreboardObjective, ScoreboardPlayerScore>> playerObjectives = Maps.<String, Map<ScoreboardObjective, ScoreboardPlayerScore>>newHashMap();
	private final ScoreboardObjective[] objectiveSlots = new ScoreboardObjective[19];
	private final Map<String, Team> teams = Maps.<String, Team>newHashMap();
	private final Map<String, Team> teamsByPlayer = Maps.<String, Team>newHashMap();
	private static String[] displaySlotNames;

	@Environment(EnvType.CLIENT)
	public boolean containsObjective(String string) {
		return this.objectives.containsKey(string);
	}

	public ScoreboardObjective getObjective(String string) {
		return (ScoreboardObjective)this.objectives.get(string);
	}

	@Nullable
	public ScoreboardObjective getNullableObjective(@Nullable String string) {
		return (ScoreboardObjective)this.objectives.get(string);
	}

	public ScoreboardObjective method_1168(String string, ScoreboardCriterion scoreboardCriterion, Text text, ScoreboardCriterion.RenderType renderType) {
		if (string.length() > 16) {
			throw new IllegalArgumentException("The objective name '" + string + "' is too long!");
		} else if (this.objectives.containsKey(string)) {
			throw new IllegalArgumentException("An objective with the name '" + string + "' already exists!");
		} else {
			ScoreboardObjective scoreboardObjective = new ScoreboardObjective(this, string, scoreboardCriterion, text, renderType);
			((List)this.objectivesByCriterion.computeIfAbsent(scoreboardCriterion, scoreboardCriterionx -> Lists.newArrayList())).add(scoreboardObjective);
			this.objectives.put(string, scoreboardObjective);
			this.updateObjective(scoreboardObjective);
			return scoreboardObjective;
		}
	}

	public final void method_1162(ScoreboardCriterion scoreboardCriterion, String string, Consumer<ScoreboardPlayerScore> consumer) {
		((List)this.objectivesByCriterion.getOrDefault(scoreboardCriterion, Collections.emptyList()))
			.forEach(scoreboardObjective -> consumer.accept(this.getPlayerScore(string, scoreboardObjective)));
	}

	public boolean playerHasObjective(String string, ScoreboardObjective scoreboardObjective) {
		Map<ScoreboardObjective, ScoreboardPlayerScore> map = (Map<ScoreboardObjective, ScoreboardPlayerScore>)this.playerObjectives.get(string);
		if (map == null) {
			return false;
		} else {
			ScoreboardPlayerScore scoreboardPlayerScore = (ScoreboardPlayerScore)map.get(scoreboardObjective);
			return scoreboardPlayerScore != null;
		}
	}

	public ScoreboardPlayerScore getPlayerScore(String string, ScoreboardObjective scoreboardObjective) {
		if (string.length() > 40) {
			throw new IllegalArgumentException("The player name '" + string + "' is too long!");
		} else {
			Map<ScoreboardObjective, ScoreboardPlayerScore> map = (Map<ScoreboardObjective, ScoreboardPlayerScore>)this.playerObjectives
				.computeIfAbsent(string, stringx -> Maps.newHashMap());
			return (ScoreboardPlayerScore)map.computeIfAbsent(scoreboardObjective, scoreboardObjectivex -> {
				ScoreboardPlayerScore scoreboardPlayerScore = new ScoreboardPlayerScore(this, scoreboardObjectivex, string);
				scoreboardPlayerScore.setScore(0);
				return scoreboardPlayerScore;
			});
		}
	}

	public Collection<ScoreboardPlayerScore> getAllPlayerScores(ScoreboardObjective scoreboardObjective) {
		List<ScoreboardPlayerScore> list = Lists.<ScoreboardPlayerScore>newArrayList();

		for (Map<ScoreboardObjective, ScoreboardPlayerScore> map : this.playerObjectives.values()) {
			ScoreboardPlayerScore scoreboardPlayerScore = (ScoreboardPlayerScore)map.get(scoreboardObjective);
			if (scoreboardPlayerScore != null) {
				list.add(scoreboardPlayerScore);
			}
		}

		Collections.sort(list, ScoreboardPlayerScore.COMPARATOR);
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

	public void resetPlayerScore(String string, @Nullable ScoreboardObjective scoreboardObjective) {
		if (scoreboardObjective == null) {
			Map<ScoreboardObjective, ScoreboardPlayerScore> map = (Map<ScoreboardObjective, ScoreboardPlayerScore>)this.playerObjectives.remove(string);
			if (map != null) {
				this.updatePlayerScore(string);
			}
		} else {
			Map<ScoreboardObjective, ScoreboardPlayerScore> map = (Map<ScoreboardObjective, ScoreboardPlayerScore>)this.playerObjectives.get(string);
			if (map != null) {
				ScoreboardPlayerScore scoreboardPlayerScore = (ScoreboardPlayerScore)map.remove(scoreboardObjective);
				if (map.size() < 1) {
					Map<ScoreboardObjective, ScoreboardPlayerScore> map2 = (Map<ScoreboardObjective, ScoreboardPlayerScore>)this.playerObjectives.remove(string);
					if (map2 != null) {
						this.updatePlayerScore(string);
					}
				} else if (scoreboardPlayerScore != null) {
					this.updatePlayerScore(string, scoreboardObjective);
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

	public void removeObjective(ScoreboardObjective scoreboardObjective) {
		this.objectives.remove(scoreboardObjective.getName());

		for (int i = 0; i < 19; i++) {
			if (this.getObjectiveForSlot(i) == scoreboardObjective) {
				this.setObjectiveSlot(i, null);
			}
		}

		List<ScoreboardObjective> list = (List<ScoreboardObjective>)this.objectivesByCriterion.get(scoreboardObjective.method_1116());
		if (list != null) {
			list.remove(scoreboardObjective);
		}

		for (Map<ScoreboardObjective, ScoreboardPlayerScore> map : this.playerObjectives.values()) {
			map.remove(scoreboardObjective);
		}

		this.updateRemovedObjective(scoreboardObjective);
	}

	public void setObjectiveSlot(int i, @Nullable ScoreboardObjective scoreboardObjective) {
		this.objectiveSlots[i] = scoreboardObjective;
	}

	@Nullable
	public ScoreboardObjective getObjectiveForSlot(int i) {
		return this.objectiveSlots[i];
	}

	public Team getTeam(String string) {
		return (Team)this.teams.get(string);
	}

	public Team addTeam(String string) {
		if (string.length() > 16) {
			throw new IllegalArgumentException("The team name '" + string + "' is too long!");
		} else {
			Team team = this.getTeam(string);
			if (team != null) {
				throw new IllegalArgumentException("A team with the name '" + string + "' already exists!");
			} else {
				team = new Team(this, string);
				this.teams.put(string, team);
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

	public boolean addPlayerToTeam(String string, Team team) {
		if (string.length() > 40) {
			throw new IllegalArgumentException("The player name '" + string + "' is too long!");
		} else {
			if (this.getPlayerTeam(string) != null) {
				this.clearPlayerTeam(string);
			}

			this.teamsByPlayer.put(string, team);
			return team.getPlayerList().add(string);
		}
	}

	public boolean clearPlayerTeam(String string) {
		Team team = this.getPlayerTeam(string);
		if (team != null) {
			this.removePlayerFromTeam(string, team);
			return true;
		} else {
			return false;
		}
	}

	public void removePlayerFromTeam(String string, Team team) {
		if (this.getPlayerTeam(string) != team) {
			throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + team.getName() + "'.");
		} else {
			this.teamsByPlayer.remove(string);
			team.getPlayerList().remove(string);
		}
	}

	public Collection<String> getTeamNames() {
		return this.teams.keySet();
	}

	public Collection<Team> getTeams() {
		return this.teams.values();
	}

	@Nullable
	public Team getPlayerTeam(String string) {
		return (Team)this.teamsByPlayer.get(string);
	}

	public void updateObjective(ScoreboardObjective scoreboardObjective) {
	}

	public void updateExistingObjective(ScoreboardObjective scoreboardObjective) {
	}

	public void updateRemovedObjective(ScoreboardObjective scoreboardObjective) {
	}

	public void updateScore(ScoreboardPlayerScore scoreboardPlayerScore) {
	}

	public void updatePlayerScore(String string) {
	}

	public void updatePlayerScore(String string, ScoreboardObjective scoreboardObjective) {
	}

	public void updateScoreboardTeamAndPlayers(Team team) {
	}

	public void updateScoreboardTeam(Team team) {
	}

	public void updateRemovedTeam(Team team) {
	}

	public static String getDisplaySlotName(int i) {
		switch (i) {
			case 0:
				return "list";
			case 1:
				return "sidebar";
			case 2:
				return "belowName";
			default:
				if (i >= 3 && i <= 18) {
					Formatting formatting = Formatting.byColorIndex(i - 3);
					if (formatting != null && formatting != Formatting.field_1070) {
						return "sidebar.team." + formatting.getName();
					}
				}

				return null;
		}
	}

	public static int getDisplaySlotId(String string) {
		if ("list".equalsIgnoreCase(string)) {
			return 0;
		} else if ("sidebar".equalsIgnoreCase(string)) {
			return 1;
		} else if ("belowName".equalsIgnoreCase(string)) {
			return 2;
		} else {
			if (string.startsWith("sidebar.team.")) {
				String string2 = string.substring("sidebar.team.".length());
				Formatting formatting = Formatting.byName(string2);
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

	protected ListTag toTag() {
		ListTag listTag = new ListTag();
		this.playerObjectives
			.values()
			.stream()
			.map(Map::values)
			.forEach(collection -> collection.stream().filter(scoreboardPlayerScore -> scoreboardPlayerScore.getObjective() != null).forEach(scoreboardPlayerScore -> {
					CompoundTag compoundTag = new CompoundTag();
					compoundTag.putString("Name", scoreboardPlayerScore.getPlayerName());
					compoundTag.putString("Objective", scoreboardPlayerScore.getObjective().getName());
					compoundTag.putInt("Score", scoreboardPlayerScore.getScore());
					compoundTag.putBoolean("Locked", scoreboardPlayerScore.isLocked());
					listTag.add(compoundTag);
				}));
		return listTag;
	}

	protected void fromTag(ListTag listTag) {
		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompoundTag(i);
			ScoreboardObjective scoreboardObjective = this.getObjective(compoundTag.getString("Objective"));
			String string = compoundTag.getString("Name");
			if (string.length() > 40) {
				string = string.substring(0, 40);
			}

			ScoreboardPlayerScore scoreboardPlayerScore = this.getPlayerScore(string, scoreboardObjective);
			scoreboardPlayerScore.setScore(compoundTag.getInt("Score"));
			if (compoundTag.containsKey("Locked")) {
				scoreboardPlayerScore.setLocked(compoundTag.getBoolean("Locked"));
			}
		}
	}
}
