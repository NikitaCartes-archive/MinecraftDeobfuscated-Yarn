package net.minecraft.scoreboard;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.stat.StatType;
import net.minecraft.text.TextFormat;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ScoreboardCriterion {
	public static final Map<String, ScoreboardCriterion> OBJECTIVES = Maps.<String, ScoreboardCriterion>newHashMap();
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
		new ScoreboardCriterion("teamkill." + TextFormat.BLACK.getName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1058.getName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1077.getName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1062.getName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1079.getName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1064.getName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1065.getName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1080.getName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1063.getName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1078.getName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1060.getName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1075.getName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1061.getName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1076.getName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1054.getName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1068.getName())
	};
	public static final ScoreboardCriterion[] KILLED_BY_TEAMS = new ScoreboardCriterion[]{
		new ScoreboardCriterion("killedByTeam." + TextFormat.BLACK.getName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1058.getName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1077.getName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1062.getName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1079.getName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1064.getName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1065.getName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1080.getName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1063.getName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1078.getName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1060.getName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1075.getName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1061.getName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1076.getName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1054.getName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1068.getName())
	};
	private final String name;
	private final boolean readOnly;
	private final ScoreboardCriterion.RenderType criterionType;

	public ScoreboardCriterion(String string) {
		this(string, false, ScoreboardCriterion.RenderType.INTEGER);
	}

	protected ScoreboardCriterion(String string, boolean bl, ScoreboardCriterion.RenderType renderType) {
		this.name = string;
		this.readOnly = bl;
		this.criterionType = renderType;
		OBJECTIVES.put(string, this);
	}

	public static Optional<ScoreboardCriterion> createStatCriterion(String string) {
		if (OBJECTIVES.containsKey(string)) {
			return Optional.of(OBJECTIVES.get(string));
		} else {
			int i = string.indexOf(58);
			return i < 0
				? Optional.empty()
				: Registry.STAT_TYPE
					.getOrEmpty(Identifier.createSplit(string.substring(0, i), '.'))
					.flatMap(statType -> createStatCriterion(statType, Identifier.createSplit(string.substring(i + 1), '.')));
		}
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

	public ScoreboardCriterion.RenderType getCriterionType() {
		return this.criterionType;
	}

	public static enum RenderType {
		INTEGER("integer"),
		HEARTS("hearts");

		private final String name;
		private static final Map<String, ScoreboardCriterion.RenderType> CRITERION_TYPES;

		private RenderType(String string2) {
			this.name = string2;
		}

		public String getName() {
			return this.name;
		}

		public static ScoreboardCriterion.RenderType getType(String string) {
			return (ScoreboardCriterion.RenderType)CRITERION_TYPES.getOrDefault(string, INTEGER);
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
