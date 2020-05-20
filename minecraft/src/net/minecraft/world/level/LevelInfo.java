package net.minecraft.world.level;

import com.mojang.serialization.Dynamic;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GeneratorOptions;

public final class LevelInfo {
	private final String name;
	private final GameMode gameMode;
	private final boolean structures;
	private final Difficulty difficulty;
	private final boolean hardcore;
	private final GameRules gameRules;
	private final GeneratorOptions generatorOptions;

	public LevelInfo(String name, GameMode gameMode, boolean bl, Difficulty difficulty, boolean bl2, GameRules gameRules, GeneratorOptions generatorOptions) {
		this.name = name;
		this.gameMode = gameMode;
		this.structures = bl;
		this.difficulty = difficulty;
		this.hardcore = bl2;
		this.gameRules = gameRules;
		this.generatorOptions = generatorOptions;
	}

	public static LevelInfo method_28383(Dynamic<?> dynamic, GeneratorOptions generatorOptions) {
		GameMode gameMode = GameMode.byId(dynamic.get("GameType").asInt(0));
		return new LevelInfo(
			dynamic.get("LevelName").asString(""),
			gameMode,
			dynamic.get("hardcore").asBoolean(false),
			(Difficulty)dynamic.get("Difficulty").asNumber().map(number -> Difficulty.byOrdinal(number.byteValue())).result().orElse(Difficulty.NORMAL),
			dynamic.get("allowCommands").asBoolean(gameMode == GameMode.CREATIVE),
			new GameRules(dynamic.get("GameRules")),
			generatorOptions
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

	public GeneratorOptions getGeneratorOptions() {
		return this.generatorOptions;
	}

	public LevelInfo method_28382(GameMode gameMode) {
		return new LevelInfo(this.name, gameMode, this.structures, this.difficulty, this.hardcore, this.gameRules, this.generatorOptions);
	}

	public LevelInfo method_28381(Difficulty difficulty) {
		return new LevelInfo(this.name, this.gameMode, this.structures, difficulty, this.hardcore, this.gameRules, this.generatorOptions);
	}

	public LevelInfo method_28385() {
		return new LevelInfo(this.name, this.gameMode, this.structures, this.difficulty, this.hardcore, this.gameRules.copy(), this.generatorOptions);
	}
}
