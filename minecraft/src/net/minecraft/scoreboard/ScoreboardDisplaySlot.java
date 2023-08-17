package net.minecraft.scoreboard;

import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public enum ScoreboardDisplaySlot implements StringIdentifiable {
	LIST(0, "list"),
	SIDEBAR(1, "sidebar"),
	BELOW_NAME(2, "below_name"),
	TEAM_BLACK(3, "sidebar.team.black"),
	TEAM_DARK_BLUE(4, "sidebar.team.dark_blue"),
	TEAM_DARK_GREEN(5, "sidebar.team.dark_green"),
	TEAM_DARK_AQUA(6, "sidebar.team.dark_aqua"),
	TEAM_DARK_RED(7, "sidebar.team.dark_red"),
	TEAM_DARK_PURPLE(8, "sidebar.team.dark_purple"),
	TEAM_GOLD(9, "sidebar.team.gold"),
	TEAM_GRAY(10, "sidebar.team.gray"),
	TEAM_DARK_GRAY(11, "sidebar.team.dark_gray"),
	TEAM_BLUE(12, "sidebar.team.blue"),
	TEAM_GREEN(13, "sidebar.team.green"),
	TEAM_AQUA(14, "sidebar.team.aqua"),
	TEAM_RED(15, "sidebar.team.red"),
	TEAM_LIGHT_PURPLE(16, "sidebar.team.light_purple"),
	TEAM_YELLOW(17, "sidebar.team.yellow"),
	TEAM_WHITE(18, "sidebar.team.white");

	public static final StringIdentifiable.EnumCodec<ScoreboardDisplaySlot> CODEC = StringIdentifiable.createCodec(ScoreboardDisplaySlot::values);
	public static final IntFunction<ScoreboardDisplaySlot> FROM_ID = ValueLists.createIdToValueFunction(
		ScoreboardDisplaySlot::getId, values(), ValueLists.OutOfBoundsHandling.ZERO
	);
	private final int id;
	private final String name;

	private ScoreboardDisplaySlot(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return this.id;
	}

	@Override
	public String asString() {
		return this.name;
	}

	@Nullable
	public static ScoreboardDisplaySlot fromFormatting(Formatting formatting) {
		return switch (formatting) {
			case BLACK -> TEAM_BLACK;
			case DARK_BLUE -> TEAM_DARK_BLUE;
			case DARK_GREEN -> TEAM_DARK_GREEN;
			case DARK_AQUA -> TEAM_DARK_AQUA;
			case DARK_RED -> TEAM_DARK_RED;
			case DARK_PURPLE -> TEAM_DARK_PURPLE;
			case GOLD -> TEAM_GOLD;
			case GRAY -> TEAM_GRAY;
			case DARK_GRAY -> TEAM_DARK_GRAY;
			case BLUE -> TEAM_BLUE;
			case GREEN -> TEAM_GREEN;
			case AQUA -> TEAM_AQUA;
			case RED -> TEAM_RED;
			case LIGHT_PURPLE -> TEAM_LIGHT_PURPLE;
			case YELLOW -> TEAM_YELLOW;
			case WHITE -> TEAM_WHITE;
			case BOLD, ITALIC, UNDERLINE, RESET, OBFUSCATED, STRIKETHROUGH -> null;
		};
	}
}
