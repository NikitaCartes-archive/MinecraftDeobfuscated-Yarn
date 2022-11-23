package net.minecraft.world.level;

import com.mojang.serialization.Dynamic;
import net.minecraft.resource.DataConfiguration;
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
	private final DataConfiguration dataConfiguration;

	public LevelInfo(
		String name, GameMode gameMode, boolean hardcore, Difficulty difficulty, boolean allowCommands, GameRules gameRules, DataConfiguration dataConfiguration
	) {
		this.name = name;
		this.gameMode = gameMode;
		this.hardcore = hardcore;
		this.difficulty = difficulty;
		this.allowCommands = allowCommands;
		this.gameRules = gameRules;
		this.dataConfiguration = dataConfiguration;
	}

	public static LevelInfo fromDynamic(Dynamic<?> dynamic, DataConfiguration dataConfiguration) {
		GameMode gameMode = GameMode.byId(dynamic.get("GameType").asInt(0));
		return new LevelInfo(
			dynamic.get("LevelName").asString(""),
			gameMode,
			dynamic.get("hardcore").asBoolean(false),
			(Difficulty)dynamic.get("Difficulty").asNumber().map(difficulty -> Difficulty.byId(difficulty.byteValue())).result().orElse(Difficulty.NORMAL),
			dynamic.get("allowCommands").asBoolean(gameMode == GameMode.CREATIVE),
			new GameRules(dynamic.get("GameRules")),
			dataConfiguration
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

	public DataConfiguration getDataConfiguration() {
		return this.dataConfiguration;
	}

	public LevelInfo withGameMode(GameMode mode) {
		return new LevelInfo(this.name, mode, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, this.dataConfiguration);
	}

	public LevelInfo withDifficulty(Difficulty difficulty) {
		return new LevelInfo(this.name, this.gameMode, this.hardcore, difficulty, this.allowCommands, this.gameRules, this.dataConfiguration);
	}

	public LevelInfo withDataConfiguration(DataConfiguration dataConfiguration) {
		return new LevelInfo(this.name, this.gameMode, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, dataConfiguration);
	}

	public LevelInfo withCopiedGameRules() {
		return new LevelInfo(this.name, this.gameMode, this.hardcore, this.difficulty, this.allowCommands, this.gameRules.copy(), this.dataConfiguration);
	}
}
