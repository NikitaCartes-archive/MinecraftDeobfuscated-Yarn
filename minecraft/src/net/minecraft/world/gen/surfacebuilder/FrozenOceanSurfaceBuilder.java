package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.class_6557;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.random.ChunkRandom;

public class FrozenOceanSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	protected static final BlockState PACKED_ICE = Blocks.PACKED_ICE.getDefaultState();
	protected static final BlockState SNOW_BLOCK = Blocks.SNOW_BLOCK.getDefaultState();
	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
	private static final BlockState ICE = Blocks.ICE.getDefaultState();
	private OctaveSimplexNoiseSampler icebergNoise;
	private OctaveSimplexNoiseSampler icebergCutoffNoise;
	private long seed;

	public FrozenOceanSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	public void generate(
		Random random,
		class_6557 arg,
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
		double e = 0.0;
		double f = 0.0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		float g = biome.getTemperature(mutable.set(i, 63, j));
		double h = Math.min(Math.abs(d), this.icebergNoise.sample((double)i * 0.1, (double)j * 0.1, false) * 15.0);
		if (h > 1.8) {
			double o = 0.09765625;
			double p = Math.abs(this.icebergCutoffNoise.sample((double)i * 0.09765625, (double)j * 0.09765625, false));
			e = h * h * 1.2;
			double q = Math.ceil(p * 40.0) + 14.0;
			if (e > q) {
				e = q;
			}

			if (g > 0.1F) {
				e -= 2.0;
			}

			if (e > 2.0) {
				f = (double)l - e - 7.0;
				e += (double)l;
			} else {
				e = 0.0;
			}
		}

		SurfaceConfig surfaceConfig = biome.getGenerationSettings().getSurfaceConfig();
		BlockState blockState3 = surfaceConfig.getUnderMaterial();
		BlockState blockState4 = surfaceConfig.getTopMaterial();
		BlockState blockState5 = blockState3;
		BlockState blockState6 = blockState4;
		int r = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		int s = -1;
		int t = 0;
		int u = 2 + random.nextInt(4);
		int v = l + 18 + random.nextInt(10);

		for (int w = Math.max(k, (int)e + 1); w >= m; w--) {
			if (arg.getState(w).isAir() && w < (int)e && random.nextDouble() > 0.01) {
				arg.method_38092(w, PACKED_ICE);
			} else if (arg.getState(w).getMaterial() == Material.WATER && w > (int)f && w < l && f != 0.0 && random.nextDouble() > 0.15) {
				arg.method_38092(w, PACKED_ICE);
			}

			BlockState blockState7 = arg.getState(w);
			if (blockState7.isAir()) {
				s = -1;
			} else if (blockState7.isOf(blockState.getBlock())) {
				if (s == -1) {
					if (r <= 0) {
						blockState6 = AIR;
						blockState5 = blockState;
					} else if (w >= l - 4 && w <= l + 1) {
						blockState6 = blockState4;
						blockState5 = blockState3;
					}

					if (w < l && (blockState6 == null || blockState6.isAir())) {
						if (biome.getTemperature(mutable.set(i, w, j)) < 0.15F) {
							blockState6 = ICE;
						} else {
							blockState6 = blockState2;
						}
					}

					s = r;
					if (w >= l - 1) {
						arg.method_38092(w, blockState6);
					} else if (w < l - 7 - r) {
						blockState6 = AIR;
						blockState5 = blockState;
						arg.method_38092(w, GRAVEL);
					} else {
						arg.method_38092(w, blockState5);
					}
				} else if (s > 0) {
					s--;
					arg.method_38092(w, blockState5);
					if (s == 0 && blockState5.isOf(Blocks.SAND) && r > 1) {
						s = random.nextInt(4) + Math.max(0, w - 63);
						blockState5 = blockState5.isOf(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
					}
				}
			} else if (blockState7.isOf(Blocks.PACKED_ICE) && t <= u && w > v) {
				arg.method_38092(w, SNOW_BLOCK);
				t++;
			}
		}
	}

	@Override
	public void initSeed(long seed) {
		if (this.seed != seed || this.icebergNoise == null || this.icebergCutoffNoise == null) {
			ChunkRandom chunkRandom = new ChunkRandom(seed);
			this.icebergNoise = new OctaveSimplexNoiseSampler(chunkRandom, IntStream.rangeClosed(-3, 0));
			this.icebergCutoffNoise = new OctaveSimplexNoiseSampler(chunkRandom, ImmutableList.of(0));
		}

		this.seed = seed;
	}
}
