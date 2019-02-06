package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class ErodedBadlandsSurfaceBuilder extends BadlandsSurfaceBuilder {
	private static final BlockState WHITE_TERRACOTTA = Blocks.field_10611.getDefaultState();
	private static final BlockState ORANGE_TERRACOTTA = Blocks.field_10184.getDefaultState();
	private static final BlockState TERACOTTA = Blocks.field_10415.getDefaultState();

	public ErodedBadlandsSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
		super(function);
	}

	@Override
	public void method_15208(
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
		double f = Math.min(Math.abs(d), this.field_15623.sample((double)i * 0.25, (double)j * 0.25));
		if (f > 0.0) {
			double g = 0.001953125;
			double h = Math.abs(this.field_15618.sample((double)i * 0.001953125, (double)j * 0.001953125));
			e = f * f * 2.5;
			double n = Math.ceil(h * 50.0) + 14.0;
			if (e > n) {
				e = n;
			}

			e += 64.0;
		}

		int o = i & 15;
		int p = j & 15;
		BlockState blockState3 = WHITE_TERRACOTTA;
		BlockState blockState4 = biome.getSurfaceConfig().getUnderMaterial();
		int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
		int r = -1;
		boolean bl2 = false;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int s = Math.max(k, (int)e + 1); s >= 0; s--) {
			mutable.set(o, s, p);
			if (chunk.getBlockState(mutable).isAir() && s < (int)e) {
				chunk.setBlockState(mutable, blockState, false);
			}

			BlockState blockState5 = chunk.getBlockState(mutable);
			if (blockState5.isAir()) {
				r = -1;
			} else if (blockState5.getBlock() == blockState.getBlock()) {
				if (r == -1) {
					bl2 = false;
					if (q <= 0) {
						blockState3 = Blocks.field_10124.getDefaultState();
						blockState4 = blockState;
					} else if (s >= l - 4 && s <= l + 1) {
						blockState3 = WHITE_TERRACOTTA;
						blockState4 = biome.getSurfaceConfig().getUnderMaterial();
					}

					if (s < l && (blockState3 == null || blockState3.isAir())) {
						blockState3 = blockState2;
					}

					r = q + Math.max(0, s - l);
					if (s >= l - 1) {
						if (s > l + 3 + q) {
							BlockState blockState6;
							if (s < 64 || s > 127) {
								blockState6 = ORANGE_TERRACOTTA;
							} else if (bl) {
								blockState6 = TERACOTTA;
							} else {
								blockState6 = this.method_15207(i, s, j);
							}

							chunk.setBlockState(mutable, blockState6, false);
						} else {
							chunk.setBlockState(mutable, biome.getSurfaceConfig().getTopMaterial(), false);
							bl2 = true;
						}
					} else {
						chunk.setBlockState(mutable, blockState4, false);
						Block block = blockState4.getBlock();
						if (block == Blocks.field_10611
							|| block == Blocks.field_10184
							|| block == Blocks.field_10015
							|| block == Blocks.field_10325
							|| block == Blocks.field_10143
							|| block == Blocks.field_10014
							|| block == Blocks.field_10444
							|| block == Blocks.field_10349
							|| block == Blocks.field_10590
							|| block == Blocks.field_10235
							|| block == Blocks.field_10570
							|| block == Blocks.field_10409
							|| block == Blocks.field_10123
							|| block == Blocks.field_10526
							|| block == Blocks.field_10328
							|| block == Blocks.field_10626) {
							chunk.setBlockState(mutable, ORANGE_TERRACOTTA, false);
						}
					}
				} else if (r > 0) {
					r--;
					if (bl2) {
						chunk.setBlockState(mutable, ORANGE_TERRACOTTA, false);
					} else {
						chunk.setBlockState(mutable, this.method_15207(i, s, j), false);
					}
				}
			}
		}
	}
}
