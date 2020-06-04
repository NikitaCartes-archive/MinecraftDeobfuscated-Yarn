package net.minecraft.world.level;

import com.mojang.serialization.Dynamic;
import net.minecraft.class_5359;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;

public final class LevelInfo {
	private final String name;
	private final GameMode gameMode;
	private final boolean structures;
	private final Difficulty difficulty;
	private final boolean hardcore;
	private final GameRules gameRules;
	private final class_5359 field_25403;

	public LevelInfo(String name, GameMode gameMode, boolean bl, Difficulty difficulty, boolean bl2, GameRules gameRules, class_5359 arg) {
		this.name = name;
		this.gameMode = gameMode;
		this.structures = bl;
		this.difficulty = difficulty;
		this.hardcore = bl2;
		this.gameRules = gameRules;
		this.field_25403 = arg;
	}

	public static LevelInfo method_28383(Dynamic<?> dynamic, class_5359 arg) {
		GameMode gameMode = GameMode.byId(dynamic.get("GameType").asInt(0));
		return new LevelInfo(
			dynamic.get("LevelName").asString(""),
			gameMode,
			dynamic.get("hardcore").asBoolean(false),
			(Difficulty)dynamic.get("Difficulty").asNumber().map(number -> Difficulty.byOrdinal(number.byteValue())).result().orElse(Difficulty.NORMAL),
			dynamic.get("allowCommands").asBoolean(gameMode == GameMode.CREATIVE),
			new GameRules(dynamic.get("GameRules")),
			arg
		);
	}

	public String getLevelName() {
		return this.name;
	}

	public GameMode getGameMode() {
		return this.gameMode;
	}

	public boolean hasStructures() {
		return this.structures;
	}

	public Difficulty getDifficulty() {
		return this.difficulty;
	}

	public boolean isHardcore() {
		return this.hardcore;
	}

	public GameRules getGameRules() {
		return this.gameRules;
	}

	public class_5359 method_29558() {
		return this.field_25403;
	}

	public LevelInfo method_28382(GameMode gameMode) {
		return new LevelInfo(this.name, gameMode, this.structures, this.difficulty, this.hardcore, this.gameRules, this.field_25403);
	}

	public LevelInfo method_28381(Difficulty difficulty) {
		return new LevelInfo(this.name, this.gameMode, this.structures, difficulty, this.hardcore, this.gameRules, this.field_25403);
	}

	public LevelInfo method_29557(class_5359 arg) {
		return new LevelInfo(this.name, this.gameMode, this.structures, this.difficulty, this.hardcore, this.gameRules, arg);
	}

	public LevelInfo method_28385() {
		return new LevelInfo(this.name, this.gameMode, this.structures, this.difficulty, this.hardcore, this.gameRules.copy(), this.field_25403);
	}
}
