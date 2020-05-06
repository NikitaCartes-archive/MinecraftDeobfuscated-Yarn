package net.minecraft.world.level;

import java.util.UUID;
import net.minecraft.class_5219;
import net.minecraft.class_5268;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.timer.Timer;

public class UnmodifiableLevelProperties implements class_5268 {
	private final DimensionType field_24178;
	private final class_5219 field_24179;
	private final class_5268 properties;

	public UnmodifiableLevelProperties(DimensionType dimensionType, class_5219 arg, class_5268 arg2) {
		this.field_24178 = dimensionType;
		this.field_24179 = arg;
		this.properties = arg2;
	}

	@Override
	public long getSeed() {
		return this.field_24179.getSeed();
	}

	@Override
	public int getSpawnX() {
		return this.properties.getSpawnX();
	}

	@Override
	public int getSpawnY() {
		return this.properties.getSpawnY();
	}

	@Override
	public int getSpawnZ() {
		return this.properties.getSpawnZ();
	}

	@Override
	public long getTime() {
		return this.properties.getTime();
	}

	@Override
	public long getTimeOfDay() {
		return this.properties.getTimeOfDay();
	}

	@Override
	public String getLevelName() {
		return this.field_24179.getLevelName();
	}

	@Override
	public int getClearWeatherTime() {
		return this.properties.getClearWeatherTime();
	}

	@Override
	public void setClearWeatherTime(int clearWeatherTime) {
	}

	@Override
	public boolean isThundering() {
		return this.properties.isThundering();
	}

	@Override
	public int getThunderTime() {
		return this.properties.getThunderTime();
	}

	@Override
	public boolean isRaining() {
		return this.properties.isRaining();
	}

	@Override
	public int getRainTime() {
		return this.properties.getRainTime();
	}

	@Override
	public GameMode getGameMode() {
		return this.field_24179.getGameMode();
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
	public void setTime(long time) {
	}

	@Override
	public void setTimeOfDay(long timeOfDay) {
	}

	@Override
	public void setSpawnPos(BlockPos pos) {
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
	public boolean hasStructures() {
		return this.properties.hasStructures();
	}

	@Override
	public void setGameMode(GameMode gameMode) {
	}

	@Override
	public boolean isHardcore() {
		return this.field_24179.isHardcore();
	}

	@Override
	public LevelGeneratorType getGeneratorType() {
		return this.properties.getGeneratorType();
	}

	@Override
	public LevelGeneratorOptions getGeneratorOptions() {
		return this.properties.getGeneratorOptions();
	}

	@Override
	public boolean areCommandsAllowed() {
		return this.field_24179.areCommandsAllowed();
	}

	@Override
	public boolean isInitialized() {
		return this.properties.isInitialized();
	}

	@Override
	public void setInitialized(boolean initialized) {
	}

	@Override
	public GameRules getGameRules() {
		return this.field_24179.getGameRules();
	}

	@Override
	public WorldBorder.class_5200 method_27422() {
		return this.properties.method_27422();
	}

	@Override
	public void method_27415(WorldBorder.class_5200 arg) {
	}

	@Override
	public Difficulty getDifficulty() {
		return this.field_24179.getDifficulty();
	}

	@Override
	public boolean isDifficultyLocked() {
		return this.field_24179.isDifficultyLocked();
	}

	@Override
	public Timer<MinecraftServer> getScheduledEvents() {
		return this.properties.getScheduledEvents();
	}

	@Override
	public void setWorldData(CompoundTag tag) {
		this.field_24179.setWorldData(this.field_24178, tag);
	}

	@Override
	public CompoundTag getWorldData() {
		return this.field_24179.getWorldData(this.field_24178);
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
	public void setWanderingTraderId(UUID uuid) {
	}

	@Override
	public void populateCrashReport(CrashReportSection crashReportSection) {
		crashReportSection.add("Derived", true);
		this.properties.populateCrashReport(crashReportSection);
	}
}
