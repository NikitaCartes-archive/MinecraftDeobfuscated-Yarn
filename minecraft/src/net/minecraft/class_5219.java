package net.minecraft;

import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelInfo;

public interface class_5219 {
	@Environment(EnvType.CLIENT)
	int getVersionId();

	@Environment(EnvType.CLIENT)
	boolean isVersionSnapshot();

	@Environment(EnvType.CLIENT)
	String getVersionName();

	Set<String> getDisabledDataPacks();

	Set<String> getEnabledDataPacks();

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

	class_5268 method_27859();

	@Environment(EnvType.CLIENT)
	LevelInfo getLevelInfo();

	CompoundTag cloneWorldTag(@Nullable CompoundTag tag);

	boolean isHardcore();

	int getVersion();

	String getLevelName();

	GameMode getGameMode();

	void setGameMode(GameMode gameMode);

	@Environment(EnvType.CLIENT)
	long getLastPlayed();

	boolean areCommandsAllowed();

	Difficulty getDifficulty();

	void setDifficulty(Difficulty difficulty);

	boolean isDifficultyLocked();

	void setDifficultyLocked(boolean locked);

	GameRules getGameRules();

	CompoundTag getPlayerData();

	CompoundTag getWorldData(DimensionType dimensionType);

	void setWorldData(DimensionType dimensionType, CompoundTag tag);

	class_5285 method_28057();
}
