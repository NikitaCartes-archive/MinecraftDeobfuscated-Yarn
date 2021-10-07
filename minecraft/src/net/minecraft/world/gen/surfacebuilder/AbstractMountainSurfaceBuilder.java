package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.BlockColumn;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public abstract class AbstractMountainSurfaceBuilder extends DefaultSurfaceBuilder {
	private long seed;
	protected DoublePerlinNoiseSampler noiseSampler;

	public AbstractMountainSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	@Override
	public void generate(
		Random random,
		BlockColumn blockColumn,
		Biome biome,
		int i,
		int j,
		int k,
		double d,
		BlockState blockState,
		BlockState blockState2,
		int l,
		int m,
		long n,
		TernarySurfaceConfig ternarySurfaceConfig
	) {
		BlockState blockState3;
		BlockState blockState4;
		if (this.getLayerBlockConfig() != null && this.shouldPlaceSteepSlopeBlock(blockColumn, i, j, this.getLayerBlockConfig())) {
			blockState3 = this.getLayerBlockConfig().getState();
			blockState4 = this.getLayerBlockConfig().getState();
		} else {
			blockState3 = this.getTopMaterial(ternarySurfaceConfig, i, j);
			blockState4 = this.getUnderMaterial(ternarySurfaceConfig, i, j);
		}

		this.generate(random, blockColumn, biome, i, j, k, d, blockState, blockState2, blockState3, blockState4, ternarySurfaceConfig.getUnderwaterMaterial(), l, m);
	}

	protected BlockState getBlockFromNoise(double scale, int x, int z, BlockState outsideRangeState, BlockState insideRangeState, double noiseMin, double noiseMax) {
		double d = this.noiseSampler.sample((double)x * scale, 0.0, (double)z * scale);
		BlockState blockState;
		if (d >= noiseMin && d <= noiseMax) {
			blockState = insideRangeState;
		} else {
			blockState = outsideRangeState;
		}

		return blockState;
	}

	@Override
	public void initSeed(long seed) {
		if (this.seed != seed) {
			ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(seed));
			this.noiseSampler = DoublePerlinNoiseSampler.create(chunkRandom, -3, 1.0, 1.0, 1.0, 1.0);
		}

		this.seed = seed;
	}

	public boolean shouldPlaceSteepSlopeBlock(BlockColumn blockColumn, int x, int z, AbstractMountainSurfaceBuilder.SteepSlopeBlockConfig config) {
		return false;
	}

	@Nullable
	protected abstract AbstractMountainSurfaceBuilder.SteepSlopeBlockConfig getLayerBlockConfig();

	protected abstract BlockState getTopMaterial(TernarySurfaceConfig config, int x, int z);

	protected abstract BlockState getUnderMaterial(TernarySurfaceConfig config, int x, int z);

	public static class SteepSlopeBlockConfig {
		private final BlockState state;
		private final boolean north;
		private final boolean south;
		private final boolean west;
		private final boolean east;

		public SteepSlopeBlockConfig(BlockState state, boolean north, boolean south, boolean west, boolean east) {
			this.state = state;
			this.north = north;
			this.south = south;
			this.west = west;
			this.east = east;
		}

		public BlockState getState() {
			return this.state;
		}

		public boolean isNorth() {
			return this.north;
		}

		public boolean isSouth() {
			return this.south;
		}

		public boolean isWest() {
			return this.west;
		}

		public boolean isEast() {
			return this.east;
		}
	}
}
