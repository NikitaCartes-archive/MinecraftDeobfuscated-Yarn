package net.minecraft;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public enum class_5000 implements StringIdentifiable {
	field_23381("down_east", Direction.DOWN, Direction.EAST),
	field_23382("down_north", Direction.DOWN, Direction.NORTH),
	field_23383("down_south", Direction.DOWN, Direction.SOUTH),
	field_23384("down_west", Direction.DOWN, Direction.WEST),
	field_23385("up_east", Direction.UP, Direction.EAST),
	field_23386("up_north", Direction.UP, Direction.NORTH),
	field_23387("up_south", Direction.UP, Direction.SOUTH),
	field_23388("up_west", Direction.UP, Direction.WEST),
	field_23389("west_up", Direction.WEST, Direction.UP),
	field_23390("east_up", Direction.EAST, Direction.UP),
	field_23391("north_up", Direction.NORTH, Direction.UP),
	field_23392("south_up", Direction.SOUTH, Direction.UP);

	private static final Int2ObjectMap<class_5000> field_23393 = new Int2ObjectOpenHashMap<>(values().length);
	private final String field_23394;
	private final Direction field_23395;
	private final Direction field_23396;

	private static int method_26427(Direction direction, Direction direction2) {
		return direction.ordinal() << 3 | direction2.ordinal();
	}

	private class_5000(String string2, Direction direction, Direction direction2) {
		this.field_23394 = string2;
		this.field_23396 = direction;
		this.field_23395 = direction2;
	}

	@Override
	public String asString() {
		return this.field_23394;
	}

	public static class_5000 method_26425(Direction direction, Direction direction2) {
		int i = method_26427(direction2, direction);
		return field_23393.get(i);
	}

	public Direction method_26426() {
		return this.field_23396;
	}

	public Direction method_26428() {
		return this.field_23395;
	}

	static {
		for (class_5000 lv : values()) {
			field_23393.put(method_26427(lv.field_23395, lv.field_23396), lv);
		}
	}
}
