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
	public static final Map<String, ScoreboardCriterion> OBJECTIVES = Maps.<String, ScoreboardCriterion>newHashMap();
	public static final ScoreboardCriterion field_1468 = new ScoreboardCriterion("dummy");
	public static final ScoreboardCriterion field_1462 = new ScoreboardCriterion("trigger");
	public static final ScoreboardCriterion field_1456 = new ScoreboardCriterion("deathCount");
	public static final ScoreboardCriterion field_1463 = new ScoreboardCriterion("playerKillCount");
	public static final ScoreboardCriterion field_1457 = new ScoreboardCriterion("totalKillCount");
	public static final ScoreboardCriterion field_1453 = new ScoreboardCriterion("health", true, ScoreboardCriterion.RenderType.field_1471);
	public static final ScoreboardCriterion field_1464 = new ScoreboardCriterion("food", true, ScoreboardCriterion.RenderType.field_1472);
	public static final ScoreboardCriterion field_1459 = new ScoreboardCriterion("air", true, ScoreboardCriterion.RenderType.field_1472);
	public static final ScoreboardCriterion field_1452 = new ScoreboardCriterion("armor", true, ScoreboardCriterion.RenderType.field_1472);
	public static final ScoreboardCriterion field_1460 = new ScoreboardCriterion("xp", true, ScoreboardCriterion.RenderType.field_1472);
	public static final ScoreboardCriterion field_1465 = new ScoreboardCriterion("level", true, ScoreboardCriterion.RenderType.field_1472);
	public static final ScoreboardCriterion[] TEAM_KILLS = new ScoreboardCriterion[]{
		new ScoreboardCriterion("teamkill." + Formatting.field_1074.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.field_1058.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.field_1077.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.field_1062.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.field_1079.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.field_1064.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.field_1065.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.field_1080.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.field_1063.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.field_1078.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.field_1060.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.field_1075.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.field_1061.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.field_1076.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.field_1054.getName()),
		new ScoreboardCriterion("teamkill." + Formatting.field_1068.getName())
	};
	public static final ScoreboardCriterion[] KILLED_BY_TEAMS = new ScoreboardCriterion[]{
		new ScoreboardCriterion("killedByTeam." + Formatting.field_1074.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.field_1058.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.field_1077.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.field_1062.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.field_1079.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.field_1064.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.field_1065.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.field_1080.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.field_1063.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.field_1078.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.field_1060.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.field_1075.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.field_1061.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.field_1076.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.field_1054.getName()),
		new ScoreboardCriterion("killedByTeam." + Formatting.field_1068.getName())
	};
	private final String name;
	private final boolean readOnly;
	private final ScoreboardCriterion.RenderType criterionType;

	public ScoreboardCriterion(String string) {
		this(string, false, ScoreboardCriterion.RenderType.field_1472);
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
					.getOrEmpty(Identifier.splitOn(string.substring(0, i), '.'))
					.flatMap(statType -> createStatCriterion(statType, Identifier.splitOn(string.substring(i + 1), '.')));
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
		field_1472("integer"),
		field_1471("hearts");

		private final String name;
		private static final Map<String, ScoreboardCriterion.RenderType> CRITERION_TYPES;

		private RenderType(String string2) {
			this.name = string2;
		}

		public String getName() {
			return this.name;
		}

		public static ScoreboardCriterion.RenderType getType(String string) {
			return (ScoreboardCriterion.RenderType)CRITERION_TYPES.getOrDefault(string, field_1472);
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
