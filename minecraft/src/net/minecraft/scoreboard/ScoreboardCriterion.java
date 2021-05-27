package net.minecraft.scoreboard;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.stat.StatType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ScoreboardCriterion {
	private static final Map<String, ScoreboardCriterion> field_33939 = Maps.<String, ScoreboardCriterion>newHashMap();
	/**
	 * A map of all scoreboard criteria by their names.
	 * Updated automatically in the constructor.
	 */
	private static final Map<String, ScoreboardCriterion> CRITERIA = Maps.<String, ScoreboardCriterion>newHashMap();
	public static final ScoreboardCriterion DUMMY = method_37270("dummy");
	public static final ScoreboardCriterion TRIGGER = method_37270("trigger");
	public static final ScoreboardCriterion DEATH_COUNT = method_37270("deathCount");
	public static final ScoreboardCriterion PLAYER_KILL_COUNT = method_37270("playerKillCount");
	public static final ScoreboardCriterion TOTAL_KILL_COUNT = method_37270("totalKillCount");
	public static final ScoreboardCriterion HEALTH = method_37269("health", true, ScoreboardCriterion.RenderType.HEARTS);
	public static final ScoreboardCriterion FOOD = method_37269("food", true, ScoreboardCriterion.RenderType.INTEGER);
	public static final ScoreboardCriterion AIR = method_37269("air", true, ScoreboardCriterion.RenderType.INTEGER);
	public static final ScoreboardCriterion ARMOR = method_37269("armor", true, ScoreboardCriterion.RenderType.INTEGER);
	public static final ScoreboardCriterion XP = method_37269("xp", true, ScoreboardCriterion.RenderType.INTEGER);
	public static final ScoreboardCriterion LEVEL = method_37269("level", true, ScoreboardCriterion.RenderType.INTEGER);
	public static final ScoreboardCriterion[] TEAM_KILLS = new ScoreboardCriterion[]{
		method_37270("teamkill." + Formatting.BLACK.getName()),
		method_37270("teamkill." + Formatting.DARK_BLUE.getName()),
		method_37270("teamkill." + Formatting.DARK_GREEN.getName()),
		method_37270("teamkill." + Formatting.DARK_AQUA.getName()),
		method_37270("teamkill." + Formatting.DARK_RED.getName()),
		method_37270("teamkill." + Formatting.DARK_PURPLE.getName()),
		method_37270("teamkill." + Formatting.GOLD.getName()),
		method_37270("teamkill." + Formatting.GRAY.getName()),
		method_37270("teamkill." + Formatting.DARK_GRAY.getName()),
		method_37270("teamkill." + Formatting.BLUE.getName()),
		method_37270("teamkill." + Formatting.GREEN.getName()),
		method_37270("teamkill." + Formatting.AQUA.getName()),
		method_37270("teamkill." + Formatting.RED.getName()),
		method_37270("teamkill." + Formatting.LIGHT_PURPLE.getName()),
		method_37270("teamkill." + Formatting.YELLOW.getName()),
		method_37270("teamkill." + Formatting.WHITE.getName())
	};
	public static final ScoreboardCriterion[] KILLED_BY_TEAMS = new ScoreboardCriterion[]{
		method_37270("killedByTeam." + Formatting.BLACK.getName()),
		method_37270("killedByTeam." + Formatting.DARK_BLUE.getName()),
		method_37270("killedByTeam." + Formatting.DARK_GREEN.getName()),
		method_37270("killedByTeam." + Formatting.DARK_AQUA.getName()),
		method_37270("killedByTeam." + Formatting.DARK_RED.getName()),
		method_37270("killedByTeam." + Formatting.DARK_PURPLE.getName()),
		method_37270("killedByTeam." + Formatting.GOLD.getName()),
		method_37270("killedByTeam." + Formatting.GRAY.getName()),
		method_37270("killedByTeam." + Formatting.DARK_GRAY.getName()),
		method_37270("killedByTeam." + Formatting.BLUE.getName()),
		method_37270("killedByTeam." + Formatting.GREEN.getName()),
		method_37270("killedByTeam." + Formatting.AQUA.getName()),
		method_37270("killedByTeam." + Formatting.RED.getName()),
		method_37270("killedByTeam." + Formatting.LIGHT_PURPLE.getName()),
		method_37270("killedByTeam." + Formatting.YELLOW.getName()),
		method_37270("killedByTeam." + Formatting.WHITE.getName())
	};
	private final String name;
	private final boolean readOnly;
	private final ScoreboardCriterion.RenderType defaultRenderType;

	private static ScoreboardCriterion method_37269(String string, boolean bl, ScoreboardCriterion.RenderType renderType) {
		ScoreboardCriterion scoreboardCriterion = new ScoreboardCriterion(string, bl, renderType);
		field_33939.put(string, scoreboardCriterion);
		return scoreboardCriterion;
	}

	private static ScoreboardCriterion method_37270(String string) {
		return method_37269(string, false, ScoreboardCriterion.RenderType.INTEGER);
	}

	protected ScoreboardCriterion(String name) {
		this(name, false, ScoreboardCriterion.RenderType.INTEGER);
	}

	protected ScoreboardCriterion(String name, boolean readOnly, ScoreboardCriterion.RenderType defaultRenderType) {
		this.name = name;
		this.readOnly = readOnly;
		this.defaultRenderType = defaultRenderType;
		CRITERIA.put(name, this);
	}

	public static Set<String> method_37271() {
		return ImmutableSet.copyOf(field_33939.keySet());
	}

	public static Optional<ScoreboardCriterion> getOrCreateStatCriterion(String name) {
		ScoreboardCriterion scoreboardCriterion = (ScoreboardCriterion)CRITERIA.get(name);
		if (scoreboardCriterion != null) {
			return Optional.of(scoreboardCriterion);
		} else {
			int i = name.indexOf(58);
			return i < 0
				? Optional.empty()
				: Registry.STAT_TYPE
					.getOrEmpty(Identifier.splitOn(name.substring(0, i), '.'))
					.flatMap(statType -> getOrCreateStatCriterion(statType, Identifier.splitOn(name.substring(i + 1), '.')));
		}
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

	public ScoreboardCriterion.RenderType getDefaultRenderType() {
		return this.defaultRenderType;
	}

	public static enum RenderType {
		INTEGER("integer"),
		HEARTS("hearts");

		private final String name;
		private static final Map<String, ScoreboardCriterion.RenderType> CRITERION_TYPES;

		private RenderType(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static ScoreboardCriterion.RenderType getType(String name) {
			return (ScoreboardCriterion.RenderType)CRITERION_TYPES.getOrDefault(name, INTEGER);
		}

		static {
			Builder<String, ScoreboardCriterion.RenderType> builder = ImmutableMap.builder();

			for (ScoreboardCriterion.RenderType renderType : values()) {
				builder.put(renderType.name, renderType);
			}

			CRITERION_TYPES = builder.build();
		}
	}
}
