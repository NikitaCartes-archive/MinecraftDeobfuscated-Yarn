package net.minecraft;

import com.google.common.hash.Hashing;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.level.LevelGeneratorOptions;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.timer.Timer;

public interface class_5217 {
	long getSeed();

	static long method_27418(long l) {
		return Hashing.sha256().hashLong(l).asLong();
	}

	int getSpawnX();

	void method_27416(int i);

	int getSpawnY();

	void method_27417(int i);

	int getSpawnZ();

	void method_27419(int i);

	default void setSpawnPos(BlockPos blockPos) {
		this.method_27416(blockPos.getX());
		this.method_27417(blockPos.getY());
		this.method_27419(blockPos.getZ());
	}

	long getTime();

	void setTime(long l);

	long getTimeOfDay();

	void setTimeOfDay(long l);

	String getLevelName();

	int getClearWeatherTime();

	void setClearWeatherTime(int i);

	boolean isThundering();

	void setThundering(boolean bl);

	int getThunderTime();

	void setThunderTime(int i);

	boolean isRaining();

	void setRaining(boolean bl);

	int getRainTime();

	void setRainTime(int i);

	GameMode getGameMode();

	boolean method_27420();

	void setGameMode(GameMode gameMode);

	boolean isHardcore();

	LevelGeneratorType getGeneratorType();

	LevelGeneratorOptions method_27421();

	boolean areCommandsAllowed();

	boolean isInitialized();

	void setInitialized(boolean bl);

	GameRules getGameRules();

	WorldBorder.class_5200 method_27422();

	void method_27415(WorldBorder.class_5200 arg);

	Difficulty getDifficulty();

	boolean isDifficultyLocked();

	Timer<MinecraftServer> getScheduledEvents();

	default void populateCrashReport(CrashReportSection crashReportSection) {
		crashReportSection.add("Level name", this::getLevelName);
		crashReportSection.add("Level seed", (CrashCallable<String>)(() -> String.valueOf(this.getSeed())));
		crashReportSection.add(
			"Level generator",
			(CrashCallable<String>)(() -> {
				LevelGeneratorType levelGeneratorType = this.method_27421().getType();
				return String.format(
					"ID %02d - %s, ver %d. Features enabled: %b",
					levelGeneratorType.getId(),
					levelGeneratorType.getName(),
					levelGeneratorType.getVersion(),
					this.method_27420()
				);
			})
		);
		crashReportSection.add("Level generator options", (CrashCallable<String>)(() -> this.method_27421().getDynamic().toString()));
		crashReportSection.add(
			"Level spawn location", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(this.getSpawnX(), this.getSpawnY(), this.getSpawnZ()))
		);
		crashReportSection.add("Level time", (CrashCallable<String>)(() -> String.format("%d game time, %d day time", this.getTime(), this.getTimeOfDay())));
		crashReportSection.add(
			"Level weather",
			(CrashCallable<String>)(() -> String.format(
					"Rain time: %d (now: %b), thunder time: %d (now: %b)", this.getRainTime(), this.isRaining(), this.getThunderTime(), this.isThundering()
				))
		);
		crashReportSection.add(
			"Level game mode",
			(CrashCallable<String>)(() -> String.format(
					"Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.getGameMode().getName(), this.getGameMode().getId(), this.isHardcore(), this.areCommandsAllowed()
				))
		);
	}

	CompoundTag getWorldData();

	void setWorldData(CompoundTag compoundTag);

	int getWanderingTraderSpawnDelay();

	void setWanderingTraderSpawnDelay(int i);

	int getWanderingTraderSpawnChance();

	void setWanderingTraderSpawnChance(int i);

	void setWanderingTraderId(UUID uUID);
}
