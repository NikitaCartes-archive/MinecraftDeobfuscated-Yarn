package net.minecraft.world.level;

import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.timer.Timer;

public interface ServerWorldProperties extends MutableWorldProperties {
	String getLevelName();

	void setThundering(boolean thundering);

	int getRainTime();

	void setRainTime(int rainTime);

	void setThunderTime(int thunderTime);

	int getThunderTime();

	@Override
	default void populateCrashReport(CrashReportSection reportSection, HeightLimitView world) {
		MutableWorldProperties.super.populateCrashReport(reportSection, world);
		reportSection.add("Level name", this::getLevelName);
		reportSection.add(
			"Level game mode",
			(CrashCallable<String>)(() -> String.format(
					Locale.ROOT,
					"Game mode: %s (ID %d). Hardcore: %b. Commands: %b",
					this.getGameMode().getName(),
					this.getGameMode().getId(),
					this.isHardcore(),
					this.areCommandsAllowed()
				))
		);
		reportSection.add(
			"Level weather",
			(CrashCallable<String>)(() -> String.format(
					Locale.ROOT, "Rain time: %d (now: %b), thunder time: %d (now: %b)", this.getRainTime(), this.isRaining(), this.getThunderTime(), this.isThundering()
				))
		);
	}

	int getClearWeatherTime();

	void setClearWeatherTime(int clearWeatherTime);

	int getWanderingTraderSpawnDelay();

	void setWanderingTraderSpawnDelay(int wanderingTraderSpawnDelay);

	int getWanderingTraderSpawnChance();

	void setWanderingTraderSpawnChance(int wanderingTraderSpawnChance);

	@Nullable
	UUID getWanderingTraderId();

	void setWanderingTraderId(UUID wanderingTraderId);

	GameMode getGameMode();

	void setWorldBorder(WorldBorder.Properties worldBorder);

	WorldBorder.Properties getWorldBorder();

	boolean isInitialized();

	void setInitialized(boolean initialized);

	boolean areCommandsAllowed();

	void setGameMode(GameMode gameMode);

	Timer<MinecraftServer> getScheduledEvents();

	void setTime(long time);

	void setTimeOfDay(long timeOfDay);

	GameRules getGameRules();
}
