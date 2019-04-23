package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public enum CubeFace {
	field_3965(
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.SOUTH),
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.NORTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.NORTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.SOUTH)
	),
	field_3960(
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.NORTH),
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.SOUTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.SOUTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.NORTH)
	),
	field_3962(
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.NORTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.NORTH),
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.NORTH),
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.NORTH)
	),
	field_3963(
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.SOUTH),
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.SOUTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.SOUTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.SOUTH)
	),
	field_3966(
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.NORTH),
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.NORTH),
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.SOUTH),
		new CubeFace.Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.SOUTH)
	),
	field_3961(
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.SOUTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.SOUTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.NORTH),
		new CubeFace.Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.NORTH)
	);

	private static final CubeFace[] field_3958 = SystemUtil.consume(new CubeFace[6], cubeFaces -> {
		cubeFaces[CubeFace.DirectionIds.DOWN] = field_3965;
		cubeFaces[CubeFace.DirectionIds.UP] = field_3960;
		cubeFaces[CubeFace.DirectionIds.NORTH] = field_3962;
		cubeFaces[CubeFace.DirectionIds.SOUTH] = field_3963;
		cubeFaces[CubeFace.DirectionIds.WEST] = field_3966;
		cubeFaces[CubeFace.DirectionIds.EAST] = field_3961;
	});
	private final CubeFace.Corner[] corners;

	public static CubeFace method_3163(Direction direction) {
		return field_3958[direction.getId()];
	}

	private CubeFace(CubeFace.Corner... corners) {
		this.corners = corners;
	}

	public CubeFace.Corner getCorner(int i) {
		return this.corners[i];
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
		public static final int SOUTH = Direction.field_11035.getId();
		public static final int UP = Direction.field_11036.getId();
		public static final int EAST = Direction.field_11034.getId();
		public static final int NORTH = Direction.field_11043.getId();
		public static final int DOWN = Direction.field_11033.getId();
		public static final int WEST = Direction.field_11039.getId();
	}
}
