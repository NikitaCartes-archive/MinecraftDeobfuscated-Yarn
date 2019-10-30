package net.minecraft.util.math;

public class Boxes {
	public static Box stretch(Box box, Direction direction, double length) {
		double d = length * (double)direction.getDirection().offset();
		double e = Math.min(d, 0.0);
		double f = Math.max(d, 0.0);
		switch (direction) {
			case WEST:
				return new Box(box.x1 + e, box.y1, box.z1, box.x1 + f, box.y2, box.z2);
			case EAST:
				return new Box(box.x2 + e, box.y1, box.z1, box.x2 + f, box.y2, box.z2);
			case DOWN:
				return new Box(box.x1, box.y1 + e, box.z1, box.x2, box.y1 + f, box.z2);
			case UP:
			default:
				return new Box(box.x1, box.y2 + e, box.z1, box.x2, box.y2 + f, box.z2);
			case NORTH:
				return new Box(box.x1, box.y1, box.z1 + e, box.x2, box.y2, box.z1 + f);
			case SOUTH:
				return new Box(box.x1, box.y1, box.z2 + e, box.x2, box.y2, box.z2 + f);
		}
	}
}
