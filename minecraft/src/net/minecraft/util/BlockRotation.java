package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.util.math.Direction;

public enum BlockRotation {
	ROT_0,
	ROT_90,
	ROT_180,
	ROT_270;

	public BlockRotation rotate(BlockRotation blockRotation) {
		switch (blockRotation) {
			case ROT_180:
				switch (this) {
					case ROT_0:
						return ROT_180;
					case ROT_90:
						return ROT_270;
					case ROT_180:
						return ROT_0;
					case ROT_270:
						return ROT_90;
				}
			case ROT_270:
				switch (this) {
					case ROT_0:
						return ROT_270;
					case ROT_90:
						return ROT_0;
					case ROT_180:
						return ROT_90;
					case ROT_270:
						return ROT_180;
				}
			case ROT_90:
				switch (this) {
					case ROT_0:
						return ROT_90;
					case ROT_90:
						return ROT_180;
					case ROT_180:
						return ROT_270;
					case ROT_270:
						return ROT_0;
				}
			default:
				return this;
		}
	}

	public Direction rotate(Direction direction) {
		if (direction.getAxis() == Direction.Axis.Y) {
			return direction;
		} else {
			switch (this) {
				case ROT_90:
					return direction.rotateYClockwise();
				case ROT_180:
					return direction.getOpposite();
				case ROT_270:
					return direction.rotateYCounterclockwise();
				default:
					return direction;
			}
		}
	}

	public int rotate(int i, int j) {
		switch (this) {
			case ROT_90:
				return (i + j / 4) % j;
			case ROT_180:
				return (i + j / 2) % j;
			case ROT_270:
				return (i + j * 3 / 4) % j;
			default:
				return i;
		}
	}

	public static BlockRotation random(Random random) {
		BlockRotation[] blockRotations = values();
		return blockRotations[random.nextInt(blockRotations.length)];
	}

	public static List<BlockRotation> randomRotationOrder(Random random) {
		List<BlockRotation> list = Lists.<BlockRotation>newArrayList(values());
		Collections.shuffle(list, random);
		return list;
	}
}
