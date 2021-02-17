package net.minecraft.world;

import com.mojang.serialization.Lifecycle;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.ServerWorldProperties;

public interface SaveProperties {
	DataPackSettings getDataPackSettings();

	void updateLevelInfo(DataPackSettings dataPackSettings);

	boolean isModded();

	Set<String> getServerBrands();

	void addServerBrand(String brand, boolean modded);

	default void populateCrashReport(CrashReportSection crashReportSection) {
		crashReportSection.add("Known server brands", (CrashCallable<String>)(() -> String.join(", ", this.getServerBrands())));
		crashReportSection.add("Level was modded", (CrashCallable<String>)(() -> Boolean.toString(this.isModded())));
		crashReportSection.add("Level storage version", (CrashCallable<String>)(() -> {
			int i = this.getVersion();
			return String.format("0x%05X - %s", i, this.getFormatName(i));
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
	CompoundTag getCustomBossEvents();

	void setCustomBossEvents(@Nullable CompoundTag tag);

	ServerWorldProperties getMainWorldProperties();

	@Environment(EnvType.CLIENT)
	LevelInfo getLevelInfo();

	CompoundTag cloneWorldNbt(DynamicRegistryManager registryManager, @Nullable CompoundTag playerTag);

	boolean isHardcore();

	int getVersion();

	String getLevelName();

	GameMode getGameMode();

	void setGameMode(GameMode gameMode);

	boolean areCommandsAllowed();

	Difficulty getDifficulty();

	void setDifficulty(Difficulty difficulty);

	boolean isDifficultyLocked();

	void setDifficultyLocked(boolean locked);

	GameRules getGameRules();

	CompoundTag getPlayerData();

	CompoundTag getDragonFight();

	void setDragonFight(CompoundTag tag);

	GeneratorOptions getGeneratorOptions();

	@Environment(EnvType.CLIENT)
	Lifecycle getLifecycle();
}
