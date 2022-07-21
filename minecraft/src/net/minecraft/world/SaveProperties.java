package net.minecraft.world;

import com.mojang.serialization.Lifecycle;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.ServerWorldProperties;

public interface SaveProperties {
	int ANVIL_FORMAT_ID = 19133;
	int MCREGION_FORMAT_ID = 19132;

	DataPackSettings getDataPackSettings();

	void updateLevelInfo(DataPackSettings dataPackSettings);

	boolean isModded();

	Set<String> getServerBrands();

	void addServerBrand(String brand, boolean modded);

	default void populateCrashReport(CrashReportSection section) {
		section.add("Known server brands", (CrashCallable<String>)(() -> String.join(", ", this.getServerBrands())));
		section.add("Level was modded", (CrashCallable<String>)(() -> Boolean.toString(this.isModded())));
		section.add("Level storage version", (CrashCallable<String>)(() -> {
			int i = this.getVersion();
			return String.format(Locale.ROOT, "0x%05X - %s", i, this.getFormatName(i));
		}));
	}

	default String getFormatName(int id) {
		switch (id) {
			case 19132:
				return "McRegion";
			case 19133:
				return "Anvil";
			default:
				return "Unknown?";
		}
	}

	@Nullable
	NbtCompound getCustomBossEvents();

	void setCustomBossEvents(@Nullable NbtCompound customBossEvents);

	ServerWorldProperties getMainWorldProperties();

	LevelInfo getLevelInfo();

	NbtCompound cloneWorldNbt(DynamicRegistryManager registryManager, @Nullable NbtCompound playerNbt);

	boolean isHardcore();

	int getVersion();

	String getLevelName();

	GameMode getGameMode();

	void setGameMode(GameMode gameMode);

	boolean areCommandsAllowed();

	Difficulty getDifficulty();

	void setDifficulty(Difficulty difficulty);

	boolean isDifficultyLocked();

	void setDifficultyLocked(boolean difficultyLocked);

	GameRules getGameRules();

	@Nullable
	NbtCompound getPlayerData();

	NbtCompound getDragonFight();

	void setDragonFight(NbtCompound dragonFight);

	GeneratorOptions getGeneratorOptions();

	Lifecycle getLifecycle();
}
