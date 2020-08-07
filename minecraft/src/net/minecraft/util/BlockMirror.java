package net.minecraft.util;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;

public enum BlockMirror {
	field_11302(DirectionTransformation.field_23292),
	field_11300(DirectionTransformation.field_23267),
	field_11301(DirectionTransformation.field_23323);

	private final DirectionTransformation directionTransformation;

	private BlockMirror(DirectionTransformation directionTransformation) {
		this.directionTransformation = directionTransformation;
	}

	public int mirror(int rotation, int fullTurn) {
		int i = fullTurn / 2;
		int j = rotation > i ? rotation - fullTurn : rotation;
		switch (this) {
			case field_11301:
				return (fullTurn - j) % fullTurn;
			case field_11300:
				return (i - j + fullTurn) % fullTurn;
			default:
				return rotation;
		}
	}

	public BlockRotation getRotation(Direction direction) {
		Direction.Axis axis = direction.getAxis();
		return (this != field_11300 || axis != Direction.Axis.field_11051) && (this != field_11301 || axis != Direction.Axis.field_11048)
			? BlockRotation.field_11467
			: BlockRotation.field_11464;
	}

	public Direction apply(Direction direction) {
		if (this == field_11301 && direction.getAxis() == Direction.Axis.field_11048) {
			return direction.getOpposite();
		} else {
			return this == field_11300 && direction.getAxis() == Direction.Axis.field_11051 ? direction.getOpposite() : direction;
		}
	}

	public DirectionTransformation getDirectionTransformation() {
		return this.directionTransformation;
	}
}
