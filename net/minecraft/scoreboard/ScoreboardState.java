/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.scoreboard;

import java.util.Collection;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.PersistentState;

public class ScoreboardState
extends PersistentState {
    public static final String SCOREBOARD_KEY = "scoreboard";
    private final Scoreboard scoreboard;

    public ScoreboardState(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public ScoreboardState readNbt(NbtCompound nbt) {
        this.readObjectivesNbt(nbt.getList("Objectives", NbtElement.COMPOUND_TYPE));
        this.scoreboard.readNbt(nbt.getList("PlayerScores", NbtElement.COMPOUND_TYPE));
        if (nbt.contains("DisplaySlots", NbtElement.COMPOUND_TYPE)) {
            this.readDisplaySlotsNbt(nbt.getCompound("DisplaySlots"));
        }
        if (nbt.contains("Teams", NbtElement.LIST_TYPE)) {
            this.readTeamsNbt(nbt.getList("Teams", NbtElement.COMPOUND_TYPE));
        }
        return this;
    }

    private void readTeamsNbt(NbtList nbt) {
        for (int i = 0; i < nbt.size(); ++i) {
            AbstractTeam.CollisionRule collisionRule;
            AbstractTeam.VisibilityRule visibilityRule;
            MutableText text2;
            NbtCompound nbtCompound = nbt.getCompound(i);
            String string = nbtCompound.getString("Name");
            Team team = this.scoreboard.addTeam(string);
            MutableText text = Text.Serializer.fromJson(nbtCompound.getString("DisplayName"));
            if (text != null) {
                team.setDisplayName(text);
            }
            if (nbtCompound.contains("TeamColor", NbtElement.STRING_TYPE)) {
                team.setColor(Formatting.byName(nbtCompound.getString("TeamColor")));
            }
            if (nbtCompound.contains("AllowFriendlyFire", NbtElement.NUMBER_TYPE)) {
                team.setFriendlyFireAllowed(nbtCompound.getBoolean("AllowFriendlyFire"));
            }
            if (nbtCompound.contains("SeeFriendlyInvisibles", NbtElement.NUMBER_TYPE)) {
                team.setShowFriendlyInvisibles(nbtCompound.getBoolean("SeeFriendlyInvisibles"));
            }
            if (nbtCompound.contains("MemberNamePrefix", NbtElement.STRING_TYPE) && (text2 = Text.Serializer.fromJson(nbtCompound.getString("MemberNamePrefix"))) != null) {
                team.setPrefix(text2);
            }
            if (nbtCompound.contains("MemberNameSuffix", NbtElement.STRING_TYPE) && (text2 = Text.Serializer.fromJson(nbtCompound.getString("MemberNameSuffix"))) != null) {
                team.setSuffix(text2);
            }
            if (nbtCompound.contains("NameTagVisibility", NbtElement.STRING_TYPE) && (visibilityRule = AbstractTeam.VisibilityRule.getRule(nbtCompound.getString("NameTagVisibility"))) != null) {
                team.setNameTagVisibilityRule(visibilityRule);
            }
            if (nbtCompound.contains("DeathMessageVisibility", NbtElement.STRING_TYPE) && (visibilityRule = AbstractTeam.VisibilityRule.getRule(nbtCompound.getString("DeathMessageVisibility"))) != null) {
                team.setDeathMessageVisibilityRule(visibilityRule);
            }
            if (nbtCompound.contains("CollisionRule", NbtElement.STRING_TYPE) && (collisionRule = AbstractTeam.CollisionRule.getRule(nbtCompound.getString("CollisionRule"))) != null) {
                team.setCollisionRule(collisionRule);
            }
            this.readTeamPlayersNbt(team, nbtCompound.getList("Players", NbtElement.STRING_TYPE));
        }
    }

    private void readTeamPlayersNbt(Team team, NbtList nbt) {
        for (int i = 0; i < nbt.size(); ++i) {
            this.scoreboard.addPlayerToTeam(nbt.getString(i), team);
        }
    }

    private void readDisplaySlotsNbt(NbtCompound nbt) {
        for (int i = 0; i < 19; ++i) {
            if (!nbt.contains("slot_" + i, NbtElement.STRING_TYPE)) continue;
            String string = nbt.getString("slot_" + i);
            ScoreboardObjective scoreboardObjective = this.scoreboard.getNullableObjective(string);
            this.scoreboard.setObjectiveSlot(i, scoreboardObjective);
        }
    }

    private void readObjectivesNbt(NbtList nbt) {
        for (int i = 0; i < nbt.size(); ++i) {
            NbtCompound nbtCompound = nbt.getCompound(i);
            ScoreboardCriterion.getOrCreateStatCriterion(nbtCompound.getString("CriteriaName")).ifPresent(criterion -> {
                String string = nbtCompound.getString("Name");
                MutableText text = Text.Serializer.fromJson(nbtCompound.getString("DisplayName"));
                ScoreboardCriterion.RenderType renderType = ScoreboardCriterion.RenderType.getType(nbtCompound.getString("RenderType"));
                this.scoreboard.addObjective(string, (ScoreboardCriterion)criterion, text, renderType);
            });
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.put("Objectives", this.objectivesToNbt());
        nbt.put("PlayerScores", this.scoreboard.toNbt());
        nbt.put("Teams", this.teamsToNbt());
        this.writeDisplaySlotsNbt(nbt);
        return nbt;
    }

    private NbtList teamsToNbt() {
        NbtList nbtList = new NbtList();
        Collection<Team> collection = this.scoreboard.getTeams();
        for (Team team : collection) {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putString("Name", team.getName());
            nbtCompound.putString("DisplayName", Text.Serializer.toJson(team.getDisplayName()));
            if (team.getColor().getColorIndex() >= 0) {
                nbtCompound.putString("TeamColor", team.getColor().getName());
            }
            nbtCompound.putBoolean("AllowFriendlyFire", team.isFriendlyFireAllowed());
            nbtCompound.putBoolean("SeeFriendlyInvisibles", team.shouldShowFriendlyInvisibles());
            nbtCompound.putString("MemberNamePrefix", Text.Serializer.toJson(team.getPrefix()));
            nbtCompound.putString("MemberNameSuffix", Text.Serializer.toJson(team.getSuffix()));
            nbtCompound.putString("NameTagVisibility", team.getNameTagVisibilityRule().name);
            nbtCompound.putString("DeathMessageVisibility", team.getDeathMessageVisibilityRule().name);
            nbtCompound.putString("CollisionRule", team.getCollisionRule().name);
            NbtList nbtList2 = new NbtList();
            for (String string : team.getPlayerList()) {
                nbtList2.add(NbtString.of(string));
            }
            nbtCompound.put("Players", nbtList2);
            nbtList.add(nbtCompound);
        }
        return nbtList;
    }

    private void writeDisplaySlotsNbt(NbtCompound nbt) {
        NbtCompound nbtCompound = new NbtCompound();
        boolean bl = false;
        for (int i = 0; i < 19; ++i) {
            ScoreboardObjective scoreboardObjective = this.scoreboard.getObjectiveForSlot(i);
            if (scoreboardObjective == null) continue;
            nbtCompound.putString("slot_" + i, scoreboardObjective.getName());
            bl = true;
        }
        if (bl) {
            nbt.put("DisplaySlots", nbtCompound);
        }
    }

    private NbtList objectivesToNbt() {
        NbtList nbtList = new NbtList();
        Collection<ScoreboardObjective> collection = this.scoreboard.getObjectives();
        for (ScoreboardObjective scoreboardObjective : collection) {
            if (scoreboardObjective.getCriterion() == null) continue;
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putString("Name", scoreboardObjective.getName());
            nbtCompound.putString("CriteriaName", scoreboardObjective.getCriterion().getName());
            nbtCompound.putString("DisplayName", Text.Serializer.toJson(scoreboardObjective.getDisplayName()));
            nbtCompound.putString("RenderType", scoreboardObjective.getRenderType().getName());
            nbtList.add(nbtCompound);
        }
        return nbtList;
    }
}

