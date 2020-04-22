package net.minecraft.world.level;

import java.util.UUID;
import net.minecraft.class_5217;
import net.minecraft.class_5219;
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

public class UnmodifiableLevelProperties implements class_5217 {
	private final DimensionType field_24178;
	private final class_5219 field_24179;
	private final class_5217 properties;

	public UnmodifiableLevelProperties(DimensionType dimensionType, class_5219 arg, class_5217 arg2) {
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
	public void setClearWeatherTime(int i) {
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
	public void method_27416(int i) {
	}

	@Override
	public void method_27417(int i) {
	}

	@Override
	public void method_27419(int i) {
	}

	@Override
	public void setTime(long l) {
	}

	@Override
	public void setTimeOfDay(long l) {
	}

	@Override
	public void setSpawnPos(BlockPos blockPos) {
	}

	@Override
	public void setThundering(boolean bl) {
	}

	@Override
	public void setThunderTime(int i) {
	}

	@Override
	public void setRaining(boolean bl) {
	}

	@Override
	public void setRainTime(int i) {
	}

	@Override
	public boolean method_27420() {
		return this.properties.method_27420();
	}

	@Override
	public void setGameMode(GameMode gameMode) {
	}

	@Override
	public boolean isHardcore() {
		return false;
	}

	@Override
	public LevelGeneratorType getGeneratorType() {
		return this.properties.getGeneratorType();
	}

	@Override
	public LevelGeneratorOptions method_27421() {
		return this.properties.method_27421();
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
	public void setInitialized(boolean bl) {
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
	public void setWorldData(CompoundTag compoundTag) {
		this.field_24179.method_27435(this.field_24178, compoundTag);
	}

	@Override
	public CompoundTag getWorldData() {
		return this.field_24179.method_27434(this.field_24178);
	}

	@Override
	public int getWanderingTraderSpawnDelay() {
		return 0;
	}

	@Override
	public void setWanderingTraderSpawnDelay(int i) {
	}

	@Override
	public int getWanderingTraderSpawnChance() {
		return 0;
	}

	@Override
	public void setWanderingTraderSpawnChance(int i) {
	}

	@Override
	public void setWanderingTraderId(UUID uUID) {
	}

	@Override
	public void populateCrashReport(CrashReportSection crashReportSection) {
		crashReportSection.add("Derived", true);
		this.properties.populateCrashReport(crashReportSection);
	}
}
