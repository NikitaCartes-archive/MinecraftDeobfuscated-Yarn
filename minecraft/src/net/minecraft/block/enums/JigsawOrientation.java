package net.minecraft.block.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public enum JigsawOrientation implements StringIdentifiable {
	DOWN_EAST("down_east", Direction.DOWN, Direction.EAST),
	DOWN_NORTH("down_north", Direction.DOWN, Direction.NORTH),
	DOWN_SOUTH("down_south", Direction.DOWN, Direction.SOUTH),
	DOWN_WEST("down_west", Direction.DOWN, Direction.WEST),
	UP_EAST("up_east", Direction.UP, Direction.EAST),
	UP_NORTH("up_north", Direction.UP, Direction.NORTH),
	UP_SOUTH("up_south", Direction.UP, Direction.SOUTH),
	UP_WEST("up_west", Direction.UP, Direction.WEST),
	WEST_UP("west_up", Direction.WEST, Direction.UP),
	EAST_UP("east_up", Direction.EAST, Direction.UP),
	NORTH_UP("north_up", Direction.NORTH, Direction.UP),
	SOUTH_UP("south_up", Direction.SOUTH, Direction.UP);

	private static final Int2ObjectMap<JigsawOrientation> BY_INDEX = new Int2ObjectOpenHashMap<>(values().length);
	private final String name;
	private final Direction field_23395;
	private final Direction field_23396;

	private static int getIndex(Direction direction, Direction direction2) {
		return direction.ordinal() << 3 | direction2.ordinal();
	}

	private JigsawOrientation(String name, Direction direction, Direction direction2) {
		this.name = name;
		this.field_23396 = direction;
		this.field_23395 = direction2;
	}

	@Override
	public String asString() {
		return this.name;
	}

	public static JigsawOrientation byDirections(Direction direction, Direction direction2) {
		int i = getIndex(direction2, direction);
		return BY_INDEX.get(i);
	}

	public Direction method_26426() {
		return this.field_23396;
	}

	public Direction method_26428() {
		return this.field_23395;
	}

	static {
		for (JigsawOrientation jigsawOrientation : values()) {
			BY_INDEX.put(getIndex(jigsawOrientation.field_23395, jigsawOrientation.field_23396), jigsawOrientation);
		}
	}
}
