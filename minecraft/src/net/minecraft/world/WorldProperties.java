package net.minecraft.world;

import java.util.Locale;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;

public interface WorldProperties {
	BlockPos getSpawnPos();

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

	default void populateCrashReport(CrashReportSection reportSection, HeightLimitView world) {
		reportSection.add("Level spawn location", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(world, this.getSpawnPos())));
		reportSection.add("Level time", (CrashCallable<String>)(() -> String.format(Locale.ROOT, "%d game time, %d day time", this.getTime(), this.getTimeOfDay())));
	}
}
