/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class Scoreboard {
    private final Map<String, ScoreboardObjective> objectives = Maps.newHashMap();
    private final Map<ScoreboardCriterion, List<ScoreboardObjective>> objectivesByCriterion = Maps.newHashMap();
    private final Map<String, Map<ScoreboardObjective, ScoreboardPlayerScore>> playerObjectives = Maps.newHashMap();
    private final ScoreboardObjective[] objectiveSlots = new ScoreboardObjective[19];
    private final Map<String, Team> teams = Maps.newHashMap();
    private final Map<String, Team> teamsByPlayer = Maps.newHashMap();
    private static String[] displaySlotNames;

    @Environment(value=EnvType.CLIENT)
    public boolean containsObjective(String string) {
        return this.objectives.containsKey(string);
    }

    public ScoreboardObjective getObjective(String string) {
        return this.objectives.get(string);
    }

    @Nullable
    public ScoreboardObjective getNullableObjective(@Nullable String string) {
        return this.objectives.get(string);
    }

    public ScoreboardObjective addObjective(String string, ScoreboardCriterion scoreboardCriterion2, Text text, ScoreboardCriterion.RenderType renderType) {
        if (string.length() > 16) {
            throw new IllegalArgumentException("The objective name '" + string + "' is too long!");
        }
        if (this.objectives.containsKey(string)) {
            throw new IllegalArgumentException("An objective with the name '" + string + "' already exists!");
        }
        ScoreboardObjective scoreboardObjective = new ScoreboardObjective(this, string, scoreboardCriterion2, text, renderType);
        this.objectivesByCriterion.computeIfAbsent(scoreboardCriterion2, scoreboardCriterion -> Lists.newArrayList()).add(scoreboardObjective);
        this.objectives.put(string, scoreboardObjective);
        this.updateObjective(scoreboardObjective);
        return scoreboardObjective;
    }

    public final void forEachScore(ScoreboardCriterion scoreboardCriterion, String string, Consumer<ScoreboardPlayerScore> consumer) {
        this.objectivesByCriterion.getOrDefault(scoreboardCriterion, Collections.emptyList()).forEach(scoreboardObjective -> consumer.accept(this.getPlayerScore(string, (ScoreboardObjective)scoreboardObjective)));
    }

    public boolean playerHasObjective(String string, ScoreboardObjective scoreboardObjective) {
        Map<ScoreboardObjective, ScoreboardPlayerScore> map = this.playerObjectives.get(string);
        if (map == null) {
            return false;
        }
        ScoreboardPlayerScore scoreboardPlayerScore = map.get(scoreboardObjective);
        return scoreboardPlayerScore != null;
    }

    public ScoreboardPlayerScore getPlayerScore(String string2, ScoreboardObjective scoreboardObjective2) {
        if (string2.length() > 40) {
            throw new IllegalArgumentException("The player name '" + string2 + "' is too long!");
        }
        Map map = this.playerObjectives.computeIfAbsent(string2, string -> Maps.newHashMap());
        return map.computeIfAbsent(scoreboardObjective2, scoreboardObjective -> {
            ScoreboardPlayerScore scoreboardPlayerScore = new ScoreboardPlayerScore(this, (ScoreboardObjective)scoreboardObjective, string2);
            scoreboardPlayerScore.setScore(0);
            return scoreboardPlayerScore;
        });
    }

    public Collection<ScoreboardPlayerScore> getAllPlayerScores(ScoreboardObjective scoreboardObjective) {
        ArrayList<ScoreboardPlayerScore> list = Lists.newArrayList();
        for (Map<ScoreboardObjective, ScoreboardPlayerScore> map : this.playerObjectives.values()) {
            ScoreboardPlayerScore scoreboardPlayerScore = map.get(scoreboardObjective);
            if (scoreboardPlayerScore == null) continue;
            list.add(scoreboardPlayerScore);
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
        return Lists.newArrayList(this.playerObjectives.keySet());
    }

    public void resetPlayerScore(String string, @Nullable ScoreboardObjective scoreboardObjective) {
        if (scoreboardObjective == null) {
            Map<ScoreboardObjective, ScoreboardPlayerScore> map = this.playerObjectives.remove(string);
            if (map != null) {
                this.updatePlayerScore(string);
            }
        } else {
            Map<ScoreboardObjective, ScoreboardPlayerScore> map = this.playerObjectives.get(string);
            if (map != null) {
                ScoreboardPlayerScore scoreboardPlayerScore = map.remove(scoreboardObjective);
                if (map.size() < 1) {
                    Map<ScoreboardObjective, ScoreboardPlayerScore> map2 = this.playerObjectives.remove(string);
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
        Map<ScoreboardObjective, ScoreboardPlayerScore> map = this.playerObjectives.get(string);
        if (map == null) {
            map = Maps.newHashMap();
        }
        return map;
    }

    public void removeObjective(ScoreboardObjective scoreboardObjective) {
        this.objectives.remove(scoreboardObjective.getName());
        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveForSlot(i) != scoreboardObjective) continue;
            this.setObjectiveSlot(i, null);
        }
        List<ScoreboardObjective> list = this.objectivesByCriterion.get(scoreboardObjective.getCriterion());
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
        return this.teams.get(string);
    }

    public Team addTeam(String string) {
        if (string.length() > 16) {
            throw new IllegalArgumentException("The team name '" + string + "' is too long!");
        }
        Team team = this.getTeam(string);
        if (team != null) {
            throw new IllegalArgumentException("A team with the name '" + string + "' already exists!");
        }
        team = new Team(this, string);
        this.teams.put(string, team);
        this.updateScoreboardTeamAndPlayers(team);
        return team;
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
        }
        if (this.getPlayerTeam(string) != null) {
            this.clearPlayerTeam(string);
        }
        this.teamsByPlayer.put(string, team);
        return team.getPlayerList().add(string);
    }

    public boolean clearPlayerTeam(String string) {
        Team team = this.getPlayerTeam(string);
        if (team != null) {
            this.removePlayerFromTeam(string, team);
            return true;
        }
        return false;
    }

    public void removePlayerFromTeam(String string, Team team) {
        if (this.getPlayerTeam(string) != team) {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + team.getName() + "'.");
        }
        this.teamsByPlayer.remove(string);
        team.getPlayerList().remove(string);
    }

    public Collection<String> getTeamNames() {
        return this.teams.keySet();
    }

    public Collection<Team> getTeams() {
        return this.teams.values();
    }

    @Nullable
    public Team getPlayerTeam(String string) {
        return this.teamsByPlayer.get(string);
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
        Formatting formatting;
        switch (i) {
            case 0: {
                return "list";
            }
            case 1: {
                return "sidebar";
            }
            case 2: {
                return "belowName";
            }
        }
        if (i >= 3 && i <= 18 && (formatting = Formatting.byColorIndex(i - 3)) != null && formatting != Formatting.RESET) {
            return "sidebar.team." + formatting.getName();
        }
        return null;
    }

    public static int getDisplaySlotId(String string) {
        String string2;
        Formatting formatting;
        if ("list".equalsIgnoreCase(string)) {
            return 0;
        }
        if ("sidebar".equalsIgnoreCase(string)) {
            return 1;
        }
        if ("belowName".equalsIgnoreCase(string)) {
            return 2;
        }
        if (string.startsWith("sidebar.team.") && (formatting = Formatting.byName(string2 = string.substring("sidebar.team.".length()))) != null && formatting.getColorIndex() >= 0) {
            return formatting.getColorIndex() + 3;
        }
        return -1;
    }

    public static String[] getDisplaySlotNames() {
        if (displaySlotNames == null) {
            displaySlotNames = new String[19];
            for (int i = 0; i < 19; ++i) {
                Scoreboard.displaySlotNames[i] = Scoreboard.getDisplaySlotName(i);
            }
        }
        return displaySlotNames;
    }

    public void resetEntityScore(Entity entity) {
        if (entity == null || entity instanceof PlayerEntity || entity.isAlive()) {
            return;
        }
        String string = entity.getUuidAsString();
        this.resetPlayerScore(string, null);
        this.clearPlayerTeam(string);
    }

    protected ListTag toTag() {
        ListTag listTag = new ListTag();
        this.playerObjectives.values().stream().map(Map::values).forEach(collection -> collection.stream().filter(scoreboardPlayerScore -> scoreboardPlayerScore.getObjective() != null).forEach(scoreboardPlayerScore -> {
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
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompoundTag(i);
            ScoreboardObjective scoreboardObjective = this.getObjective(compoundTag.getString("Objective"));
            String string = compoundTag.getString("Name");
            if (string.length() > 40) {
                string = string.substring(0, 40);
            }
            ScoreboardPlayerScore scoreboardPlayerScore = this.getPlayerScore(string, scoreboardObjective);
            scoreboardPlayerScore.setScore(compoundTag.getInt("Score"));
            if (!compoundTag.containsKey("Locked")) continue;
            scoreboardPlayerScore.setLocked(compoundTag.getBoolean("Locked"));
        }
    }
}

