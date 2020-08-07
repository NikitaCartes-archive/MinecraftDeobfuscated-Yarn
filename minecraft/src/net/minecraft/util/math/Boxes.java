package net.minecraft.util.math;

public class Boxes {
	public static Box stretch(Box box, Direction direction, double length) {
		double d = length * (double)direction.getDirection().offset();
		double e = Math.min(d, 0.0);
		double f = Math.max(d, 0.0);
		switch (direction) {
			case field_11039:
				return new Box(box.minX + e, box.minY, box.minZ, box.minX + f, box.maxY, box.maxZ);
			case field_11034:
				return new Box(box.maxX + e, box.minY, box.minZ, box.maxX + f, box.maxY, box.maxZ);
			case field_11033:
				return new Box(box.minX, box.minY + e, box.minZ, box.maxX, box.minY + f, box.maxZ);
			case field_11036:
			default:
				return new Box(box.minX, box.maxY + e, box.minZ, box.maxX, box.maxY + f, box.maxZ);
			case field_11043:
				return new Box(box.minX, box.minY, box.minZ + e, box.maxX, box.maxY, box.minZ + f);
			case field_11035:
				return new Box(box.minX, box.minY, box.maxZ + e, box.maxX, box.maxY, box.maxZ + f);
		}
	}
}
