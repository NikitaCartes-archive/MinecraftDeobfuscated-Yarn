package net.minecraft.world.dimension;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelGeneratorType;

public abstract class Dimension {
	public static final float[] MOON_PHASE_TO_SIZE = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
	public static final Vector3f field_23480 = new Vector3f(1.0F, 1.0F, 1.0F);
	protected final World world;
	private final DimensionType type;
	protected boolean waterVaporizes;
	protected boolean isNether;
	protected final float[] lightLevelToBrightness = new float[16];
	private final float[] backgroundColor = new float[4];
	private static final Vector3f field_23481 = new Vector3f(1.0F, 1.0F, 1.0F);

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

	/**
	 * Modify the fog color offered (usually by the biome).
	 * 
	 * <p>The overworld slightly whiteshifts and blueshifts this color; the
	 * nether doesn't touch it; the end significantly blackshifts this color.
	 */
	@Environment(EnvType.CLIENT)
	public abstract Vec3d modifyFogColor(Vec3d vec3d, float tickDelta);

	public abstract boolean canPlayersSleep();

	@Environment(EnvType.CLIENT)
	public abstract boolean isFogThick(int x, int z);

	@Environment(EnvType.CLIENT)
	public void method_26493(int i, int j, Vector3f vector3f) {
	}

	public float method_26497(Direction direction, boolean bl) {
		if (!bl) {
			return 1.0F;
		} else {
			switch (direction) {
				case DOWN:
					return 0.5F;
				case UP:
					return 1.0F;
				case NORTH:
				case SOUTH:
					return 0.8F;
				case WEST:
				case EAST:
					return 0.6F;
				default:
					return 1.0F;
			}
		}
	}

	public final DimensionType getType() {
		return this.type;
	}

	public <T> Dynamic<T> method_26496(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("type"),
					dynamicOps.createString(Registry.DIMENSION_TYPE.getId(this.getType()).toString()),
					dynamicOps.createString("generator"),
					this.createChunkGenerator().method_26489(dynamicOps).getValue()
				)
			)
		);
	}

	public Stream<Biome> method_26498() {
		return this.createChunkGenerator().getBiomeSource().method_26468();
	}

	@Environment(EnvType.CLIENT)
	public Vector3f method_26495(BlockState blockState, BlockPos blockPos) {
		return field_23480;
	}

	@Environment(EnvType.CLIENT)
	public <T extends LivingEntity> Vector3f method_26494(T livingEntity) {
		return field_23480;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_26499() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	public float method_26500() {
		return 30.0F;
	}

	@Environment(EnvType.CLIENT)
	public float method_26501() {
		return 20.0F;
	}

	@Environment(EnvType.CLIENT)
	public Vector3f method_26502() {
		return field_23481;
	}

	@Environment(EnvType.CLIENT)
	public Vector3f method_26503() {
		return field_23481;
	}
}
