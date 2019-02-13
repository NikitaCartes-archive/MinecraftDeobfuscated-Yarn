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
	public static final ScoreboardCriterion HEALTH = new ScoreboardCriterion("health", true, ScoreboardCriterion.Type.HEARTS);
	public static final ScoreboardCriterion FOOD = new ScoreboardCriterion("food", true, ScoreboardCriterion.Type.INTEGER);
	public static final ScoreboardCriterion AIR = new ScoreboardCriterion("air", true, ScoreboardCriterion.Type.INTEGER);
	public static final ScoreboardCriterion ARMOR = new ScoreboardCriterion("armor", true, ScoreboardCriterion.Type.INTEGER);
	public static final ScoreboardCriterion XP = new ScoreboardCriterion("xp", true, ScoreboardCriterion.Type.INTEGER);
	public static final ScoreboardCriterion LEVEL = new ScoreboardCriterion("level", true, ScoreboardCriterion.Type.INTEGER);
	public static final ScoreboardCriterion[] TEAM_KILLS = new ScoreboardCriterion[]{
		new ScoreboardCriterion("teamkill." + TextFormat.BLACK.getFormatName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1058.getFormatName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1077.getFormatName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1062.getFormatName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1079.getFormatName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1064.getFormatName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1065.getFormatName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1080.getFormatName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1063.getFormatName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1078.getFormatName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1060.getFormatName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1075.getFormatName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1061.getFormatName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1076.getFormatName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1054.getFormatName()),
		new ScoreboardCriterion("teamkill." + TextFormat.field_1068.getFormatName())
	};
	public static final ScoreboardCriterion[] KILLED_BY_TEAMS = new ScoreboardCriterion[]{
		new ScoreboardCriterion("killedByTeam." + TextFormat.BLACK.getFormatName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1058.getFormatName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1077.getFormatName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1062.getFormatName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1079.getFormatName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1064.getFormatName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1065.getFormatName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1080.getFormatName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1063.getFormatName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1078.getFormatName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1060.getFormatName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1075.getFormatName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1061.getFormatName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1076.getFormatName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1054.getFormatName()),
		new ScoreboardCriterion("killedByTeam." + TextFormat.field_1068.getFormatName())
	};
	private final String name;
	private final boolean readOnly;
	private final ScoreboardCriterion.Type criterionType;

	public ScoreboardCriterion(String string) {
		this(string, false, ScoreboardCriterion.Type.INTEGER);
	}

	protected ScoreboardCriterion(String string, boolean bl, ScoreboardCriterion.Type type) {
		this.name = string;
		this.readOnly = bl;
		this.criterionType = type;
		OBJECTIVES.put(string, this);
	}

	public static Optional<ScoreboardCriterion> method_1224(String string) {
		if (OBJECTIVES.containsKey(string)) {
			return Optional.of(OBJECTIVES.get(string));
		} else {
			int i = string.indexOf(58);
			return i < 0
				? Optional.empty()
				: Registry.STAT_TYPE
					.getOrEmpty(Identifier.createSplit(string.substring(0, i), '.'))
					.flatMap(statType -> method_1223(statType, Identifier.createSplit(string.substring(i + 1), '.')));
		}
	}

	private static <T> Optional<ScoreboardCriterion> method_1223(StatType<T> statType, Identifier identifier) {
		return statType.getRegistry().getOrEmpty(identifier).map(statType::getOrCreateStat);
	}

	public String getName() {
		return this.name;
	}

	public boolean isReadOnly() {
		return this.readOnly;
	}

	public ScoreboardCriterion.Type getCriterionType() {
		return this.criterionType;
	}

	public static enum Type {
		INTEGER("integer"),
		HEARTS("hearts");

		private final String name;
		private static final Map<String, ScoreboardCriterion.Type> field_1470;

		private Type(String string2) {
			this.name = string2;
		}

		public String getName() {
			return this.name;
		}

		public static ScoreboardCriterion.Type method_1229(String string) {
			return (ScoreboardCriterion.Type)field_1470.getOrDefault(string, INTEGER);
		}

		static {
			Builder<String, ScoreboardCriterion.Type> builder = ImmutableMap.builder();

			for (ScoreboardCriterion.Type type : values()) {
				builder.put(type.name, type);
			}

			field_1470 = builder.build();
		}
	}
}
