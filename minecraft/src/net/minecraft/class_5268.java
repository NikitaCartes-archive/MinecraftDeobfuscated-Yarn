package net.minecraft;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.GameMode;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.timer.Timer;

public interface class_5268 extends class_5269 {
	String getLevelName();

	void setThundering(boolean thundering);

	int getRainTime();

	void setRainTime(int rainTime);

	void setThunderTime(int thunderTime);

	int getThunderTime();

	@Override
	default void populateCrashReport(CrashReportSection crashReportSection) {
		class_5269.super.populateCrashReport(crashReportSection);
		crashReportSection.add("Level name", this::getLevelName);
		crashReportSection.add(
			"Level game mode",
			(CrashCallable<String>)(() -> String.format(
					"Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.getGameMode().getName(), this.getGameMode().getId(), this.isHardcore(), this.areCommandsAllowed()
				))
		);
		crashReportSection.add(
			"Level generator",
			(CrashCallable<String>)(() -> {
				LevelGeneratorType levelGeneratorType = this.getGeneratorOptions().getType();
				return String.format(
					"ID %02d - %s, ver %d. Features enabled: %b",
					levelGeneratorType.getId(),
					levelGeneratorType.getName(),
					levelGeneratorType.getVersion(),
					this.hasStructures()
				);
			})
		);
		crashReportSection.add(
			"Level weather",
			(CrashCallable<String>)(() -> String.format(
					"Rain time: %d (now: %b), thunder time: %d (now: %b)", this.getRainTime(), this.isRaining(), this.getThunderTime(), this.isThundering()
				))
		);
	}

	int getClearWeatherTime();

	void setClearWeatherTime(int clearWeatherTime);

	boolean hasStructures();

	CompoundTag getWorldData();

	void setWorldData(CompoundTag tag);

	int getWanderingTraderSpawnDelay();

	void setWanderingTraderSpawnDelay(int wanderingTraderSpawnDelay);

	int getWanderingTraderSpawnChance();

	void setWanderingTraderSpawnChance(int wanderingTraderSpawnChance);

	void setWanderingTraderId(UUID uuid);

	GameMode getGameMode();

	void method_27415(WorldBorder.class_5200 arg);

	WorldBorder.class_5200 method_27422();

	boolean isInitialized();

	void setInitialized(boolean initialized);

	boolean areCommandsAllowed();

	void setGameMode(GameMode gameMode);

	Timer<MinecraftServer> getScheduledEvents();
}
