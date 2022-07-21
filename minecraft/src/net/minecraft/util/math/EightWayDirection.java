package net.minecraft.util.math;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;

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
	private final Vec3i offset;

	private EightWayDirection(Direction... directions) {
		this.directions = Sets.immutableEnumSet(Arrays.asList(directions));
		this.offset = new Vec3i(0, 0, 0);

		for (Direction direction : directions) {
			this.offset
				.setX(this.offset.getX() + direction.getOffsetX())
				.setY(this.offset.getY() + direction.getOffsetY())
				.setZ(this.offset.getZ() + direction.getOffsetZ());
		}
	}

	public Set<Direction> getDirections() {
		return this.directions;
	}

	public int getOffsetX() {
		return this.offset.getX();
	}

	public int getOffsetZ() {
		return this.offset.getZ();
	}
}
