package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelGeneratorType;

public abstract class Dimension {
	public static final float[] field_13059 = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
	protected final World world;
	private final DimensionType type;
	protected boolean waterVaporizes;
	protected boolean isNether;
	protected final float[] field_13053 = new float[16];
	private final float[] field_13054 = new float[4];

	public Dimension(World world, DimensionType dimensionType) {
		this.world = world;
		this.type = dimensionType;
		this.method_12447();
	}

	protected void method_12447() {
		float f = 0.0F;

		for (int i = 0; i <= 15; i++) {
			float g = 1.0F - (float)i / 15.0F;
			this.field_13053[i] = (1.0F - g) / (g * 3.0F + 1.0F) * 1.0F + 0.0F;
		}
	}

	public int method_12454(long l) {
		return (int)(l / 24000L % 8L + 8L) % 8;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public float[] method_12446(float f, float g) {
		float h = 0.4F;
		float i = MathHelper.cos(f * (float) (Math.PI * 2)) - 0.0F;
		float j = -0.0F;
		if (i >= -0.4F && i <= 0.4F) {
			float k = (i - -0.0F) / 0.4F * 0.5F + 0.5F;
			float l = 1.0F - (1.0F - MathHelper.sin(k * (float) Math.PI)) * 0.99F;
			l *= l;
			this.field_13054[0] = k * 0.3F + 0.7F;
			this.field_13054[1] = k * k * 0.7F + 0.2F;
			this.field_13054[2] = k * k * 0.0F + 0.2F;
			this.field_13054[3] = l;
			return this.field_13054;
		} else {
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_12455() {
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
	public double method_12459() {
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

	public float[] method_12456() {
		return this.field_13053;
	}

	public WorldBorder createWorldBorder() {
		return new WorldBorder();
	}

	public void method_12457(ServerPlayerEntity serverPlayerEntity) {
	}

	public void method_12458(ServerPlayerEntity serverPlayerEntity) {
	}

	public void saveWorldData() {
	}

	public void method_12461() {
	}

	public abstract ChunkGenerator<?> createChunkGenerator();

	@Nullable
	public abstract BlockPos method_12452(ChunkPos chunkPos, boolean bl);

	@Nullable
	public abstract BlockPos method_12444(int i, int j, boolean bl);

	public abstract float getSkyAngle(long l, float f);

	public abstract boolean hasVisibleSky();

	@Environment(EnvType.CLIENT)
	public abstract Vec3d method_12445(float f, float g);

	public abstract boolean method_12448();

	@Environment(EnvType.CLIENT)
	public abstract boolean method_12453(int i, int j);

	public abstract DimensionType getType();
}
