package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.timer.Timer;

public class UnmodifiableLevelProperties extends LevelProperties {
	private final LevelProperties field_139;

	public UnmodifiableLevelProperties(LevelProperties levelProperties) {
		this.field_139 = levelProperties;
	}

	@Override
	public CompoundTag cloneWorldTag(@Nullable CompoundTag compoundTag) {
		return this.field_139.cloneWorldTag(compoundTag);
	}

	@Override
	public long getSeed() {
		return this.field_139.getSeed();
	}

	@Override
	public int getSpawnX() {
		return this.field_139.getSpawnX();
	}

	@Override
	public int getSpawnY() {
		return this.field_139.getSpawnY();
	}

	@Override
	public int getSpawnZ() {
		return this.field_139.getSpawnZ();
	}

	@Override
	public long getTime() {
		return this.field_139.getTime();
	}

	@Override
	public long getTimeOfDay() {
		return this.field_139.getTimeOfDay();
	}

	@Override
	public CompoundTag getPlayerData() {
		return this.field_139.getPlayerData();
	}

	@Override
	public String getLevelName() {
		return this.field_139.getLevelName();
	}

	@Override
	public int getVersion() {
		return this.field_139.getVersion();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long getLastPlayed() {
		return this.field_139.getLastPlayed();
	}

	@Override
	public boolean isThundering() {
		return this.field_139.isThundering();
	}

	@Override
	public int getThunderTime() {
		return this.field_139.getThunderTime();
	}

	@Override
	public boolean isRaining() {
		return this.field_139.isRaining();
	}

	@Override
	public int getRainTime() {
		return this.field_139.getRainTime();
	}

	@Override
	public GameMode getGameMode() {
		return this.field_139.getGameMode();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setSpawnX(int i) {
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setSpawnY(int i) {
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setSpawnZ(int i) {
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
	public void setLevelName(String string) {
	}

	@Override
	public void setVersion(int i) {
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
	public boolean hasStructures() {
		return this.field_139.hasStructures();
	}

	@Override
	public boolean isHardcore() {
		return this.field_139.isHardcore();
	}

	@Override
	public LevelGeneratorType getGeneratorType() {
		return this.field_139.getGeneratorType();
	}

	@Override
	public void setGeneratorType(LevelGeneratorType levelGeneratorType) {
	}

	@Override
	public boolean areCommandsAllowed() {
		return this.field_139.areCommandsAllowed();
	}

	@Override
	public void setCommandsAllowed(boolean bl) {
	}

	@Override
	public boolean isInitialized() {
		return this.field_139.isInitialized();
	}

	@Override
	public void setInitialized(boolean bl) {
	}

	@Override
	public GameRules getGameRules() {
		return this.field_139.getGameRules();
	}

	@Override
	public Difficulty getDifficulty() {
		return this.field_139.getDifficulty();
	}

	@Override
	public void setDifficulty(Difficulty difficulty) {
	}

	@Override
	public boolean isDifficultyLocked() {
		return this.field_139.isDifficultyLocked();
	}

	@Override
	public void setDifficultyLocked(boolean bl) {
	}

	@Override
	public Timer<MinecraftServer> method_143() {
		return this.field_139.method_143();
	}

	@Override
	public void setWorldData(DimensionType dimensionType, CompoundTag compoundTag) {
		this.field_139.setWorldData(dimensionType, compoundTag);
	}

	@Override
	public CompoundTag getWorldData(DimensionType dimensionType) {
		return this.field_139.getWorldData(dimensionType);
	}

	@Override
	public void populateCrashReport(CrashReportSection crashReportSection) {
		crashReportSection.add("Derived", true);
		this.field_139.populateCrashReport(crashReportSection);
	}
}
