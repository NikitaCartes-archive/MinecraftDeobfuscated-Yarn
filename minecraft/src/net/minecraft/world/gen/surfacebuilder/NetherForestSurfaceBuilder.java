package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;

public class NetherForestSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
	protected long seed;
	private OctavePerlinNoiseSampler surfaceNoise;

	public NetherForestSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
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
		long m,
		TernarySurfaceConfig ternarySurfaceConfig
	) {
		int n = l;
		int o = i & 15;
		int p = j & 15;
		double e = this.surfaceNoise.sample((double)i * 0.1, (double)l, (double)j * 0.1);
		boolean bl = e > 0.15 + random.nextDouble() * 0.35;
		double f = this.surfaceNoise.sample((double)i * 0.1, 109.0, (double)j * 0.1);
		boolean bl2 = f > 0.25 + random.nextDouble() * 0.9;
		int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int r = -1;
		BlockState blockState3 = ternarySurfaceConfig.getUnderMaterial();

		for (int s = 127; s >= 0; s--) {
			mutable.set(o, s, p);
			BlockState blockState4 = ternarySurfaceConfig.getTopMaterial();
			BlockState blockState5 = chunk.getBlockState(mutable);
			if (blockState5.isAir()) {
				r = -1;
			} else if (blockState5.isOf(blockState.getBlock())) {
				if (r == -1) {
					boolean bl3 = false;
					if (q <= 0) {
						bl3 = true;
						blockState3 = ternarySurfaceConfig.getUnderMaterial();
					}

					if (bl) {
						blockState4 = ternarySurfaceConfig.getUnderMaterial();
					} else if (bl2) {
						blockState4 = ternarySurfaceConfig.getUnderwaterMaterial();
					}

					if (s < n && bl3) {
						blockState4 = blockState2;
					}

					r = q;
					if (s >= n - 1) {
						chunk.setBlockState(mutable, blockState4, false);
					} else {
						chunk.setBlockState(mutable, blockState3, false);
					}
				} else if (r > 0) {
					r--;
					chunk.setBlockState(mutable, blockState3, false);
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
