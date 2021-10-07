package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.BlockColumn;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class NetherSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
	private static final BlockState GLOWSTONE = Blocks.SOUL_SAND.getDefaultState();
	protected long seed;
	protected OctavePerlinNoiseSampler noise;

	public NetherSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
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
		int o = l;
		double e = 0.03125;
		boolean bl = this.noise.sample((double)i * 0.03125, (double)j * 0.03125, 0.0) * 75.0 + random.nextDouble() > 0.0;
		boolean bl2 = this.noise.sample((double)i * 0.03125, 109.0, (double)j * 0.03125) * 75.0 + random.nextDouble() > 0.0;
		int p = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		int q = -1;
		BlockState blockState3 = ternarySurfaceConfig.getTopMaterial();
		BlockState blockState4 = ternarySurfaceConfig.getUnderMaterial();

		for (int r = 127; r >= m; r--) {
			BlockState blockState5 = blockColumn.getState(r);
			if (blockState5.isAir()) {
				q = -1;
			} else if (blockState5.isOf(blockState.getBlock())) {
				if (q == -1) {
					boolean bl3 = false;
					if (p <= 0) {
						bl3 = true;
						blockState4 = ternarySurfaceConfig.getUnderMaterial();
					} else if (r >= o - 4 && r <= o + 1) {
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

					if (r < o && bl3) {
						blockState3 = blockState2;
					}

					q = p;
					if (r >= o - 1) {
						blockColumn.setState(r, blockState3);
					} else {
						blockColumn.setState(r, blockState4);
					}
				} else if (q > 0) {
					q--;
					blockColumn.setState(r, blockState4);
				}
			}
		}
	}

	@Override
	public void initSeed(long seed) {
		if (this.seed != seed || this.noise == null) {
			this.noise = new OctavePerlinNoiseSampler(new ChunkRandom(new AtomicSimpleRandom(seed)), IntStream.rangeClosed(-3, 0));
		}

		this.seed = seed;
	}
}
