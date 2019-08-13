package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;

public class FrozenOceanSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	protected static final BlockState PACKED_ICE = Blocks.field_10225.getDefaultState();
	protected static final BlockState SNOW_BLOCK = Blocks.field_10491.getDefaultState();
	private static final BlockState AIR = Blocks.field_10124.getDefaultState();
	private static final BlockState GRAVEL = Blocks.field_10255.getDefaultState();
	private static final BlockState ICE = Blocks.field_10295.getDefaultState();
	private OctaveSimplexNoiseSampler field_15644;
	private OctaveSimplexNoiseSampler field_15642;
	private long seed;

	public FrozenOceanSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
		super(function);
	}

	public void method_15221(
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
		double e = 0.0;
		double f = 0.0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		float g = biome.getTemperature(mutable.set(i, 63, j));
		double h = Math.min(Math.abs(d), this.field_15644.sample((double)i * 0.1, (double)j * 0.1));
		if (h > 1.8) {
			double n = 0.09765625;
			double o = Math.abs(this.field_15642.sample((double)i * 0.09765625, (double)j * 0.09765625));
			e = h * h * 1.2;
			double p = Math.ceil(o * 40.0) + 14.0;
			if (e > p) {
				e = p;
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

		int q = i & 15;
		int r = j & 15;
		BlockState blockState3 = biome.getSurfaceConfig().getUnderMaterial();
		BlockState blockState4 = biome.getSurfaceConfig().getTopMaterial();
		int s = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		int t = -1;
		int u = 0;
		int v = 2 + random.nextInt(4);
		int w = l + 18 + random.nextInt(10);

		for (int x = Math.max(k, (int)e + 1); x >= 0; x--) {
			mutable.set(q, x, r);
			if (chunk.getBlockState(mutable).isAir() && x < (int)e && random.nextDouble() > 0.01) {
				chunk.setBlockState(mutable, PACKED_ICE, false);
			} else if (chunk.getBlockState(mutable).getMaterial() == Material.WATER && x > (int)f && x < l && f != 0.0 && random.nextDouble() > 0.15) {
				chunk.setBlockState(mutable, PACKED_ICE, false);
			}

			BlockState blockState5 = chunk.getBlockState(mutable);
			if (blockState5.isAir()) {
				t = -1;
			} else if (blockState5.getBlock() == blockState.getBlock()) {
				if (t == -1) {
					if (s <= 0) {
						blockState4 = AIR;
						blockState3 = blockState;
					} else if (x >= l - 4 && x <= l + 1) {
						blockState4 = biome.getSurfaceConfig().getTopMaterial();
						blockState3 = biome.getSurfaceConfig().getUnderMaterial();
					}

					if (x < l && (blockState4 == null || blockState4.isAir())) {
						if (biome.getTemperature(mutable.set(i, x, j)) < 0.15F) {
							blockState4 = ICE;
						} else {
							blockState4 = blockState2;
						}
					}

					t = s;
					if (x >= l - 1) {
						chunk.setBlockState(mutable, blockState4, false);
					} else if (x < l - 7 - s) {
						blockState4 = AIR;
						blockState3 = blockState;
						chunk.setBlockState(mutable, GRAVEL, false);
					} else {
						chunk.setBlockState(mutable, blockState3, false);
					}
				} else if (t > 0) {
					t--;
					chunk.setBlockState(mutable, blockState3, false);
					if (t == 0 && blockState3.getBlock() == Blocks.field_10102 && s > 1) {
						t = random.nextInt(4) + Math.max(0, x - 63);
						blockState3 = blockState3.getBlock() == Blocks.field_10534 ? Blocks.field_10344.getDefaultState() : Blocks.field_9979.getDefaultState();
					}
				}
			} else if (blockState5.getBlock() == Blocks.field_10225 && u <= v && x > w) {
				chunk.setBlockState(mutable, SNOW_BLOCK, false);
				u++;
			}
		}
	}

	@Override
	public void initSeed(long l) {
		if (this.seed != l || this.field_15644 == null || this.field_15642 == null) {
			Random random = new ChunkRandom(l);
			this.field_15644 = new OctaveSimplexNoiseSampler(random, 4);
			this.field_15642 = new OctaveSimplexNoiseSampler(random, 1);
		}

		this.seed = l;
	}
}
