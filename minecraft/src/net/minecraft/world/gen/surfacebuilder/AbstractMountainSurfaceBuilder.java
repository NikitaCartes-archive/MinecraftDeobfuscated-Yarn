package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;

public abstract class AbstractMountainSurfaceBuilder extends DefaultSurfaceBuilder {
	private long seed;
	protected DoublePerlinNoiseSampler noiseSampler;

	public AbstractMountainSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	@Override
	public void generate(
		Random random,
		Chunk chunk,
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
		if (this.getSteepSlopeBlockConfig() != null && this.shouldPlaceSteepSlopeBlock(chunk, i, j, this.getSteepSlopeBlockConfig())) {
			blockState3 = this.getSteepSlopeBlockConfig().getState();
			blockState4 = this.getSteepSlopeBlockConfig().getState();
		} else {
			blockState3 = this.getTopMaterial(ternarySurfaceConfig, i, j);
			blockState4 = this.getUnderMaterial(ternarySurfaceConfig, i, j);
		}

		generate(random, chunk, biome, i, j, k, d, blockState, blockState2, blockState3, blockState4, ternarySurfaceConfig.getUnderwaterMaterial(), m);
	}

	protected BlockState getBlockFromNoise(double scale, int x, int z, BlockState outsideRangeState, BlockState insideRangeState, double noiseMin, double noiseMax) {
		double d = this.noiseSampler.sample((double)x * scale, 100.0, (double)z * scale);
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
			ChunkRandom chunkRandom = new ChunkRandom(seed);
			this.noiseSampler = DoublePerlinNoiseSampler.create(chunkRandom, -3, 1.0, 1.0, 1.0, 1.0);
		}

		this.seed = seed;
	}

	public boolean shouldPlaceSteepSlopeBlock(Chunk chunk, int x, int z, AbstractMountainSurfaceBuilder.SteepSlopeBlockConfig config) {
		int i = 1;
		int j = 3;
		int k = x & 15;
		int l = z & 15;
		if (config.isNorth() || config.isSouth()) {
			int m = Math.max(l - 1, 0);
			int n = Math.min(l + 1, 15);
			int o = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, k, m);
			int p = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, k, n);
			int q = o - p;
			if (config.isSouth() && q > 3) {
				return true;
			}

			if (config.isNorth() && -q > 3) {
				return true;
			}
		}

		if (!config.isEast() && !config.isWest()) {
			return false;
		} else {
			int mx = Math.max(k - 1, 0);
			int nx = Math.min(k + 1, 15);
			int ox = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, mx, l);
			int px = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, nx, l);
			int qx = ox - px;
			return config.isEast() && qx > 3 ? true : config.isWest() && -qx > 3;
		}
	}

	@Nullable
	protected abstract AbstractMountainSurfaceBuilder.SteepSlopeBlockConfig getSteepSlopeBlockConfig();

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
