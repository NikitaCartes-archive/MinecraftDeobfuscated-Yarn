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
	private final Vec3i field_37995;

	private EightWayDirection(Direction... directions) {
		this.directions = Sets.immutableEnumSet(Arrays.asList(directions));
		this.field_37995 = new Vec3i(0, 0, 0);

		for (Direction direction : directions) {
			this.field_37995
				.setX(this.field_37995.getX() + direction.getOffsetX())
				.setY(this.field_37995.getY() + direction.getOffsetY())
				.setZ(this.field_37995.getZ() + direction.getOffsetZ());
		}
	}

	public Set<Direction> getDirections() {
		return this.directions;
	}

	public int method_42015() {
		return this.field_37995.getX();
	}

	public int method_42016() {
		return this.field_37995.getZ();
	}
}
