package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.MushroomBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class HugeRedMushroomFeature extends HugeMushroomFeature {
	public HugeRedMushroomFeature(Function<Dynamic<?>, ? extends HugeMushroomFeatureConfig> function) {
		super(function);
	}

	@Override
	protected void generateCap(IWorld world, Random random, BlockPos start, int y, BlockPos.Mutable mutable, HugeMushroomFeatureConfig config) {
		for (int i = y - 3; i <= y; i++) {
			int j = i < y ? config.capSize : config.capSize - 1;
			int k = config.capSize - 2;

			for (int l = -j; l <= j; l++) {
				for (int m = -j; m <= j; m++) {
					boolean bl = l == -j;
					boolean bl2 = l == j;
					boolean bl3 = m == -j;
					boolean bl4 = m == j;
					boolean bl5 = bl || bl2;
					boolean bl6 = bl3 || bl4;
					if (i >= y || bl5 != bl6) {
						mutable.set(start, l, i, m);
						if (!world.getBlockState(mutable).isOpaqueFullCube(world, mutable)) {
							this.setBlockState(
								world,
								mutable,
								config.capProvider
									.getBlockState(random, start)
									.with(MushroomBlock.UP, Boolean.valueOf(i >= y - 1))
									.with(MushroomBlock.WEST, Boolean.valueOf(l < -k))
									.with(MushroomBlock.EAST, Boolean.valueOf(l > k))
									.with(MushroomBlock.NORTH, Boolean.valueOf(m < -k))
									.with(MushroomBlock.SOUTH, Boolean.valueOf(m > k))
							);
						}
					}
				}
			}
		}
	}

	@Override
	protected int getCapSize(int i, int j, int capSize, int y) {
		int k = 0;
		if (y < j && y >= j - 3) {
			k = capSize;
		} else if (y == j) {
			k = capSize;
		}

		return k;
	}
}
