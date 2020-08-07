package net.minecraft.block.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public enum JigsawOrientation implements StringIdentifiable {
	field_23381("down_east", Direction.field_11033, Direction.field_11034),
	field_23382("down_north", Direction.field_11033, Direction.field_11043),
	field_23383("down_south", Direction.field_11033, Direction.field_11035),
	field_23384("down_west", Direction.field_11033, Direction.field_11039),
	field_23385("up_east", Direction.field_11036, Direction.field_11034),
	field_23386("up_north", Direction.field_11036, Direction.field_11043),
	field_23387("up_south", Direction.field_11036, Direction.field_11035),
	field_23388("up_west", Direction.field_11036, Direction.field_11039),
	field_23389("west_up", Direction.field_11039, Direction.field_11036),
	field_23390("east_up", Direction.field_11034, Direction.field_11036),
	field_23391("north_up", Direction.field_11043, Direction.field_11036),
	field_23392("south_up", Direction.field_11035, Direction.field_11036);

	private static final Int2ObjectMap<JigsawOrientation> BY_INDEX = new Int2ObjectOpenHashMap<>(values().length);
	private final String name;
	private final Direction rotation;
	private final Direction facing;

	private static int getIndex(Direction facing, Direction rotation) {
		return facing.ordinal() << 3 | rotation.ordinal();
	}

	private JigsawOrientation(String name, Direction facing, Direction rotation) {
		this.name = name;
		this.facing = facing;
		this.rotation = rotation;
	}

	@Override
	public String asString() {
		return this.name;
	}

	public static JigsawOrientation byDirections(Direction facing, Direction rotation) {
		int i = getIndex(rotation, facing);
		return BY_INDEX.get(i);
	}

	public Direction getFacing() {
		return this.facing;
	}

	public Direction getRotation() {
		return this.rotation;
	}

	static {
		for (JigsawOrientation jigsawOrientation : values()) {
			BY_INDEX.put(getIndex(jigsawOrientation.rotation, jigsawOrientation.facing), jigsawOrientation);
		}
	}
}
