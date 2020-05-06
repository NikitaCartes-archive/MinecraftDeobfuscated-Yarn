package net.minecraft;

import com.google.common.hash.Hashing;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.LevelGeneratorOptions;
import net.minecraft.world.level.LevelGeneratorType;

public interface class_5217 {
	long getSeed();

	static long method_27418(long l) {
		return Hashing.sha256().hashLong(l).asLong();
	}

	int getSpawnX();

	int getSpawnY();

	int getSpawnZ();

	long getTime();

	long getTimeOfDay();

	boolean isThundering();

	boolean isRaining();

	void setRaining(boolean raining);

	boolean isHardcore();

	LevelGeneratorType getGeneratorType();

	LevelGeneratorOptions getGeneratorOptions();

	GameRules getGameRules();

	Difficulty getDifficulty();

	boolean isDifficultyLocked();

	default void populateCrashReport(CrashReportSection crashReportSection) {
		crashReportSection.add("Level seed", (CrashCallable<String>)(() -> String.valueOf(this.getSeed())));
		crashReportSection.add("Level generator options", (CrashCallable<String>)(() -> this.getGeneratorOptions().getDynamic().toString()));
		crashReportSection.add(
			"Level spawn location", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(this.getSpawnX(), this.getSpawnY(), this.getSpawnZ()))
		);
		crashReportSection.add("Level time", (CrashCallable<String>)(() -> String.format("%d game time, %d day time", this.getTime(), this.getTimeOfDay())));
	}
}
