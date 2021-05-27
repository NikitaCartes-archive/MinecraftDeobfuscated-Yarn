/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.scoreboard;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.stat.StatType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ScoreboardCriterion {
    private static final Map<String, ScoreboardCriterion> field_33939 = Maps.newHashMap();
    /**
     * A map of all scoreboard criteria by their names.
     * Updated automatically in the constructor.
     */
    private static final Map<String, ScoreboardCriterion> CRITERIA = Maps.newHashMap();
    public static final ScoreboardCriterion DUMMY = ScoreboardCriterion.method_37270("dummy");
    public static final ScoreboardCriterion TRIGGER = ScoreboardCriterion.method_37270("trigger");
    public static final ScoreboardCriterion DEATH_COUNT = ScoreboardCriterion.method_37270("deathCount");
    public static final ScoreboardCriterion PLAYER_KILL_COUNT = ScoreboardCriterion.method_37270("playerKillCount");
    public static final ScoreboardCriterion TOTAL_KILL_COUNT = ScoreboardCriterion.method_37270("totalKillCount");
    public static final ScoreboardCriterion HEALTH = ScoreboardCriterion.method_37269("health", true, RenderType.HEARTS);
    public static final ScoreboardCriterion FOOD = ScoreboardCriterion.method_37269("food", true, RenderType.INTEGER);
    public static final ScoreboardCriterion AIR = ScoreboardCriterion.method_37269("air", true, RenderType.INTEGER);
    public static final ScoreboardCriterion ARMOR = ScoreboardCriterion.method_37269("armor", true, RenderType.INTEGER);
    public static final ScoreboardCriterion XP = ScoreboardCriterion.method_37269("xp", true, RenderType.INTEGER);
    public static final ScoreboardCriterion LEVEL = ScoreboardCriterion.method_37269("level", true, RenderType.INTEGER);
    public static final ScoreboardCriterion[] TEAM_KILLS = new ScoreboardCriterion[]{ScoreboardCriterion.method_37270("teamkill." + Formatting.BLACK.getName()), ScoreboardCriterion.method_37270("teamkill." + Formatting.DARK_BLUE.getName()), ScoreboardCriterion.method_37270("teamkill." + Formatting.DARK_GREEN.getName()), ScoreboardCriterion.method_37270("teamkill." + Formatting.DARK_AQUA.getName()), ScoreboardCriterion.method_37270("teamkill." + Formatting.DARK_RED.getName()), ScoreboardCriterion.method_37270("teamkill." + Formatting.DARK_PURPLE.getName()), ScoreboardCriterion.method_37270("teamkill." + Formatting.GOLD.getName()), ScoreboardCriterion.method_37270("teamkill." + Formatting.GRAY.getName()), ScoreboardCriterion.method_37270("teamkill." + Formatting.DARK_GRAY.getName()), ScoreboardCriterion.method_37270("teamkill." + Formatting.BLUE.getName()), ScoreboardCriterion.method_37270("teamkill." + Formatting.GREEN.getName()), ScoreboardCriterion.method_37270("teamkill." + Formatting.AQUA.getName()), ScoreboardCriterion.method_37270("teamkill." + Formatting.RED.getName()), ScoreboardCriterion.method_37270("teamkill." + Formatting.LIGHT_PURPLE.getName()), ScoreboardCriterion.method_37270("teamkill." + Formatting.YELLOW.getName()), ScoreboardCriterion.method_37270("teamkill." + Formatting.WHITE.getName())};
    public static final ScoreboardCriterion[] KILLED_BY_TEAMS = new ScoreboardCriterion[]{ScoreboardCriterion.method_37270("killedByTeam." + Formatting.BLACK.getName()), ScoreboardCriterion.method_37270("killedByTeam." + Formatting.DARK_BLUE.getName()), ScoreboardCriterion.method_37270("killedByTeam." + Formatting.DARK_GREEN.getName()), ScoreboardCriterion.method_37270("killedByTeam." + Formatting.DARK_AQUA.getName()), ScoreboardCriterion.method_37270("killedByTeam." + Formatting.DARK_RED.getName()), ScoreboardCriterion.method_37270("killedByTeam." + Formatting.DARK_PURPLE.getName()), ScoreboardCriterion.method_37270("killedByTeam." + Formatting.GOLD.getName()), ScoreboardCriterion.method_37270("killedByTeam." + Formatting.GRAY.getName()), ScoreboardCriterion.method_37270("killedByTeam." + Formatting.DARK_GRAY.getName()), ScoreboardCriterion.method_37270("killedByTeam." + Formatting.BLUE.getName()), ScoreboardCriterion.method_37270("killedByTeam." + Formatting.GREEN.getName()), ScoreboardCriterion.method_37270("killedByTeam." + Formatting.AQUA.getName()), ScoreboardCriterion.method_37270("killedByTeam." + Formatting.RED.getName()), ScoreboardCriterion.method_37270("killedByTeam." + Formatting.LIGHT_PURPLE.getName()), ScoreboardCriterion.method_37270("killedByTeam." + Formatting.YELLOW.getName()), ScoreboardCriterion.method_37270("killedByTeam." + Formatting.WHITE.getName())};
    private final String name;
    private final boolean readOnly;
    private final RenderType defaultRenderType;

    private static ScoreboardCriterion method_37269(String string, boolean bl, RenderType renderType) {
        ScoreboardCriterion scoreboardCriterion = new ScoreboardCriterion(string, bl, renderType);
        field_33939.put(string, scoreboardCriterion);
        return scoreboardCriterion;
    }

    private static ScoreboardCriterion method_37270(String string) {
        return ScoreboardCriterion.method_37269(string, false, RenderType.INTEGER);
    }

    protected ScoreboardCriterion(String name) {
        this(name, false, RenderType.INTEGER);
    }

    protected ScoreboardCriterion(String name, boolean readOnly, RenderType defaultRenderType) {
        this.name = name;
        this.readOnly = readOnly;
        this.defaultRenderType = defaultRenderType;
        CRITERIA.put(name, this);
    }

    public static Set<String> method_37271() {
        return ImmutableSet.copyOf(field_33939.keySet());
    }

    public static Optional<ScoreboardCriterion> getOrCreateStatCriterion(String name) {
        ScoreboardCriterion scoreboardCriterion = CRITERIA.get(name);
        if (scoreboardCriterion != null) {
            return Optional.of(scoreboardCriterion);
        }
        int i = name.indexOf(58);
        if (i < 0) {
            return Optional.empty();
        }
        return Registry.STAT_TYPE.getOrEmpty(Identifier.splitOn(name.substring(0, i), '.')).flatMap(statType -> ScoreboardCriterion.getOrCreateStatCriterion(statType, Identifier.splitOn(name.substring(i + 1), '.')));
    }

    private static <T> Optional<ScoreboardCriterion> getOrCreateStatCriterion(StatType<T> statType, Identifier id) {
        return statType.getRegistry().getOrEmpty(id).map(statType::getOrCreateStat);
    }

    public String getName() {
        return this.name;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public RenderType getDefaultRenderType() {
        return this.defaultRenderType;
    }

    public static enum RenderType {
        INTEGER("integer"),
        HEARTS("hearts");

        private final String name;
        private static final Map<String, RenderType> CRITERION_TYPES;

        private RenderType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static RenderType getType(String name) {
            return CRITERION_TYPES.getOrDefault(name, INTEGER);
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

