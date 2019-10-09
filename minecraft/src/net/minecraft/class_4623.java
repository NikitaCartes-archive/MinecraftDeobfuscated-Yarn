package net.minecraft;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class class_4623 {
	public static List<Box> method_23363(boolean bl, Box box, Direction direction, double d) {
		ArrayList<Box> arrayList = Lists.newArrayList(method_23362(box, direction, d));
		if (bl && direction.getAxis().isHorizontal()) {
			arrayList.add(method_23361(box));
		}

		return arrayList;
	}

	public static Box method_23361(Box box) {
		return new Box(box.minX, box.maxY, box.minZ, box.maxX, box.maxY + 1.0E-7, box.maxZ);
	}

	public static Box method_23362(Box box, Direction direction, double d) {
		double e = d * (double)direction.getDirection().offset();
		double f = Math.min(e, 0.0);
		double g = Math.max(e, 0.0);
		switch (direction) {
			case WEST:
				return new Box(box.minX + f, box.minY, box.minZ, box.minX + g, box.maxY, box.maxZ);
			case EAST:
				return new Box(box.maxX + f, box.minY, box.minZ, box.maxX + g, box.maxY, box.maxZ);
			case DOWN:
				return new Box(box.minX, box.minY + f, box.minZ, box.maxX, box.minY + g, box.maxZ);
			case UP:
			default:
				return new Box(box.minX, box.maxY + f, box.minZ, box.maxX, box.maxY + g, box.maxZ);
			case NORTH:
				return new Box(box.minX, box.minY, box.minZ + f, box.maxX, box.maxY, box.minZ + g);
			case SOUTH:
				return new Box(box.minX, box.minY, box.maxZ + f, box.maxX, box.maxY, box.maxZ + g);
		}
	}
}
