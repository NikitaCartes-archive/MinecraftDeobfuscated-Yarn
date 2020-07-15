package net.minecraft.world.level;

import com.mojang.serialization.Dynamic;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;

public final class LevelInfo {
	private final String name;
	private final GameMode gameMode;
	private final boolean hardcore;
	private final Difficulty difficulty;
	private final boolean allowCommands;
	private final GameRules gameRules;
	private final DataPackSettings dataPackSettings;

	public LevelInfo(
		String name, GameMode gameMode, boolean hardcore, Difficulty difficulty, boolean allowCommands, GameRules gameRules, DataPackSettings dataPackSettings
	) {
		this.name = name;
		this.gameMode = gameMode;
		this.hardcore = hardcore;
		this.difficulty = difficulty;
		this.allowCommands = allowCommands;
		this.gameRules = gameRules;
		this.dataPackSettings = dataPackSettings;
	}

	public static LevelInfo fromDynamic(Dynamic<?> dynamic, DataPackSettings dataPackSettings) {
		GameMode gameMode = GameMode.byId(dynamic.get("GameType").asInt(0));
		return new LevelInfo(
			dynamic.get("LevelName").asString(""),
			gameMode,
			dynamic.get("hardcore").asBoolean(false),
			(Difficulty)dynamic.get("Difficulty").asNumber().map(number -> Difficulty.byOrdinal(number.byteValue())).result().orElse(Difficulty.NORMAL),
			dynamic.get("allowCommands").asBoolean(gameMode == GameMode.CREATIVE),
			new GameRules(dynamic.get("GameRules")),
			dataPackSettings
		);
	}

	public String getLevelName() {
		return this.name;
	}

	public GameMode getGameMode() {
		return this.gameMode;
	}

	public boolean isHardcore() {
		return this.hardcore;
	}

	public Difficulty getDifficulty() {
		return this.difficulty;
	}

	public boolean areCommandsAllowed() {
		return this.allowCommands;
	}

	public GameRules getGameRules() {
		return this.gameRules;
	}

	public DataPackSettings getDataPackSettings() {
		return this.dataPackSettings;
	}

	public LevelInfo withGameMode(GameMode mode) {
		return new LevelInfo(this.name, mode, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, this.dataPackSettings);
	}

	public LevelInfo withDifficulty(Difficulty difficulty) {
		return new LevelInfo(this.name, this.gameMode, this.hardcore, difficulty, this.allowCommands, this.gameRules, this.dataPackSettings);
	}

	public LevelInfo withDataPackSettings(DataPackSettings dataPackSettings) {
		return new LevelInfo(this.name, this.gameMode, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, dataPackSettings);
	}

	public LevelInfo withCopiedGameRules() {
		return new LevelInfo(this.name, this.gameMode, this.hardcore, this.difficulty, this.allowCommands, this.gameRules.copy(), this.dataPackSettings);
	}
}
