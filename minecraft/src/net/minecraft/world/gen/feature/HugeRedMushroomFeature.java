package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.MushroomBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class HugeRedMushroomFeature extends HugeMushroomFeature {
	public HugeRedMushroomFeature(Function<Dynamic<?>, ? extends HugeMushroomFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	protected void generate(IWorld world, Random random, BlockPos blockPos, int i, BlockPos.Mutable pos, HugeMushroomFeatureConfig config) {
		for (int j = i - 3; j <= i; j++) {
			int k = j < i ? config.capSize : config.capSize - 1;
			int l = config.capSize - 2;

			for (int m = -k; m <= k; m++) {
				for (int n = -k; n <= k; n++) {
					boolean bl = m == -k;
					boolean bl2 = m == k;
					boolean bl3 = n == -k;
					boolean bl4 = n == k;
					boolean bl5 = bl || bl2;
					boolean bl6 = bl3 || bl4;
					if (j >= i || bl5 != bl6) {
						pos.set(blockPos).setOffset(m, j, n);
						if (!world.getBlockState(pos).isFullOpaque(world, pos)) {
							this.setBlockState(
								world,
								pos,
								config.capProvider
									.getBlockState(random, blockPos)
									.with(MushroomBlock.UP, Boolean.valueOf(j >= i - 1))
									.with(MushroomBlock.WEST, Boolean.valueOf(m < -l))
									.with(MushroomBlock.EAST, Boolean.valueOf(m > l))
									.with(MushroomBlock.NORTH, Boolean.valueOf(n < -l))
									.with(MushroomBlock.SOUTH, Boolean.valueOf(n > l))
							);
						}
					}
				}
			}
		}
	}

	@Override
	protected int method_23372(int i, int j, int k, int l) {
		int m = 0;
		if (l < j && l >= j - 3) {
			m = k;
		} else if (l == j) {
			m = k;
		}

		return m;
	}
}
