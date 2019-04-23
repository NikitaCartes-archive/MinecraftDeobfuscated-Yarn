package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.util.math.Direction;

public enum BlockRotation {
	field_11467,
	field_11463,
	field_11464,
	field_11465;

	public BlockRotation rotate(BlockRotation blockRotation) {
		switch (blockRotation) {
			case field_11464:
				switch (this) {
					case field_11467:
						return field_11464;
					case field_11463:
						return field_11465;
					case field_11464:
						return field_11467;
					case field_11465:
						return field_11463;
				}
			case field_11465:
				switch (this) {
					case field_11467:
						return field_11465;
					case field_11463:
						return field_11467;
					case field_11464:
						return field_11463;
					case field_11465:
						return field_11464;
				}
			case field_11463:
				switch (this) {
					case field_11467:
						return field_11463;
					case field_11463:
						return field_11464;
					case field_11464:
						return field_11465;
					case field_11465:
						return field_11467;
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
				case field_11463:
					return direction.rotateYClockwise();
				case field_11464:
					return direction.getOpposite();
				case field_11465:
					return direction.rotateYCounterclockwise();
				default:
					return direction;
			}
		}
	}

	public int rotate(int i, int j) {
		switch (this) {
			case field_11463:
				return (i + j / 4) % j;
			case field_11464:
				return (i + j / 2) % j;
			case field_11465:
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
