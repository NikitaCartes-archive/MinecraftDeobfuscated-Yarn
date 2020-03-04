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

public class CoralClawFeature extends CoralFeature {
	public CoralClawFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected boolean spawnCoral(IWorld world, Random random, BlockPos pos, BlockState state) {
		if (!this.spawnCoralPiece(world, random, pos, state)) {
			return false;
		} else {
			Direction direction = Direction.Type.HORIZONTAL.random(random);
			int i = random.nextInt(2) + 2;
			List<Direction> list = Lists.<Direction>newArrayList(direction, direction.rotateYClockwise(), direction.rotateYCounterclockwise());
			Collections.shuffle(list, random);

			for (Direction direction2 : list.subList(0, i)) {
				BlockPos.Mutable mutable = pos.mutableCopy();
				int j = random.nextInt(2) + 1;
				mutable.setOffset(direction2);
				int k;
				Direction direction3;
				if (direction2 == direction) {
					direction3 = direction;
					k = random.nextInt(3) + 2;
				} else {
					mutable.setOffset(Direction.UP);
					Direction[] directions = new Direction[]{direction2, Direction.UP};
					direction3 = directions[random.nextInt(directions.length)];
					k = random.nextInt(3) + 3;
				}

				for (int l = 0; l < j && this.spawnCoralPiece(world, random, mutable, state); l++) {
					mutable.setOffset(direction3);
				}

				mutable.setOffset(direction3.getOpposite());
				mutable.setOffset(Direction.UP);

				for (int l = 0; l < k; l++) {
					mutable.setOffset(direction);
					if (!this.spawnCoralPiece(world, random, mutable, state)) {
						break;
					}

					if (random.nextFloat() < 0.25F) {
						mutable.setOffset(Direction.UP);
					}
				}
			}

			return true;
		}
	}
}
