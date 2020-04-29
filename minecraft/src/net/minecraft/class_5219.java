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

	boolean method_27431();

	Set<String> method_27432();

	void addServerBrand(String string, boolean bl);

	default void populateCrashReport(CrashReportSection crashReportSection) {
		crashReportSection.add("Known server brands", (CrashCallable<String>)(() -> String.join(", ", this.method_27432())));
		crashReportSection.add("Level was modded", (CrashCallable<String>)(() -> Boolean.toString(this.method_27431())));
		crashReportSection.add("Level storage version", (CrashCallable<String>)(() -> {
			int i = this.getVersion();
			return String.format("0x%05X - %s", i, this.method_27440(i));
		}));
	}

	default String method_27440(int i) {
		switch (i) {
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

	void setCustomBossEvents(@Nullable CompoundTag compoundTag);

	class_5268 method_27859();

	LevelInfo method_27433();

	CompoundTag cloneWorldTag(@Nullable CompoundTag compoundTag);

	boolean isHardcore();

	int getVersion();

	String getLevelName();

	GameMode getGameMode();

	void setGameMode(GameMode gameMode);

	@Environment(EnvType.CLIENT)
	long getLastPlayed();

	long getSeed();

	boolean areCommandsAllowed();

	Difficulty getDifficulty();

	void setDifficulty(Difficulty difficulty);

	boolean isDifficultyLocked();

	void setDifficultyLocked(boolean bl);

	GameRules getGameRules();

	CompoundTag getPlayerData();

	CompoundTag method_27434(DimensionType dimensionType);

	void method_27435(DimensionType dimensionType, CompoundTag compoundTag);
}
