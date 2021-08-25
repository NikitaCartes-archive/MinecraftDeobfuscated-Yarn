package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;

public class StoneSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	private static final float ANDESITE_THRESHOLD = 0.3F;
	private static final float LAYER_THRESHOLD = 0.025F;
	private long seed;
	private DoublePerlinNoiseSampler layerBlockNoise;
	private DoublePerlinNoiseSampler layerNoise;

	public StoneSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

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
		double e = this.layerNoise.sample((double)i, (double)j, (double)k);
		TernarySurfaceConfig ternarySurfaceConfig2;
		if (e > -0.025F && e < 0.025F) {
			double f = this.layerBlockNoise.sample((double)i, (double)k, (double)j);
			ternarySurfaceConfig2 = this.getLayerBlockConfig(f);
		} else {
			ternarySurfaceConfig2 = SurfaceBuilder.STONE_CONFIG;
		}

		SurfaceBuilder.DEFAULT.generate(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, n, ternarySurfaceConfig2);
	}

	protected TernarySurfaceConfig getLayerBlockConfig(double noise) {
		TernarySurfaceConfig ternarySurfaceConfig;
		if (noise < -0.3F) {
			ternarySurfaceConfig = SurfaceBuilder.DIORITE_CONFIG;
		} else if (noise < 0.0) {
			ternarySurfaceConfig = SurfaceBuilder.ANDESITE_CONFIG;
		} else if (noise < 0.3F) {
			ternarySurfaceConfig = SurfaceBuilder.GRAVEL_CONFIG;
		} else {
			ternarySurfaceConfig = SurfaceBuilder.GRANITE_CONFIG;
		}

		return ternarySurfaceConfig;
	}

	@Override
	public void initSeed(long seed) {
		if (this.seed != seed) {
			ChunkRandom chunkRandom = new ChunkRandom(seed);
			this.layerNoise = DoublePerlinNoiseSampler.create(chunkRandom, -7, 1.0, 1.0, 0.0);
			this.layerBlockNoise = DoublePerlinNoiseSampler.create(chunkRandom, -8, 1.0);
		}

		this.seed = seed;
	}
}
