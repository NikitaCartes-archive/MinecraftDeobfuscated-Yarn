package net.minecraft.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;
import net.minecraft.util.math.Direction;

public enum EightWayDirection {
	NORTH(Direction.NORTH),
	NORTH_EAST(Direction.NORTH, Direction.EAST),
	EAST(Direction.EAST),
	SOUTH_EAST(Direction.SOUTH, Direction.EAST),
	SOUTH(Direction.SOUTH),
	SOUTH_WEST(Direction.SOUTH, Direction.WEST),
	WEST(Direction.WEST),
	NORTH_WEST(Direction.NORTH, Direction.WEST);

	private static final int NORTHWEST_BIT = 1 << NORTH_WEST.ordinal();
	private static final int WEST_BIT = 1 << WEST.ordinal();
	private static final int SOUTHWEST_BIT = 1 << SOUTH_WEST.ordinal();
	private static final int SOUTH_BIT = 1 << SOUTH.ordinal();
	private static final int SOUTHEAST_BIT = 1 << SOUTH_EAST.ordinal();
	private static final int EAST_BIT = 1 << EAST.ordinal();
	private static final int NORTHEAST_BIT = 1 << NORTH_EAST.ordinal();
	private static final int NORTH_BIT = 1 << NORTH.ordinal();
	private final Set<Direction> directions;

	private EightWayDirection(Direction... directions) {
		this.directions = Sets.immutableEnumSet(Arrays.asList(directions));
	}

	public Set<Direction> getDirections() {
		return this.directions;
	}
}
