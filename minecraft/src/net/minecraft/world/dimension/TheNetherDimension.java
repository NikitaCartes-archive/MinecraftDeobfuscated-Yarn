package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.chunk.CavesChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class TheNetherDimension extends Dimension {
	private static final Vec3d field_21216 = new Vec3d(0.2F, 0.03F, 0.03F);

	public TheNetherDimension(World world, DimensionType type) {
		super(world, type, 0.1F);
		this.waterVaporizes = true;
		this.isNether = true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d getFogColor(float skyAngle, float tickDelta) {
		return field_21216;
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		CavesChunkGeneratorConfig cavesChunkGeneratorConfig = ChunkGeneratorType.CAVES.createSettings();
		cavesChunkGeneratorConfig.setDefaultBlock(Blocks.NETHERRACK.getDefaultState());
		cavesChunkGeneratorConfig.setDefaultFluid(Blocks.LAVA.getDefaultState());
		return ChunkGeneratorType.CAVES
			.create(
				this.world,
				BiomeSourceType.FIXED.applyConfig(BiomeSourceType.FIXED.getConfig(this.world.getLevelProperties()).setBiome(Biomes.NETHER)),
				cavesChunkGeneratorConfig
			);
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

	@Override
	public DimensionType getType() {
		return DimensionType.THE_NETHER;
	}
}
