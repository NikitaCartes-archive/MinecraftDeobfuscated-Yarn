package net.minecraft.util.math;

public enum AxisCycle {
	NONE {
		@Override
		public int method_10056(int i, int j, int k, Direction.Axis axis) {
			return axis.choose(i, j, k);
		}

		@Override
		public Direction.Axis cycle(Direction.Axis axis) {
			return axis;
		}

		@Override
		public AxisCycle opposite() {
			return this;
		}
	},
	NEXT {
		@Override
		public int method_10056(int i, int j, int k, Direction.Axis axis) {
			return axis.choose(k, i, j);
		}

		@Override
		public Direction.Axis cycle(Direction.Axis axis) {
			return AXES[Math.floorMod(axis.ordinal() + 1, 3)];
		}

		@Override
		public AxisCycle opposite() {
			return PREVIOUS;
		}
	},
	PREVIOUS {
		@Override
		public int method_10056(int i, int j, int k, Direction.Axis axis) {
			return axis.choose(j, k, i);
		}

		@Override
		public Direction.Axis cycle(Direction.Axis axis) {
			return AXES[Math.floorMod(axis.ordinal() - 1, 3)];
		}

		@Override
		public AxisCycle opposite() {
			return NEXT;
		}
	};

	public static final Direction.Axis[] AXES = Direction.Axis.values();
	public static final AxisCycle[] VALUES = values();

	private AxisCycle() {
	}

	public abstract int method_10056(int i, int j, int k, Direction.Axis axis);

	public abstract Direction.Axis cycle(Direction.Axis axis);

	public abstract AxisCycle opposite();

	public static AxisCycle between(Direction.Axis axis, Direction.Axis axis2) {
		return VALUES[Math.floorMod(axis2.ordinal() - axis.ordinal(), 3)];
	}
}
