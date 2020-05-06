package net.minecraft.world.level;

import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;

public final class LevelInfo {
	private final String name;
	private final long seed;
	private final GameMode gameMode;
	private final boolean structures;
	private final boolean hardcore;
	private final LevelGeneratorOptions generatorOptions;
	private final Difficulty difficulty;
	private boolean commands;
	private boolean bonusChest;
	private final GameRules gameRules;

	public LevelInfo(
		String name, long seed, GameMode gameMode, boolean structures, boolean hardcore, Difficulty difficulty, LevelGeneratorOptions generatorOptions
	) {
		this(name, seed, gameMode, structures, hardcore, difficulty, generatorOptions, new GameRules());
	}

	public LevelInfo(
		String name,
		long seed,
		GameMode gameMode,
		boolean structures,
		boolean hardcore,
		Difficulty difficulty,
		LevelGeneratorOptions generatorOptions,
		GameRules gameRules
	) {
		this.name = name;
		this.seed = seed;
		this.gameMode = gameMode;
		this.structures = structures;
		this.hardcore = hardcore;
		this.generatorOptions = generatorOptions;
		this.difficulty = difficulty;
		this.gameRules = gameRules;
	}

	public LevelInfo setBonusChest() {
		this.bonusChest = true;
		return this;
	}

	public LevelInfo enableCommands() {
		this.commands = true;
		return this;
	}

	public boolean hasBonusChest() {
		return this.bonusChest;
	}

	public long getSeed() {
		return this.seed;
	}

	public GameMode getGameMode() {
		return this.gameMode;
	}

	public boolean isHardcore() {
		return this.hardcore;
	}

	public boolean hasStructures() {
		return this.structures;
	}

	public LevelGeneratorOptions getGeneratorOptions() {
		return this.generatorOptions;
	}

	public boolean allowCommands() {
		return this.commands;
	}

	public String getLevelName() {
		return this.name;
	}

	public Difficulty getDifficulty() {
		return this.difficulty;
	}

	public GameRules getGameRules() {
		return this.gameRules;
	}
}
