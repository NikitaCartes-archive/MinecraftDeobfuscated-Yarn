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

	private final Set<Direction> directions;

	private EightWayDirection(Direction... directions) {
		this.directions = Sets.immutableEnumSet(Arrays.asList(directions));
	}

	public Set<Direction> getDirections() {
		return this.directions;
	}
}
