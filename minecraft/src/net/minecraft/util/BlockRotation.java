package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.class_4990;
import net.minecraft.util.math.Direction;

public enum BlockRotation {
	NONE(class_4990.field_23292),
	CLOCKWISE_90(class_4990.field_23318),
	CLOCKWISE_180(class_4990.field_23300),
	COUNTERCLOCKWISE_90(class_4990.field_23319);

	private final class_4990 field_23264;

	private BlockRotation(class_4990 arg) {
		this.field_23264 = arg;
	}

	public BlockRotation rotate(BlockRotation rotation) {
		switch (rotation) {
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

	public class_4990 method_26383() {
		return this.field_23264;
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

	public int rotate(int rotation, int fullTurn) {
		switch (this) {
			case CLOCKWISE_90:
				return (rotation + fullTurn / 4) % fullTurn;
			case CLOCKWISE_180:
				return (rotation + fullTurn / 2) % fullTurn;
			case COUNTERCLOCKWISE_90:
				return (rotation + fullTurn * 3 / 4) % fullTurn;
			default:
				return rotation;
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
