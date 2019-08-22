package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.util.math.Direction;

public enum BlockRotation {
	NONE,
	CLOCKWISE_90,
	CLOCKWISE_180,
	COUNTERCLOCKWISE_90;

	public BlockRotation rotate(BlockRotation blockRotation) {
		switch (blockRotation) {
			case CLOCKWISE_180:
				switch (this) {
					case NONE:
						return CLOCKWISE_180;
					case CLOCKWISE_90:
						return COUNTERCLOCKWISE_90;
					case CLOCKWISE_180:
						return NONE;
					case COUNTERCLOCKWISE_90:
						return CLOCKWISE_90;
				}
			case COUNTERCLOCKWISE_90:
				switch (this) {
					case NONE:
						return COUNTERCLOCKWISE_90;
					case CLOCKWISE_90:
						return NONE;
					case CLOCKWISE_180:
						return CLOCKWISE_90;
					case COUNTERCLOCKWISE_90:
						return CLOCKWISE_180;
				}
			case CLOCKWISE_90:
				switch (this) {
					case NONE:
						return CLOCKWISE_90;
					case CLOCKWISE_90:
						return CLOCKWISE_180;
					case CLOCKWISE_180:
						return COUNTERCLOCKWISE_90;
					case COUNTERCLOCKWISE_90:
						return NONE;
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
				case CLOCKWISE_90:
					return direction.rotateYClockwise();
				case CLOCKWISE_180:
					return direction.getOpposite();
				case COUNTERCLOCKWISE_90:
					return direction.rotateYCounterclockwise();
				default:
					return direction;
			}
		}
	}

	public int rotate(int i, int j) {
		switch (this) {
			case CLOCKWISE_90:
				return (i + j / 4) % j;
			case CLOCKWISE_180:
				return (i + j / 2) % j;
			case COUNTERCLOCKWISE_90:
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
