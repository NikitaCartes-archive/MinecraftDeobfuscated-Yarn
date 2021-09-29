package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.BlockColumn;
import net.minecraft.world.gen.random.ChunkRandom;

public class NetherForestSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	protected long seed;
	private OctavePerlinNoiseSampler surfaceNoise;

	public NetherForestSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

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
		double e = this.surfaceNoise.sample((double)i * 0.1, (double)l, (double)j * 0.1);
		boolean bl = e > 0.15 + random.nextDouble() * 0.35;
		double f = this.surfaceNoise.sample((double)i * 0.1, 109.0, (double)j * 0.1);
		boolean bl2 = f > 0.25 + random.nextDouble() * 0.9;
		int o = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		int p = -1;
		BlockState blockState3 = ternarySurfaceConfig.getUnderMaterial();

		for (int q = 127; q >= m; q--) {
			BlockState blockState4 = ternarySurfaceConfig.getTopMaterial();
			BlockState blockState5 = blockColumn.getState(q);
			if (blockState5.isAir()) {
				p = -1;
			} else if (blockState5.isOf(blockState.getBlock())) {
				if (p == -1) {
					boolean bl3 = false;
					if (o <= 0) {
						bl3 = true;
						blockState3 = ternarySurfaceConfig.getUnderMaterial();
					}

					if (bl) {
						blockState4 = ternarySurfaceConfig.getUnderMaterial();
					} else if (bl2) {
						blockState4 = ternarySurfaceConfig.getUnderwaterMaterial();
					}

					if (q < l && bl3) {
						blockState4 = blockState2;
					}

					p = o;
					if (q >= l - 1) {
						blockColumn.setState(q, blockState4);
					} else {
						blockColumn.setState(q, blockState3);
					}
				} else if (p > 0) {
					p--;
					blockColumn.setState(q, blockState3);
				}
			}
		}
	}

	@Override
	public void initSeed(long seed) {
		if (this.seed != seed || this.surfaceNoise == null) {
			this.surfaceNoise = new OctavePerlinNoiseSampler(new ChunkRandom(seed), ImmutableList.of(0));
		}

		this.seed = seed;
	}
}
