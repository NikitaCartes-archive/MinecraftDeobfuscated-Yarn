package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.MushroomBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class HugeBrownMushroomFeature extends HugeMushroomFeature {
	public HugeBrownMushroomFeature(Function<Dynamic<?>, ? extends HugeMushroomFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	protected void generate(IWorld world, Random random, BlockPos blockPos, int i, BlockPos.Mutable pos, HugeMushroomFeatureConfig config) {
		int j = config.capSize;

		for (int k = -j; k <= j; k++) {
			for (int l = -j; l <= j; l++) {
				boolean bl = k == -j;
				boolean bl2 = k == j;
				boolean bl3 = l == -j;
				boolean bl4 = l == j;
				boolean bl5 = bl || bl2;
				boolean bl6 = bl3 || bl4;
				if (!bl5 || !bl6) {
					pos.set(blockPos).setOffset(k, i, l);
					if (!world.getBlockState(pos).isFullOpaque(world, pos)) {
						boolean bl7 = bl || bl6 && k == 1 - j;
						boolean bl8 = bl2 || bl6 && k == j - 1;
						boolean bl9 = bl3 || bl5 && l == 1 - j;
						boolean bl10 = bl4 || bl5 && l == j - 1;
						this.setBlockState(
							world,
							pos,
							config.capProvider
								.getBlockState(random, blockPos)
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
	protected int method_23372(int i, int j, int k, int l) {
		return l <= 3 ? 0 : k;
	}
}
