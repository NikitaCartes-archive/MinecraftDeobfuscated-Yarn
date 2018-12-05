package net.minecraft.util;

import net.minecraft.util.math.Direction;

public enum Mirror {
	NONE,
	LEFT_RIGHT,
	FRONT_BACK;

	public int method_10344(int i, int j) {
		int k = j / 2;
		int l = i > k ? i - j : i;
		switch (this) {
			case FRONT_BACK:
				return (j - l) % j;
			case LEFT_RIGHT:
				return (k - l + j) % j;
			default:
				return i;
		}
	}

	public Rotation method_10345(Direction direction) {
		Direction.Axis axis = direction.getAxis();
		return (this != LEFT_RIGHT || axis != Direction.Axis.Z) && (this != FRONT_BACK || axis != Direction.Axis.X) ? Rotation.ROT_0 : Rotation.ROT_180;
	}

	public Direction method_10343(Direction direction) {
		if (this == FRONT_BACK && direction.getAxis() == Direction.Axis.X) {
			return direction.getOpposite();
		} else {
			return this == LEFT_RIGHT && direction.getAxis() == Direction.Axis.Z ? direction.getOpposite() : direction;
		}
	}
}
