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
	public CoralTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, Function<Random, ? extends DefaultFeatureConfig> function2) {
		super(function, function2);
	}

	@Override
	protected boolean spawnCoral(IWorld world, Random random, BlockPos pos, BlockState state) {
		BlockPos.Mutable mutable = pos.mutableCopy();
		int i = random.nextInt(3) + 1;

		for (int j = 0; j < i; j++) {
			if (!this.spawnCoralPiece(world, random, mutable, state)) {
				return true;
			}

			mutable.move(Direction.UP);
		}

		BlockPos blockPos = mutable.toImmutable();
		int k = random.nextInt(3) + 2;
		List<Direction> list = Lists.<Direction>newArrayList(Direction.Type.HORIZONTAL);
		Collections.shuffle(list, random);

		for (Direction direction : list.subList(0, k)) {
			mutable.set(blockPos);
			mutable.move(direction);
			int l = random.nextInt(5) + 2;
			int m = 0;

			for (int n = 0; n < l && this.spawnCoralPiece(world, random, mutable, state); n++) {
				m++;
				mutable.move(Direction.UP);
				if (n == 0 || m >= 2 && random.nextFloat() < 0.25F) {
					mutable.move(direction);
					m = 0;
				}
			}
		}

		return true;
	}
}
