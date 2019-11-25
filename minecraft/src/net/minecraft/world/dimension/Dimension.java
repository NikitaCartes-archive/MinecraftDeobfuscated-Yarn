package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelGeneratorType;

public abstract class Dimension {
	public static final float[] MOON_PHASE_TO_SIZE = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
	protected final World world;
	private final DimensionType type;
	protected boolean waterVaporizes;
	protected boolean isNether;
	protected final float[] lightLevelToBrightness = new float[16];
	private final float[] backgroundColor = new float[4];

	public Dimension(World world, DimensionType type, float f) {
		this.world = world;
		this.type = type;

		for (int i = 0; i <= 15; i++) {
			float g = (float)i / 15.0F;
			float h = g / (4.0F - 3.0F * g);
			this.lightLevelToBrightness[i] = MathHelper.lerp(f, h, 1.0F);
		}
	}

	public int getMoonPhase(long time) {
		return (int)(time / 24000L % 8L + 8L) % 8;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public float[] getBackgroundColor(float skyAngle, float tickDelta) {
		float f = 0.4F;
		float g = MathHelper.cos(skyAngle * (float) (Math.PI * 2)) - 0.0F;
		float h = -0.0F;
		if (g >= -0.4F && g <= 0.4F) {
			float i = (g - -0.0F) / 0.4F * 0.5F + 0.5F;
			float j = 1.0F - (1.0F - MathHelper.sin(i * (float) Math.PI)) * 0.99F;
			j *= j;
			this.backgroundColor[0] = i * 0.3F + 0.7F;
			this.backgroundColor[1] = i * i * 0.7F + 0.2F;
			this.backgroundColor[2] = i * i * 0.0F + 0.2F;
			this.backgroundColor[3] = j;
			return this.backgroundColor;
		} else {
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	public float getCloudHeight() {
		return 128.0F;
	}

	@Environment(EnvType.CLIENT)
	public boolean hasGround() {
		return true;
	}

	@Nullable
	public BlockPos getForcedSpawnPoint() {
		return null;
	}

	@Environment(EnvType.CLIENT)
	public double getHorizonShadingRatio() {
		return this.world.getLevelProperties().getGeneratorType() == LevelGeneratorType.FLAT ? 1.0 : 0.03125;
	}

	public boolean doesWaterVaporize() {
		return this.waterVaporizes;
	}

	public boolean hasSkyLight() {
		return this.type.hasSkyLight();
	}

	public boolean isNether() {
		return this.isNether;
	}

	public float getBrightness(int lightLevel) {
		return this.lightLevelToBrightness[lightLevel];
	}

	public WorldBorder createWorldBorder() {
		return new WorldBorder();
	}

	public void saveWorldData() {
	}

	public void update() {
	}

	public abstract ChunkGenerator<?> createChunkGenerator();

	@Nullable
	public abstract BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean checkMobSpawnValidity);

	@Nullable
	public abstract BlockPos getTopSpawningBlockPosition(int x, int z, boolean checkMobSpawnValidity);

	public abstract float getSkyAngle(long timeOfDay, float tickDelta);

	public abstract boolean hasVisibleSky();

	@Environment(EnvType.CLIENT)
	public abstract Vec3d getFogColor(float skyAngle, float tickDelta);

	public abstract boolean canPlayersSleep();

	@Environment(EnvType.CLIENT)
	public abstract boolean isFogThick(int x, int z);

	public abstract DimensionType getType();
}
