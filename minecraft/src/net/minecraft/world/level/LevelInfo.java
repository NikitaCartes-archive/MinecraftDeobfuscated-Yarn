package net.minecraft.world.level;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.GameMode;

public final class LevelInfo {
	private final long seed;
	private final GameMode gameMode;
	private final boolean structures;
	private final boolean hardcore;
	private final LevelGeneratorOptions generatorOptions;
	private boolean commands;
	private boolean bonusChest;

	public LevelInfo(long seed, GameMode gameMode, boolean structures, boolean hardcore, LevelGeneratorOptions generatorOptions) {
		this.seed = seed;
		this.gameMode = gameMode;
		this.structures = structures;
		this.hardcore = hardcore;
		this.generatorOptions = generatorOptions;
	}

	public LevelInfo(LevelProperties properties) {
		this(properties.getSeed(), properties.getGameMode(), properties.hasStructures(), properties.isHardcore(), properties.getGeneratorOptions());
	}

	public LevelInfo setBonusChest() {
		this.bonusChest = true;
		return this;
	}

	@Environment(EnvType.CLIENT)
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
}
