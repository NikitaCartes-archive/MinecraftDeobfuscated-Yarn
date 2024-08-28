package net.minecraft.scoreboard;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.registry.Registries;
import net.minecraft.stat.StatType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

public class ScoreboardCriterion {
	/**
	 * A map of all scoreboard criteria that can be identified with a single
	 * keyword (as opposed to combined words) by their names.
	 * Updated automatically in {@link #create(String, boolean, ScoreboardCriterion.RenderType)}.
	 */
	private static final Map<String, ScoreboardCriterion> SIMPLE_CRITERIA = Maps.<String, ScoreboardCriterion>newHashMap();
	/**
	 * A map of all scoreboard criteria by their names.
	 * Updated automatically in the constructor.
	 */
	private static final Map<String, ScoreboardCriterion> CRITERIA = Maps.<String, ScoreboardCriterion>newHashMap();
	public static final ScoreboardCriterion DUMMY = create("dummy");
	public static final ScoreboardCriterion TRIGGER = create("trigger");
	public static final ScoreboardCriterion DEATH_COUNT = create("deathCount");
	public static final ScoreboardCriterion PLAYER_KILL_COUNT = create("playerKillCount");
	public static final ScoreboardCriterion TOTAL_KILL_COUNT = create("totalKillCount");
	public static final ScoreboardCriterion HEALTH = create("health", true, ScoreboardCriterion.RenderType.HEARTS);
	public static final ScoreboardCriterion FOOD = create("food", true, ScoreboardCriterion.RenderType.INTEGER);
	public static final ScoreboardCriterion AIR = create("air", true, ScoreboardCriterion.RenderType.INTEGER);
	public static final ScoreboardCriterion ARMOR = create("armor", true, ScoreboardCriterion.RenderType.INTEGER);
	public static final ScoreboardCriterion XP = create("xp", true, ScoreboardCriterion.RenderType.INTEGER);
	public static final ScoreboardCriterion LEVEL = create("level", true, ScoreboardCriterion.RenderType.INTEGER);
	public static final ScoreboardCriterion[] TEAM_KILLS = new ScoreboardCriterion[]{
		create("teamkill." + Formatting.BLACK.getName()),
		create("teamkill." + Formatting.DARK_BLUE.getName()),
		create("teamkill." + Formatting.DARK_GREEN.getName()),
		create("teamkill." + Formatting.DARK_AQUA.getName()),
		create("teamkill." + Formatting.DARK_RED.getName()),
		create("teamkill." + Formatting.DARK_PURPLE.getName()),
		create("teamkill." + Formatting.GOLD.getName()),
		create("teamkill." + Formatting.GRAY.getName()),
		create("teamkill." + Formatting.DARK_GRAY.getName()),
		create("teamkill." + Formatting.BLUE.getName()),
		create("teamkill." + Formatting.GREEN.getName()),
		create("teamkill." + Formatting.AQUA.getName()),
		create("teamkill." + Formatting.RED.getName()),
		create("teamkill." + Formatting.LIGHT_PURPLE.getName()),
		create("teamkill." + Formatting.YELLOW.getName()),
		create("teamkill." + Formatting.WHITE.getName())
	};
	public static final ScoreboardCriterion[] KILLED_BY_TEAMS = new ScoreboardCriterion[]{
		create("killedByTeam." + Formatting.BLACK.getName()),
		create("killedByTeam." + Formatting.DARK_BLUE.getName()),
		create("killedByTeam." + Formatting.DARK_GREEN.getName()),
		create("killedByTeam." + Formatting.DARK_AQUA.getName()),
		create("killedByTeam." + Formatting.DARK_RED.getName()),
		create("killedByTeam." + Formatting.DARK_PURPLE.getName()),
		create("killedByTeam." + Formatting.GOLD.getName()),
		create("killedByTeam." + Formatting.GRAY.getName()),
		create("killedByTeam." + Formatting.DARK_GRAY.getName()),
		create("killedByTeam." + Formatting.BLUE.getName()),
		create("killedByTeam." + Formatting.GREEN.getName()),
		create("killedByTeam." + Formatting.AQUA.getName()),
		create("killedByTeam." + Formatting.RED.getName()),
		create("killedByTeam." + Formatting.LIGHT_PURPLE.getName()),
		create("killedByTeam." + Formatting.YELLOW.getName()),
		create("killedByTeam." + Formatting.WHITE.getName())
	};
	private final String name;
	private final boolean readOnly;
	private final ScoreboardCriterion.RenderType defaultRenderType;

	private static ScoreboardCriterion create(String name, boolean readOnly, ScoreboardCriterion.RenderType defaultRenderType) {
		ScoreboardCriterion scoreboardCriterion = new ScoreboardCriterion(name, readOnly, defaultRenderType);
		SIMPLE_CRITERIA.put(name, scoreboardCriterion);
		return scoreboardCriterion;
	}

	private static ScoreboardCriterion create(String name) {
		return create(name, false, ScoreboardCriterion.RenderType.INTEGER);
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

	public static Set<String> getAllSimpleCriteria() {
		return ImmutableSet.copyOf(SIMPLE_CRITERIA.keySet());
	}

	public static Optional<ScoreboardCriterion> getOrCreateStatCriterion(String name) {
		ScoreboardCriterion scoreboardCriterion = (ScoreboardCriterion)CRITERIA.get(name);
		if (scoreboardCriterion != null) {
			return Optional.of(scoreboardCriterion);
		} else {
			int i = name.indexOf(58);
			return i < 0
				? Optional.empty()
				: Registries.STAT_TYPE
					.getOptionalValue(Identifier.splitOn(name.substring(0, i), '.'))
					.flatMap(type -> getOrCreateStatCriterion(type, Identifier.splitOn(name.substring(i + 1), '.')));
		}
	}

	private static <T> Optional<ScoreboardCriterion> getOrCreateStatCriterion(StatType<T> statType, Identifier id) {
		return statType.getRegistry().getOptionalValue(id).map(statType::getOrCreateStat);
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

	public static enum RenderType implements StringIdentifiable {
		INTEGER("integer"),
		HEARTS("hearts");

		private final String name;
		public static final StringIdentifiable.EnumCodec<ScoreboardCriterion.RenderType> CODEC = StringIdentifiable.createCodec(
			ScoreboardCriterion.RenderType::values
		);

		private RenderType(final String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		@Override
		public String asString() {
			return this.name;
		}

		public static ScoreboardCriterion.RenderType getType(String name) {
			return (ScoreboardCriterion.RenderType)CODEC.byId(name, INTEGER);
		}
	}
}
