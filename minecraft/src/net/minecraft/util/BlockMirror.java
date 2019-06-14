package net.minecraft.util;

import net.minecraft.util.math.Direction;

public enum BlockMirror {
	field_11302,
	field_11300,
	field_11301;

	public int mirror(int i, int j) {
		int k = j / 2;
		int l = i > k ? i - j : i;
		switch (this) {
			case field_11301:
				return (j - l) % j;
			case field_11300:
				return (k - l + j) % j;
			default:
				return i;
		}
	}

	public BlockRotation method_10345(Direction direction) {
		Direction.Axis axis = direction.getAxis();
		return (this != field_11300 || axis != Direction.Axis.Z) && (this != field_11301 || axis != Direction.Axis.X)
			? BlockRotation.field_11467
			: BlockRotation.field_11464;
	}

	public Direction apply(Direction direction) {
		if (this == field_11301 && direction.getAxis() == Direction.Axis.X) {
			return direction.getOpposite();
		} else {
			return this == field_11300 && direction.getAxis() == Direction.Axis.Z ? direction.getOpposite() : direction;
		}
	}
}
