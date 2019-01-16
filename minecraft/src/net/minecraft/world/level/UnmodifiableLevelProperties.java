package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_236;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionType;

public class UnmodifiableLevelProperties extends LevelProperties {
	private final LevelProperties properties;

	public UnmodifiableLevelProperties(LevelProperties levelProperties) {
		this.properties = levelProperties;
	}

	@Override
	public CompoundTag cloneWorldTag(@Nullable CompoundTag compoundTag) {
		return this.properties.cloneWorldTag(compoundTag);
	}

	@Override
	public long getSeed() {
		return this.properties.getSeed();
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

	@Environment(EnvType.CLIENT)
	@Override
	public long getSizeOnDisk() {
		return this.properties.getSizeOnDisk();
	}

	@Override
	public CompoundTag getPlayerData() {
		return this.properties.getPlayerData();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getDimension() {
		return this.properties.getDimension();
	}

	@Override
	public String getLevelName() {
		return this.properties.getLevelName();
	}

	@Override
	public int getVersion() {
		return this.properties.getVersion();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long getLastPlayed() {
		return this.properties.getLastPlayed();
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
		return this.properties.getGameMode();
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
		return this.properties.hasStructures();
	}

	@Override
	public boolean isHardcore() {
		return this.properties.isHardcore();
	}

	@Override
	public LevelGeneratorType getGeneratorType() {
		return this.properties.getGeneratorType();
	}

	@Override
	public void setGeneratorType(LevelGeneratorType levelGeneratorType) {
	}

	@Override
	public boolean areCommandsAllowed() {
		return this.properties.areCommandsAllowed();
	}

	@Override
	public void setCommandsAllowed(boolean bl) {
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
		return this.properties.getGameRules();
	}

	@Override
	public Difficulty getDifficulty() {
		return this.properties.getDifficulty();
	}

	@Override
	public void setDifficulty(Difficulty difficulty) {
	}

	@Override
	public boolean isDifficultyLocked() {
		return this.properties.isDifficultyLocked();
	}

	@Override
	public void setDifficultyLocked(boolean bl) {
	}

	@Override
	public class_236<MinecraftServer> method_143() {
		return this.properties.method_143();
	}

	@Override
	public void setWorldData(DimensionType dimensionType, CompoundTag compoundTag) {
		this.properties.setWorldData(dimensionType, compoundTag);
	}

	@Override
	public CompoundTag getWorldData(DimensionType dimensionType) {
		return this.properties.getWorldData(dimensionType);
	}
}
