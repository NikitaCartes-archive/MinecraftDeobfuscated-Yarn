package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;

public enum Orientation implements StringIdentifiable {
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

	private static final int DIRECTIONS = Direction.values().length;
	private static final Orientation[] VALUES = Util.make(new Orientation[DIRECTIONS * DIRECTIONS], values -> {
		for (Orientation orientation : values()) {
			values[getIndex(orientation.facing, orientation.rotation)] = orientation;
		}
	});
	private final String name;
	private final Direction rotation;
	private final Direction facing;

	private static int getIndex(Direction facing, Direction rotation) {
		return facing.ordinal() * DIRECTIONS + rotation.ordinal();
	}

	private Orientation(final String name, final Direction facing, final Direction rotation) {
		this.name = name;
		this.facing = facing;
		this.rotation = rotation;
	}

	@Override
	public String asString() {
		return this.name;
	}

	public static Orientation byDirections(Direction facing, Direction rotation) {
		return VALUES[getIndex(facing, rotation)];
	}

	public Direction getFacing() {
		return this.facing;
	}

	public Direction getRotation() {
		return this.rotation;
	}
}
