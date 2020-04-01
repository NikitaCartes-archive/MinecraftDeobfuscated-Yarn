package net.minecraft.world.dimension;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceConfig;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.chunk.CavesChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class TheNetherDimension extends Dimension {
	public TheNetherDimension(World world, DimensionType type) {
		super(world, type, 0.1F);
		this.waterVaporizes = true;
		this.isNether = true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d modifyFogColor(Vec3d vec3d, float tickDelta) {
		return vec3d;
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		CavesChunkGeneratorConfig cavesChunkGeneratorConfig = ChunkGeneratorType.CAVES.createConfig();
		cavesChunkGeneratorConfig.setDefaultBlock(Blocks.NETHERRACK.getDefaultState());
		cavesChunkGeneratorConfig.setDefaultFluid(Blocks.LAVA.getDefaultState());
		MultiNoiseBiomeSourceConfig multiNoiseBiomeSourceConfig = BiomeSourceType.MULTI_NOISE
			.getConfig(this.world.getSeed())
			.withBiomes(
				ImmutableMap.of(
					Biomes.NETHER_WASTES,
					ImmutableList.of(new Biome.MixedNoisePoint(0.0F, 0.0F, 0.0F, -0.5F, 1.0F)),
					Biomes.SOUL_SAND_VALLEY,
					ImmutableList.of(new Biome.MixedNoisePoint(0.0F, 0.0F, 0.0F, 0.5F, 1.0F)),
					Biomes.CRIMSON_FOREST,
					ImmutableList.of(new Biome.MixedNoisePoint(0.0F, -0.5F, 0.0F, 0.0F, 1.0F)),
					Biomes.WARPED_FOREST,
					ImmutableList.of(new Biome.MixedNoisePoint(0.0F, 0.5F, 0.0F, 0.0F, 1.0F))
				)
			);
		return ChunkGeneratorType.CAVES.create(this.world, BiomeSourceType.MULTI_NOISE.applyConfig(multiNoiseBiomeSourceConfig), cavesChunkGeneratorConfig);
	}

	@Override
	public boolean hasVisibleSky() {
		return false;
	}

	@Nullable
	@Override
	public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean checkMobSpawnValidity) {
		return null;
	}

	@Nullable
	@Override
	public BlockPos getTopSpawningBlockPosition(int x, int z, boolean checkMobSpawnValidity) {
		return null;
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return 0.5F;
	}

	@Override
	public boolean canPlayersSleep() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isFogThick(int x, int z) {
		return true;
	}

	@Override
	public float method_26497(Direction direction, boolean bl) {
		if (!bl) {
			return 0.9F;
		} else {
			switch (direction) {
				case DOWN:
					return 0.9F;
				case UP:
					return 0.9F;
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

	@Override
	public WorldBorder createWorldBorder() {
		return new WorldBorder() {
			@Override
			public double getCenterX() {
				return super.getCenterX() / 8.0;
			}

			@Override
			public double getCenterZ() {
				return super.getCenterZ() / 8.0;
			}
		};
	}
}
