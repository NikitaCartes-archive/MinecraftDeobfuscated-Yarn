package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public enum CubeFace {
	DOWN(
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.SOUTH),
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.NORTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.NORTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.SOUTH)
	),
	UP(
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.NORTH),
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.SOUTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.SOUTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.NORTH)
	),
	NORTH(
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.NORTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.NORTH),
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.NORTH),
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.NORTH)
	),
	SOUTH(
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.SOUTH),
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.SOUTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.SOUTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.SOUTH)
	),
	WEST(
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.NORTH),
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.NORTH),
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.SOUTH),
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.SOUTH)
	),
	EAST(
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.SOUTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.SOUTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.NORTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.NORTH)
	);

	private static final CubeFace[] DIRECTION_LOOKUP = Util.make(new CubeFace[6], lookup -> {
		lookup[CubeFace.DirectionIds.DOWN] = DOWN;
		lookup[CubeFace.DirectionIds.UP] = UP;
		lookup[CubeFace.DirectionIds.NORTH] = NORTH;
		lookup[CubeFace.DirectionIds.SOUTH] = SOUTH;
		lookup[CubeFace.DirectionIds.WEST] = WEST;
		lookup[CubeFace.DirectionIds.EAST] = EAST;
	});
	private final CubeFace.Corner[] corners;

	public static CubeFace getFace(Direction direction) {
		return DIRECTION_LOOKUP[direction.getId()];
	}

	private CubeFace(final CubeFace.Corner... corners) {
		this.corners = corners;
	}

	public CubeFace.Corner getCorner(int corner) {
		return this.corners[corner];
	}

	@Environment(EnvType.CLIENT)
	public static class Corner {
		public final int xSide;
		public final int ySide;
		public final int zSide;

		Corner(int xSide, int ySide, int zSide) {
			this.xSide = xSide;
			this.ySide = ySide;
			this.zSide = zSide;
		}
	}

	@Environment(EnvType.CLIENT)
	public static final class DirectionIds {
		public static final int SOUTH = Direction.SOUTH.getId();
		public static final int UP = Direction.UP.getId();
		public static final int EAST = Direction.EAST.getId();
		public static final int NORTH = Direction.NORTH.getId();
		public static final int DOWN = Direction.DOWN.getId();
		public static final int WEST = Direction.WEST.getId();
	}
}
