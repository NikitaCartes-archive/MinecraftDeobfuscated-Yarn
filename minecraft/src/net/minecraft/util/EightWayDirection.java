package net.minecraft.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;
import net.minecraft.util.math.Direction;

public enum EightWayDirection {
	field_11069(Direction.NORTH),
	field_11074(Direction.NORTH, Direction.EAST),
	field_11075(Direction.EAST),
	field_11070(Direction.SOUTH, Direction.EAST),
	field_11073(Direction.SOUTH),
	field_11068(Direction.SOUTH, Direction.WEST),
	field_11072(Direction.WEST),
	field_11076(Direction.NORTH, Direction.WEST);

	private static final int NORTHWEST_BIT = 1 << field_11076.ordinal();
	private static final int WEST_BIT = 1 << field_11072.ordinal();
	private static final int SOUTHWEST_BIT = 1 << field_11068.ordinal();
	private static final int SOUTH_BIT = 1 << field_11073.ordinal();
	private static final int SOUTHEAST_BIT = 1 << field_11070.ordinal();
	private static final int EAST_BIT = 1 << field_11075.ordinal();
	private static final int NORTHEAST_BIT = 1 << field_11074.ordinal();
	private static final int NORTH_BIT = 1 << field_11069.ordinal();
	private final Set<Direction> directions;

	private EightWayDirection(Direction... directions) {
		this.directions = Sets.immutableEnumSet(Arrays.asList(directions));
	}

	public Set<Direction> getDirections() {
		return this.directions;
	}
}
