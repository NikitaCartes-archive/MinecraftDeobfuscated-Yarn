package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;

public class NetherSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
	private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
	private static final BlockState GLOWSTONE = Blocks.SOUL_SAND.getDefaultState();
	protected long seed;
	protected OctavePerlinNoiseSampler noise;

	public NetherSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
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
		double e = 0.03125;
		boolean bl = this.noise.sample((double)i * 0.03125, (double)j * 0.03125, 0.0) * 75.0 + random.nextDouble() > 0.0;
		boolean bl2 = this.noise.sample((double)i * 0.03125, 109.0, (double)j * 0.03125) * 75.0 + random.nextDouble() > 0.0;
		int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int r = -1;
		BlockState blockState3 = ternarySurfaceConfig.getTopMaterial();
		BlockState blockState4 = ternarySurfaceConfig.getUnderMaterial();

		for (int s = 127; s >= 0; s--) {
			mutable.set(o, s, p);
			BlockState blockState5 = chunk.getBlockState(mutable);
			if (blockState5.isAir()) {
				r = -1;
			} else if (blockState5.isOf(blockState.getBlock())) {
				if (r == -1) {
					boolean bl3 = false;
					if (q <= 0) {
						bl3 = true;
						blockState4 = ternarySurfaceConfig.getUnderMaterial();
					} else if (s >= n - 4 && s <= n + 1) {
						blockState3 = ternarySurfaceConfig.getTopMaterial();
						blockState4 = ternarySurfaceConfig.getUnderMaterial();
						if (bl2) {
							blockState3 = GRAVEL;
							blockState4 = ternarySurfaceConfig.getUnderMaterial();
						}

						if (bl) {
							blockState3 = GLOWSTONE;
							blockState4 = GLOWSTONE;
						}
					}

					if (s < n && bl3) {
						blockState3 = blockState2;
					}

					r = q;
					if (s >= n - 1) {
						chunk.setBlockState(mutable, blockState3, false);
					} else {
						chunk.setBlockState(mutable, blockState4, false);
					}
				} else if (r > 0) {
					r--;
					chunk.setBlockState(mutable, blockState4, false);
				}
			}
		}
	}

	@Override
	public void initSeed(long seed) {
		if (this.seed != seed || this.noise == null) {
			this.noise = new OctavePerlinNoiseSampler(new ChunkRandom(seed), IntStream.rangeClosed(-3, 0));
		}

		this.seed = seed;
	}
}
