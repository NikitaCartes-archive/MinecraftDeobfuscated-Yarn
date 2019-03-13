package net.minecraft.world.level;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.GameMode;

public final class LevelInfo {
	private final long seed;
	private final GameMode gameMode;
	private final boolean structures;
	private final boolean hardcore;
	private final LevelGeneratorType field_9260;
	private boolean commands;
	private boolean bonusChest;
	private JsonElement generatorOptions = new JsonObject();

	public LevelInfo(long l, GameMode gameMode, boolean bl, boolean bl2, LevelGeneratorType levelGeneratorType) {
		this.seed = l;
		this.gameMode = gameMode;
		this.structures = bl;
		this.hardcore = bl2;
		this.field_9260 = levelGeneratorType;
	}

	public LevelInfo(LevelProperties levelProperties) {
		this(
			levelProperties.getSeed(), levelProperties.getGameMode(), levelProperties.hasStructures(), levelProperties.isHardcore(), levelProperties.getGeneratorType()
		);
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

	public LevelInfo setGeneratorOptions(JsonElement jsonElement) {
		this.generatorOptions = jsonElement;
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

	public LevelGeneratorType method_8576() {
		return this.field_9260;
	}

	public boolean allowCommands() {
		return this.commands;
	}

	public JsonElement getGeneratorOptions() {
		return this.generatorOptions;
	}
}
