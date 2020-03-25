package net.minecraft.util;

import net.minecraft.class_4990;
import net.minecraft.util.math.Direction;

public enum BlockMirror {
	NONE(class_4990.field_23292),
	LEFT_RIGHT(class_4990.field_23267),
	FRONT_BACK(class_4990.field_23323);

	private final class_4990 field_23263;

	private BlockMirror(class_4990 arg) {
		this.field_23263 = arg;
	}

	public int mirror(int rotation, int fullTurn) {
		int i = fullTurn / 2;
		int j = rotation > i ? rotation - fullTurn : rotation;
		switch (this) {
			case FRONT_BACK:
				return (fullTurn - j) % fullTurn;
			case LEFT_RIGHT:
				return (i - j + fullTurn) % fullTurn;
			default:
				return rotation;
		}
	}

	public BlockRotation getRotation(Direction direction) {
		Direction.Axis axis = direction.getAxis();
		return (this != LEFT_RIGHT || axis != Direction.Axis.Z) && (this != FRONT_BACK || axis != Direction.Axis.X)
			? BlockRotation.NONE
			: BlockRotation.CLOCKWISE_180;
	}

	public Direction apply(Direction direction) {
		if (this == FRONT_BACK && direction.getAxis() == Direction.Axis.X) {
			return direction.getOpposite();
		} else {
			return this == LEFT_RIGHT && direction.getAxis() == Direction.Axis.Z ? direction.getOpposite() : direction;
		}
	}

	public class_4990 method_26380() {
		return this.field_23263;
	}
}
