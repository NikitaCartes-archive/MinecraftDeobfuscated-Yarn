package net.minecraft.world.level;

import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.timer.Timer;

public class UnmodifiableLevelProperties implements ServerWorldProperties {
	private final SaveProperties saveProperties;
	private final ServerWorldProperties worldProperties;

	public UnmodifiableLevelProperties(SaveProperties saveProperties, ServerWorldProperties worldProperties) {
		this.saveProperties = saveProperties;
		this.worldProperties = worldProperties;
	}

	@Override
	public int getSpawnX() {
		return this.worldProperties.getSpawnX();
	}

	@Override
	public int getSpawnY() {
		return this.worldProperties.getSpawnY();
	}

	@Override
	public int getSpawnZ() {
		return this.worldProperties.getSpawnZ();
	}

	@Override
	public float getSpawnAngle() {
		return this.worldProperties.getSpawnAngle();
	}

	@Override
	public long getTime() {
		return this.worldProperties.getTime();
	}

	@Override
	public long getTimeOfDay() {
		return this.worldProperties.getTimeOfDay();
	}

	@Override
	public String getLevelName() {
		return this.saveProperties.getLevelName();
	}

	@Override
	public int getClearWeatherTime() {
		return this.worldProperties.getClearWeatherTime();
	}

	@Override
	public void setClearWeatherTime(int clearWeatherTime) {
	}

	@Override
	public boolean isThundering() {
		return this.worldProperties.isThundering();
	}

	@Override
	public int getThunderTime() {
		return this.worldProperties.getThunderTime();
	}

	@Override
	public boolean isRaining() {
		return this.worldProperties.isRaining();
	}

	@Override
	public int getRainTime() {
		return this.worldProperties.getRainTime();
	}

	@Override
	public GameMode getGameMode() {
		return this.saveProperties.getGameMode();
	}

	@Override
	public void setSpawnX(int spawnX) {
	}

	@Override
	public void setSpawnY(int spawnY) {
	}

	@Override
	public void setSpawnZ(int spawnZ) {
	}

	@Override
	public void setSpawnAngle(float spawnAngle) {
	}

	@Override
	public void setTime(long time) {
	}

	@Override
	public void setTimeOfDay(long timeOfDay) {
	}

	@Override
	public void setSpawnPos(BlockPos pos, float angle) {
	}

	@Override
	public void setThundering(boolean thundering) {
	}

	@Override
	public void setThunderTime(int thunderTime) {
	}

	@Override
	public void setRaining(boolean raining) {
	}

	@Override
	public void setRainTime(int rainTime) {
	}

	@Override
	public void setGameMode(GameMode gameMode) {
	}

	@Override
	public boolean isHardcore() {
		return this.saveProperties.isHardcore();
	}

	@Override
	public boolean areCommandsAllowed() {
		return this.saveProperties.areCommandsAllowed();
	}

	@Override
	public boolean isInitialized() {
		return this.worldProperties.isInitialized();
	}

	@Override
	public void setInitialized(boolean initialized) {
	}

	@Override
	public GameRules getGameRules() {
		return this.saveProperties.getGameRules();
	}

	@Override
	public WorldBorder.Properties getWorldBorder() {
		return this.worldProperties.getWorldBorder();
	}

	@Override
	public void setWorldBorder(WorldBorder.Properties worldBorder) {
	}

	@Override
	public Difficulty getDifficulty() {
		return this.saveProperties.getDifficulty();
	}

	@Override
	public boolean isDifficultyLocked() {
		return this.saveProperties.isDifficultyLocked();
	}

	@Override
	public Timer<MinecraftServer> getScheduledEvents() {
		return this.worldProperties.getScheduledEvents();
	}

	@Override
	public int getWanderingTraderSpawnDelay() {
		return 0;
	}

	@Override
	public void setWanderingTraderSpawnDelay(int wanderingTraderSpawnDelay) {
	}

	@Override
	public int getWanderingTraderSpawnChance() {
		return 0;
	}

	@Override
	public void setWanderingTraderSpawnChance(int wanderingTraderSpawnChance) {
	}

	@Override
	public UUID getWanderingTraderId() {
		return null;
	}

	@Override
	public void setWanderingTraderId(UUID wanderingTraderId) {
	}

	@Override
	public void populateCrashReport(CrashReportSection reportSection, HeightLimitView world) {
		reportSection.add("Derived", true);
		this.worldProperties.populateCrashReport(reportSection, world);
	}
}
