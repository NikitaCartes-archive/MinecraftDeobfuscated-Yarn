package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;

public enum BlockRotation {
	field_11467(DirectionTransformation.field_23292),
	field_11463(DirectionTransformation.field_23318),
	field_11464(DirectionTransformation.field_23300),
	field_11465(DirectionTransformation.field_23319);

	private final DirectionTransformation directionTransformation;

	private BlockRotation(DirectionTransformation directionTransformation) {
		this.directionTransformation = directionTransformation;
	}

	public BlockRotation rotate(BlockRotation rotation) {
		switch (rotation) {
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

	public DirectionTransformation getDirectionTransformation() {
		return this.directionTransformation;
	}

	public Direction rotate(Direction direction) {
		if (direction.getAxis() == Direction.Axis.field_11052) {
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

	public int rotate(int rotation, int fullTurn) {
		switch (this) {
			case field_11463:
				return (rotation + fullTurn / 4) % fullTurn;
			case field_11464:
				return (rotation + fullTurn / 2) % fullTurn;
			case field_11465:
				return (rotation + fullTurn * 3 / 4) % fullTurn;
			default:
				return rotation;
		}
	}

	public static BlockRotation random(Random random) {
		return Util.getRandom(values(), random);
	}

	public static List<BlockRotation> randomRotationOrder(Random random) {
		List<BlockRotation> list = Lists.<BlockRotation>newArrayList(values());
		Collections.shuffle(list, random);
		return list;
	}
}
