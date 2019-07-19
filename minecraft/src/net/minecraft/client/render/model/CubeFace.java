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

	private static final CubeFace[] field_3958 = Util.make(new CubeFace[6], cubeFaces -> {
		cubeFaces[CubeFace.DirectionIds.DOWN] = DOWN;
		cubeFaces[CubeFace.DirectionIds.UP] = UP;
		cubeFaces[CubeFace.DirectionIds.NORTH] = NORTH;
		cubeFaces[CubeFace.DirectionIds.SOUTH] = SOUTH;
		cubeFaces[CubeFace.DirectionIds.WEST] = WEST;
		cubeFaces[CubeFace.DirectionIds.EAST] = EAST;
	});
	private final CubeFace.Corner[] corners;

	public static CubeFace method_3163(Direction direction) {
		return field_3958[direction.getId()];
	}

	private CubeFace(CubeFace.Corner... corners) {
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

		private Corner(int i, int j, int k) {
			this.xSide = i;
			this.ySide = j;
			this.zSide = k;
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
