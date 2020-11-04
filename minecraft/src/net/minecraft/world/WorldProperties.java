package net.minecraft.world;

import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReportSection;

public interface WorldProperties {
	int getSpawnX();

	int getSpawnY();

	int getSpawnZ();

	float getSpawnAngle();

	long getTime();

	long getTimeOfDay();

	boolean isThundering();

	boolean isRaining();

	void setRaining(boolean raining);

	boolean isHardcore();

	GameRules getGameRules();

	Difficulty getDifficulty();

	boolean isDifficultyLocked();

	default void populateCrashReport(CrashReportSection reportSection, HeightLimitView heightLimitView) {
		reportSection.add(
			"Level spawn location",
			(CrashCallable<String>)(() -> CrashReportSection.createPositionString(heightLimitView, this.getSpawnX(), this.getSpawnY(), this.getSpawnZ()))
		);
		reportSection.add("Level time", (CrashCallable<String>)(() -> String.format("%d game time, %d day time", this.getTime(), this.getTimeOfDay())));
	}
}
