package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class class_6485 extends SurfaceBuilder<TernarySurfaceConfig> {
	private static final float field_34319 = 0.3F;
	private static final float field_34320 = 0.025F;
	private long field_34321;
	private DoublePerlinNoiseSampler field_34322;
	private DoublePerlinNoiseSampler field_34323;

	public class_6485(Codec<TernarySurfaceConfig> codec) {
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
		double e = this.field_34323.sample((double)i, (double)j, (double)k);
		TernarySurfaceConfig ternarySurfaceConfig2;
		if (e > -0.025F && e < 0.025F) {
			double f = this.field_34322.sample((double)i, (double)k, (double)j);
			ternarySurfaceConfig2 = this.method_37866(f);
		} else {
			ternarySurfaceConfig2 = SurfaceBuilder.STONE_CONFIG;
		}

		SurfaceBuilder.DEFAULT.generate(random, chunk, biome, i, j, k, d, blockState, blockState2, l, m, n, ternarySurfaceConfig2);
	}

	protected TernarySurfaceConfig method_37866(double d) {
		TernarySurfaceConfig ternarySurfaceConfig;
		if (d < -0.3F) {
			ternarySurfaceConfig = SurfaceBuilder.field_34331;
		} else if (d < 0.0) {
			ternarySurfaceConfig = SurfaceBuilder.field_34330;
		} else if (d < 0.3F) {
			ternarySurfaceConfig = SurfaceBuilder.GRAVEL_CONFIG;
		} else {
			ternarySurfaceConfig = SurfaceBuilder.field_34329;
		}

		return ternarySurfaceConfig;
	}

	@Override
	public void initSeed(long seed) {
		if (this.field_34321 != seed) {
			ChunkRandom chunkRandom = new ChunkRandom(seed);
			this.field_34323 = DoublePerlinNoiseSampler.create(chunkRandom, -7, 1.0, 1.0, 0.0);
			this.field_34322 = DoublePerlinNoiseSampler.create(chunkRandom, -8, 1.0);
		}

		this.field_34321 = seed;
	}
}
