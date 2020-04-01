package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.MushroomBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class HugeBrownMushroomFeature extends HugeMushroomFeature {
	public HugeBrownMushroomFeature(
		Function<Dynamic<?>, ? extends HugeMushroomFeatureConfig> function, Function<Random, ? extends HugeMushroomFeatureConfig> function2
	) {
		super(function, function2);
	}

	@Override
	protected void generateCap(IWorld world, Random random, BlockPos start, int y, BlockPos.Mutable mutable, HugeMushroomFeatureConfig config) {
		int i = config.capSize;

		for (int j = -i; j <= i; j++) {
			for (int k = -i; k <= i; k++) {
				boolean bl = j == -i;
				boolean bl2 = j == i;
				boolean bl3 = k == -i;
				boolean bl4 = k == i;
				boolean bl5 = bl || bl2;
				boolean bl6 = bl3 || bl4;
				if (!bl5 || !bl6) {
					mutable.set(start, j, y, k);
					if (!world.getBlockState(mutable).isOpaqueFullCube(world, mutable)) {
						boolean bl7 = bl || bl6 && j == 1 - i;
						boolean bl8 = bl2 || bl6 && j == i - 1;
						boolean bl9 = bl3 || bl5 && k == 1 - i;
						boolean bl10 = bl4 || bl5 && k == i - 1;
						this.setBlockState(
							world,
							mutable,
							config.capProvider
								.getBlockState(random, start)
								.with(MushroomBlock.WEST, Boolean.valueOf(bl7))
								.with(MushroomBlock.EAST, Boolean.valueOf(bl8))
								.with(MushroomBlock.NORTH, Boolean.valueOf(bl9))
								.with(MushroomBlock.SOUTH, Boolean.valueOf(bl10))
						);
					}
				}
			}
		}
	}

	@Override
	protected int getCapSize(int i, int j, int capSize, int y) {
		return y <= 3 ? 0 : capSize;
	}
}
