/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.scoreboard;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.ChatFormat;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ScoreboardCriterion {
    public static final Map<String, ScoreboardCriterion> OBJECTIVES = Maps.newHashMap();
    public static final ScoreboardCriterion DUMMY = new ScoreboardCriterion("dummy");
    public static final ScoreboardCriterion TRIGGER = new ScoreboardCriterion("trigger");
    public static final ScoreboardCriterion DEATH_COUNT = new ScoreboardCriterion("deathCount");
    public static final ScoreboardCriterion PLAYER_KILL_COUNT = new ScoreboardCriterion("playerKillCount");
    public static final ScoreboardCriterion TOTAL_KILL_COUNT = new ScoreboardCriterion("totalKillCount");
    public static final ScoreboardCriterion HEALTH = new ScoreboardCriterion("health", true, RenderType.HEARTS);
    public static final ScoreboardCriterion FOOD = new ScoreboardCriterion("food", true, RenderType.INTEGER);
    public static final ScoreboardCriterion AIR = new ScoreboardCriterion("air", true, RenderType.INTEGER);
    public static final ScoreboardCriterion ARMOR = new ScoreboardCriterion("armor", true, RenderType.INTEGER);
    public static final ScoreboardCriterion XP = new ScoreboardCriterion("xp", true, RenderType.INTEGER);
    public static final ScoreboardCriterion LEVEL = new ScoreboardCriterion("level", true, RenderType.INTEGER);
    public static final ScoreboardCriterion[] TEAM_KILLS = new ScoreboardCriterion[]{new ScoreboardCriterion("teamkill." + ChatFormat.BLACK.getName()), new ScoreboardCriterion("teamkill." + ChatFormat.DARK_BLUE.getName()), new ScoreboardCriterion("teamkill." + ChatFormat.DARK_GREEN.getName()), new ScoreboardCriterion("teamkill." + ChatFormat.DARK_AQUA.getName()), new ScoreboardCriterion("teamkill." + ChatFormat.DARK_RED.getName()), new ScoreboardCriterion("teamkill." + ChatFormat.DARK_PURPLE.getName()), new ScoreboardCriterion("teamkill." + ChatFormat.GOLD.getName()), new ScoreboardCriterion("teamkill." + ChatFormat.GRAY.getName()), new ScoreboardCriterion("teamkill." + ChatFormat.DARK_GRAY.getName()), new ScoreboardCriterion("teamkill." + ChatFormat.BLUE.getName()), new ScoreboardCriterion("teamkill." + ChatFormat.GREEN.getName()), new ScoreboardCriterion("teamkill." + ChatFormat.AQUA.getName()), new ScoreboardCriterion("teamkill." + ChatFormat.RED.getName()), new ScoreboardCriterion("teamkill." + ChatFormat.LIGHT_PURPLE.getName()), new ScoreboardCriterion("teamkill." + ChatFormat.YELLOW.getName()), new ScoreboardCriterion("teamkill." + ChatFormat.WHITE.getName())};
    public static final ScoreboardCriterion[] KILLED_BY_TEAMS = new ScoreboardCriterion[]{new ScoreboardCriterion("killedByTeam." + ChatFormat.BLACK.getName()), new ScoreboardCriterion("killedByTeam." + ChatFormat.DARK_BLUE.getName()), new ScoreboardCriterion("killedByTeam." + ChatFormat.DARK_GREEN.getName()), new ScoreboardCriterion("killedByTeam." + ChatFormat.DARK_AQUA.getName()), new ScoreboardCriterion("killedByTeam." + ChatFormat.DARK_RED.getName()), new ScoreboardCriterion("killedByTeam." + ChatFormat.DARK_PURPLE.getName()), new ScoreboardCriterion("killedByTeam." + ChatFormat.GOLD.getName()), new ScoreboardCriterion("killedByTeam." + ChatFormat.GRAY.getName()), new ScoreboardCriterion("killedByTeam." + ChatFormat.DARK_GRAY.getName()), new ScoreboardCriterion("killedByTeam." + ChatFormat.BLUE.getName()), new ScoreboardCriterion("killedByTeam." + ChatFormat.GREEN.getName()), new ScoreboardCriterion("killedByTeam." + ChatFormat.AQUA.getName()), new ScoreboardCriterion("killedByTeam." + ChatFormat.RED.getName()), new ScoreboardCriterion("killedByTeam." + ChatFormat.LIGHT_PURPLE.getName()), new ScoreboardCriterion("killedByTeam." + ChatFormat.YELLOW.getName()), new ScoreboardCriterion("killedByTeam." + ChatFormat.WHITE.getName())};
    private final String name;
    private final boolean readOnly;
    private final RenderType criterionType;

    public ScoreboardCriterion(String string) {
        this(string, false, RenderType.INTEGER);
    }

    protected ScoreboardCriterion(String string, boolean bl, RenderType renderType) {
        this.name = string;
        this.readOnly = bl;
        this.criterionType = renderType;
        OBJECTIVES.put(string, this);
    }

    public static Optional<ScoreboardCriterion> createStatCriterion(String string) {
        if (OBJECTIVES.containsKey(string)) {
            return Optional.of(OBJECTIVES.get(string));
        }
        int i = string.indexOf(58);
        if (i < 0) {
            return Optional.empty();
        }
        return Registry.STAT_TYPE.getOrEmpty(Identifier.splitOn(string.substring(0, i), '.')).flatMap(statType -> ScoreboardCriterion.createStatCriterion(statType, Identifier.splitOn(string.substring(i + 1), '.')));
    }

    private static <T> Optional<ScoreboardCriterion> createStatCriterion(StatType<T> statType, Identifier identifier) {
        return statType.getRegistry().getOrEmpty(identifier).map(statType::getOrCreateStat);
    }

    public String getName() {
        return this.name;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public RenderType getCriterionType() {
        return this.criterionType;
    }

    public static enum RenderType {
        INTEGER("integer"),
        HEARTS("hearts");

        private final String name;
        private static final Map<String, RenderType> CRITERION_TYPES;

        private RenderType(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        public static RenderType getType(String string) {
            return CRITERION_TYPES.getOrDefault(string, INTEGER);
        }

        static {
            ImmutableMap.Builder<String, RenderType> builder = ImmutableMap.builder();
            for (RenderType renderType : RenderType.values()) {
                builder.put(renderType.name, renderType);
            }
            CRITERION_TYPES = builder.build();
        }
    }
}

