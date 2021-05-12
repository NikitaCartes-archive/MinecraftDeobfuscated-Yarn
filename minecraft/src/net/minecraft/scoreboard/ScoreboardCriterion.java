package net.minecraft.scoreboard;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.stat.StatType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ScoreboardCriterion {
	/**
	 * A map of all scoreboard criteria by their names.
	 * Updated automatically in the constructor.
	 */
	public static final Map<String, ScoreboardCriterion> CRITERIA = Maps.<String, ScoreboardCriterion>newHashMap();
	public static final ScoreboardCriterion DUMMY = new ScoreboardCriterion("dummy");
	public static final ScoreboardCriterion TRIGGER = new ScoreboardCriterion("trigger");
	public static final ScoreboardCriterion DEATH_COUNT = new ScoreboardCriterion("deathCount");
	public static final ScoreboardCriterion PLAYER_KILL_COUNT = new ScoreboardCriterion("playerKillCount");
	public static final ScoreboardCriterion TOTAL_KILL_COUNT = new ScoreboardCriterion("totalKillCount");
	public static final ScoreboardCriterion HEALTH = new ScoreboardCriterion("health", true, ScoreboardCriterion.RenderType.HEARTS);
	public static final ScoreboardCriterion FOOD = new ScoreboardCriterion("food", true, ScoreboardCriterion.RenderType.INTEGER);
	public static final ScoreboardCriterion AIR = new ScoreboardCriterion("air", true, ScoreboardCriterion.RenderType.INTEGER);
	public static final ScoreboardCriterion ARMOR = new ScoreboardCriterion("armor", true, ScoreboardCriterion.RenderType.INTEGER);
	public static final ScoreboardCriterion XP = new ScoreboardCriterion("xp", true, ScoreboardCriterion.RenderType.INTEGER);
	public static final ScoreboardCriterion LEVEL = new ScoreboardCriterion("level", true, ScoreboardCriterion.RenderType.INTEGER);
	public static final ScoreboardCriterion[] TEAM_KILLS = new ScoreboardCriterion[]{
		new ScoreboardCriterion("teamkill." + Formatting.BLACK.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.DARK_BLUE.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.DARK_GREEN.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.DARK_AQUA.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.DARK_RED.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.DARK_PURPLE.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.GOLD.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.GRAY.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.DARK_GRAY.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.BLUE.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.GREEN.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.AQUA.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.RED.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.LIGHT_PURPLE.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.YELLOW.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.WHITE.getName())
	};
	public static final ScoreboardCriterion[] KILLED_BY_TEAMS = new ScoreboardCriterion[]{
		new ScoreboardCriterion("killedByTeam." + Formatting.BLACK.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.DARK_BLUE.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.DARK_GREEN.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.DARK_AQUA.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.DARK_RED.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.DARK_PURPLE.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.GOLD.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.GRAY.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.DARK_GRAY.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.BLUE.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.GREEN.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.AQUA.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.RED.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.LIGHT_PURPLE.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.YELLOW.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.WHITE.getName())
	};
	private final String name;
	private final boolean readOnly;
	private final ScoreboardCriterion.RenderType defaultRenderType;

	public ScoreboardCriterion(String name) {
		this(name, false, ScoreboardCriterion.RenderType.INTEGER);
	}

	protected ScoreboardCriterion(String name, boolean readOnly, ScoreboardCriterion.RenderType defaultRenderType) {
		this.name = name;
		this.readOnly = readOnly;
		this.defaultRenderType = defaultRenderType;
		CRITERIA.put(name, this);
	}

	public static Optional<ScoreboardCriterion> getOrCreateStatCriterion(String name) {
		if (CRITERIA.containsKey(name)) {
			return Optional.of((ScoreboardCriterion)CRITERIA.get(name));
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
