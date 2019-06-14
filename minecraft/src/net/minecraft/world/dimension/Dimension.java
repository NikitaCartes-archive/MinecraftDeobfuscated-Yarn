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
	private final DimensionType field_13055;
	protected boolean waterVaporizes;
	protected boolean isNether;
	protected final float[] lightLevelToBrightness = new float[16];
	private final float[] backgroundColor = new float[4];

	public Dimension(World world, DimensionType dimensionType) {
		this.world = world;
		this.field_13055 = dimensionType;
		this.initializeLightLevelToBrightness();
	}

	protected void initializeLightLevelToBrightness() {
		float f = 0.0F;

		for (int i = 0; i <= 15; i++) {
			float g = 1.0F - (float)i / 15.0F;
			this.lightLevelToBrightness[i] = (1.0F - g) / (g * 3.0F + 1.0F) * 1.0F + 0.0F;
		}
	}

	public int getMoonPhase(long l) {
		return (int)(l / 24000L % 8L + 8L) % 8;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public float[] getBackgroundColor(float f, float g) {
		float h = 0.4F;
		float i = MathHelper.cos(f * (float) (Math.PI * 2)) - 0.0F;
		float j = -0.0F;
		if (i >= -0.4F && i <= 0.4F) {
			float k = (i - -0.0F) / 0.4F * 0.5F + 0.5F;
			float l = 1.0F - (1.0F - MathHelper.sin(k * (float) Math.PI)) * 0.99F;
			l *= l;
			this.backgroundColor[0] = k * 0.3F + 0.7F;
			this.backgroundColor[1] = k * k * 0.7F + 0.2F;
			this.backgroundColor[2] = k * k * 0.0F + 0.2F;
			this.backgroundColor[3] = l;
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
	public boolean method_12449() {
		return true;
	}

	@Nullable
	public BlockPos getForcedSpawnPoint() {
		return null;
	}

	@Environment(EnvType.CLIENT)
	public double getHorizonShadingRatio() {
		return this.world.method_8401().getGeneratorType() == LevelGeneratorType.FLAT ? 1.0 : 0.03125;
	}

	public boolean doesWaterVaporize() {
		return this.waterVaporizes;
	}

	public boolean hasSkyLight() {
		return this.field_13055.hasSkyLight();
	}

	public boolean isNether() {
		return this.isNether;
	}

	public float[] getLightLevelToBrightness() {
		return this.lightLevelToBrightness;
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
	public abstract BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean bl);

	@Nullable
	public abstract BlockPos getTopSpawningBlockPosition(int i, int j, boolean bl);

	public abstract float getSkyAngle(long l, float f);

	public abstract boolean hasVisibleSky();

	@Environment(EnvType.CLIENT)
	public abstract Vec3d method_12445(float f, float g);

	public abstract boolean canPlayersSleep();

	@Environment(EnvType.CLIENT)
	public abstract boolean shouldRenderFog(int i, int j);

	public abstract DimensionType method_12460();
}
