package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.util.math.Direction;

public enum Rotation {
	ROT_0,
	ROT_90,
	ROT_180,
	ROT_270;

	public Rotation method_10501(Rotation rotation) {
		switch (rotation) {
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

	public Direction method_10503(Direction direction) {
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

	public int method_10502(int i, int j) {
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

	public static Rotation method_16548(Random random) {
		Rotation[] rotations = values();
		return rotations[random.nextInt(rotations.length)];
	}

	public static List<Rotation> method_16547(Random random) {
		List<Rotation> list = Lists.<Rotation>newArrayList(values());
		Collections.shuffle(list, random);
		return list;
	}
}
