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
		int m,
		long n,
		TernarySurfaceConfig ternarySurfaceConfig
	) {
		int o = l;
		int p = i & 15;
		int q = j & 15;
		double e = 0.03125;
		boolean bl = this.noise.sample((double)i * 0.03125, (double)j * 0.03125, 0.0) * 75.0 + random.nextDouble() > 0.0;
		boolean bl2 = this.noise.sample((double)i * 0.03125, 109.0, (double)j * 0.03125) * 75.0 + random.nextDouble() > 0.0;
		int r = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int s = -1;
		BlockState blockState3 = ternarySurfaceConfig.getTopMaterial();
		BlockState blockState4 = ternarySurfaceConfig.getUnderMaterial();

		for (int t = 127; t >= m; t--) {
			mutable.set(p, t, q);
			BlockState blockState5 = chunk.getBlockState(mutable);
			if (blockState5.isAir()) {
				s = -1;
			} else if (blockState5.isOf(blockState.getBlock())) {
				if (s == -1) {
					boolean bl3 = false;
					if (r <= 0) {
						bl3 = true;
						blockState4 = ternarySurfaceConfig.getUnderMaterial();
					} else if (t >= o - 4 && t <= o + 1) {
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

					if (t < o && bl3) {
						blockState3 = blockState2;
					}

					s = r;
					if (t >= o - 1) {
						chunk.setBlockState(mutable, blockState3, false);
					} else {
						chunk.setBlockState(mutable, blockState4, false);
					}
				} else if (s > 0) {
					s--;
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
