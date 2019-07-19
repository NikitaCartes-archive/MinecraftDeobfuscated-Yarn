/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.scoreboard;

import java.util.Collection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.PersistentState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreboardState
extends PersistentState {
    private static final Logger LOGGER = LogManager.getLogger();
    private Scoreboard scoreboard;
    private CompoundTag tag;

    public ScoreboardState() {
        super("scoreboard");
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        if (this.tag != null) {
            this.fromTag(this.tag);
        }
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        if (this.scoreboard == null) {
            this.tag = compoundTag;
            return;
        }
        this.deserializeObjectives(compoundTag.getList("Objectives", 10));
        this.scoreboard.fromTag(compoundTag.getList("PlayerScores", 10));
        if (compoundTag.contains("DisplaySlots", 10)) {
            this.deserializeDisplaySlots(compoundTag.getCompound("DisplaySlots"));
        }
        if (compoundTag.contains("Teams", 9)) {
            this.deserializeTeams(compoundTag.getList("Teams", 10));
        }
    }

    protected void deserializeTeams(ListTag listTag) {
        for (int i = 0; i < listTag.size(); ++i) {
            AbstractTeam.CollisionRule collisionRule;
            AbstractTeam.VisibilityRule visibilityRule;
            Text text2;
            CompoundTag compoundTag = listTag.getCompound(i);
            String string = compoundTag.getString("Name");
            if (string.length() > 16) {
                string = string.substring(0, 16);
            }
            Team team = this.scoreboard.addTeam(string);
            Text text = Text.Serializer.fromJson(compoundTag.getString("DisplayName"));
            if (text != null) {
                team.setDisplayName(text);
            }
            if (compoundTag.contains("TeamColor", 8)) {
                team.setColor(Formatting.byName(compoundTag.getString("TeamColor")));
            }
            if (compoundTag.contains("AllowFriendlyFire", 99)) {
                team.setFriendlyFireAllowed(compoundTag.getBoolean("AllowFriendlyFire"));
            }
            if (compoundTag.contains("SeeFriendlyInvisibles", 99)) {
                team.setShowFriendlyInvisibles(compoundTag.getBoolean("SeeFriendlyInvisibles"));
            }
            if (compoundTag.contains("MemberNamePrefix", 8) && (text2 = Text.Serializer.fromJson(compoundTag.getString("MemberNamePrefix"))) != null) {
                team.setPrefix(text2);
            }
            if (compoundTag.contains("MemberNameSuffix", 8) && (text2 = Text.Serializer.fromJson(compoundTag.getString("MemberNameSuffix"))) != null) {
                team.setSuffix(text2);
            }
            if (compoundTag.contains("NameTagVisibility", 8) && (visibilityRule = AbstractTeam.VisibilityRule.getRule(compoundTag.getString("NameTagVisibility"))) != null) {
                team.setNameTagVisibilityRule(visibilityRule);
            }
            if (compoundTag.contains("DeathMessageVisibility", 8) && (visibilityRule = AbstractTeam.VisibilityRule.getRule(compoundTag.getString("DeathMessageVisibility"))) != null) {
                team.setDeathMessageVisibilityRule(visibilityRule);
            }
            if (compoundTag.contains("CollisionRule", 8) && (collisionRule = AbstractTeam.CollisionRule.getRule(compoundTag.getString("CollisionRule"))) != null) {
                team.setCollisionRule(collisionRule);
            }
            this.deserializeTeamPlayers(team, compoundTag.getList("Players", 8));
        }
    }

    protected void deserializeTeamPlayers(Team team, ListTag listTag) {
        for (int i = 0; i < listTag.size(); ++i) {
            this.scoreboard.addPlayerToTeam(listTag.getString(i), team);
        }
    }

    protected void deserializeDisplaySlots(CompoundTag compoundTag) {
        for (int i = 0; i < 19; ++i) {
            if (!compoundTag.contains("slot_" + i, 8)) continue;
            String string = compoundTag.getString("slot_" + i);
            ScoreboardObjective scoreboardObjective = this.scoreboard.getNullableObjective(string);
            this.scoreboard.setObjectiveSlot(i, scoreboardObjective);
        }
    }

    protected void deserializeObjectives(ListTag listTag) {
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            ScoreboardCriterion.createStatCriterion(compoundTag.getString("CriteriaName")).ifPresent(scoreboardCriterion -> {
                String string = compoundTag.getString("Name");
                if (string.length() > 16) {
                    string = string.substring(0, 16);
                }
                Text text = Text.Serializer.fromJson(compoundTag.getString("DisplayName"));
                ScoreboardCriterion.RenderType renderType = ScoreboardCriterion.RenderType.getType(compoundTag.getString("RenderType"));
                this.scoreboard.addObjective(string, (ScoreboardCriterion)scoreboardCriterion, text, renderType);
            });
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        if (this.scoreboard == null) {
            LOGGER.warn("Tried to save scoreboard without having a scoreboard...");
            return compoundTag;
        }
        compoundTag.put("Objectives", this.serializeObjectives());
        compoundTag.put("PlayerScores", this.scoreboard.toTag());
        compoundTag.put("Teams", this.serializeTeams());
        this.serializeSlots(compoundTag);
        return compoundTag;
    }

    protected ListTag serializeTeams() {
        ListTag listTag = new ListTag();
        Collection<Team> collection = this.scoreboard.getTeams();
        for (Team team : collection) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("Name", team.getName());
            compoundTag.putString("DisplayName", Text.Serializer.toJson(team.getDisplayName()));
            if (team.getColor().getColorIndex() >= 0) {
                compoundTag.putString("TeamColor", team.getColor().getName());
            }
            compoundTag.putBoolean("AllowFriendlyFire", team.isFriendlyFireAllowed());
            compoundTag.putBoolean("SeeFriendlyInvisibles", team.shouldShowFriendlyInvisibles());
            compoundTag.putString("MemberNamePrefix", Text.Serializer.toJson(team.getPrefix()));
            compoundTag.putString("MemberNameSuffix", Text.Serializer.toJson(team.getSuffix()));
            compoundTag.putString("NameTagVisibility", team.getNameTagVisibilityRule().name);
            compoundTag.putString("DeathMessageVisibility", team.getDeathMessageVisibilityRule().name);
            compoundTag.putString("CollisionRule", team.getCollisionRule().name);
            ListTag listTag2 = new ListTag();
            for (String string : team.getPlayerList()) {
                listTag2.add(new StringTag(string));
            }
            compoundTag.put("Players", listTag2);
            listTag.add(compoundTag);
        }
        return listTag;
    }

    protected void serializeSlots(CompoundTag compoundTag) {
        CompoundTag compoundTag2 = new CompoundTag();
        boolean bl = false;
        for (int i = 0; i < 19; ++i) {
            ScoreboardObjective scoreboardObjective = this.scoreboard.getObjectiveForSlot(i);
            if (scoreboardObjective == null) continue;
            compoundTag2.putString("slot_" + i, scoreboardObjective.getName());
            bl = true;
        }
        if (bl) {
            compoundTag.put("DisplaySlots", compoundTag2);
        }
    }

    protected ListTag serializeObjectives() {
        ListTag listTag = new ListTag();
        Collection<ScoreboardObjective> collection = this.scoreboard.getObjectives();
        for (ScoreboardObjective scoreboardObjective : collection) {
            if (scoreboardObjective.getCriterion() == null) continue;
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("Name", scoreboardObjective.getName());
            compoundTag.putString("CriteriaName", scoreboardObjective.getCriterion().getName());
            compoundTag.putString("DisplayName", Text.Serializer.toJson(scoreboardObjective.getDisplayName()));
            compoundTag.putString("RenderType", scoreboardObjective.getRenderType().getName());
            listTag.add(compoundTag);
        }
        return listTag;
    }
}

