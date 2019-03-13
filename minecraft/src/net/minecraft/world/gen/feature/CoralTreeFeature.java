package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class CoralTreeFeature extends CoralFeature {
	public CoralTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected boolean method_12863(IWorld iWorld, Random random, BlockPos blockPos, BlockState blockState) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
		int i = random.nextInt(3) + 1;

		for (int j = 0; j < i; j++) {
			if (!this.method_12864(iWorld, random, mutable, blockState)) {
				return true;
			}

			mutable.method_10098(Direction.UP);
		}

		BlockPos blockPos2 = mutable.toImmutable();
		int k = random.nextInt(3) + 2;
		List<Direction> list = Lists.<Direction>newArrayList(Direction.Type.HORIZONTAL);
		Collections.shuffle(list, random);

		for (Direction direction : list.subList(0, k)) {
			mutable.method_10101(blockPos2);
			mutable.method_10098(direction);
			int l = random.nextInt(5) + 2;
			int m = 0;

			for (int n = 0; n < l && this.method_12864(iWorld, random, mutable, blockState); n++) {
				m++;
				mutable.method_10098(Direction.UP);
				if (n == 0 || m >= 2 && random.nextFloat() < 0.25F) {
					mutable.method_10098(direction);
					m = 0;
				}
			}
		}

		return true;
	}
}
