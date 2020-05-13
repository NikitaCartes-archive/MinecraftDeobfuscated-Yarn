package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.minecraft.class_5268;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

public abstract class Dimension {
	public static final float[] MOON_PHASE_TO_SIZE = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
	protected final World world;
	private final DimensionType type;
	protected final float[] lightLevelToBrightness = new float[16];

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

	public float getBrightness(int lightLevel) {
		return this.lightLevelToBrightness[lightLevel];
	}

	public abstract float getSkyAngle(long timeOfDay, float tickDelta);

	public WorldBorder createWorldBorder() {
		return new WorldBorder();
	}

	public abstract DimensionType getType();

	@Nullable
	public BlockPos getForcedSpawnPoint() {
		return null;
	}

	public void saveWorldData(class_5268 arg) {
	}

	public void update() {
	}

	@Nullable
	public abstract BlockPos getSpawningBlockInChunk(long l, ChunkPos chunkPos, boolean bl);

	@Nullable
	public abstract BlockPos getTopSpawningBlockPosition(long l, int i, int j, boolean bl);

	public abstract boolean hasVisibleSky();

	public abstract boolean canPlayersSleep();
}
