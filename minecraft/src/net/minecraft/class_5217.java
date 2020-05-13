package net.minecraft;

import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

public interface class_5217 {
	int getSpawnX();

	int getSpawnY();

	int getSpawnZ();

	long getTime();

	long getTimeOfDay();

	boolean isThundering();

	boolean isRaining();

	void setRaining(boolean raining);

	boolean isHardcore();

	GameRules getGameRules();

	Difficulty getDifficulty();

	boolean isDifficultyLocked();

	default void populateCrashReport(CrashReportSection crashReportSection) {
		crashReportSection.add(
			"Level spawn location", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(this.getSpawnX(), this.getSpawnY(), this.getSpawnZ()))
		);
		crashReportSection.add("Level time", (CrashCallable<String>)(() -> String.format("%d game time, %d day time", this.getTime(), this.getTimeOfDay())));
	}
}
