package net.minecraft.util.math;

public enum AxisCycleDirection {
	NONE {
		@Override
		public int choose(int x, int y, int z, Direction.Axis axis) {
			return axis.choose(x, y, z);
		}

		@Override
		public double choose(double x, double y, double z, Direction.Axis axis) {
			return axis.choose(x, y, z);
		}

		@Override
		public Direction.Axis cycle(Direction.Axis axis) {
			return axis;
		}

		@Override
		public AxisCycleDirection opposite() {
			return this;
		}
	},
	FORWARD {
		@Override
		public int choose(int x, int y, int z, Direction.Axis axis) {
			return axis.choose(z, x, y);
		}

		@Override
		public double choose(double x, double y, double z, Direction.Axis axis) {
			return axis.choose(z, x, y);
		}

		@Override
		public Direction.Axis cycle(Direction.Axis axis) {
			return AXES[Math.floorMod(axis.ordinal() + 1, 3)];
		}

		@Override
		public AxisCycleDirection opposite() {
			return BACKWARD;
		}
	},
	BACKWARD {
		@Override
		public int choose(int x, int y, int z, Direction.Axis axis) {
			return axis.choose(y, z, x);
		}

		@Override
		public double choose(double x, double y, double z, Direction.Axis axis) {
			return axis.choose(y, z, x);
		}

		@Override
		public Direction.Axis cycle(Direction.Axis axis) {
			return AXES[Math.floorMod(axis.ordinal() - 1, 3)];
		}

		@Override
		public AxisCycleDirection opposite() {
			return FORWARD;
		}
	};

	public static final Direction.Axis[] AXES = Direction.Axis.values();
	public static final AxisCycleDirection[] VALUES = values();

	public abstract int choose(int x, int y, int z, Direction.Axis axis);

	public abstract double choose(double x, double y, double z, Direction.Axis axis);

	public abstract Direction.Axis cycle(Direction.Axis axis);

	public abstract AxisCycleDirection opposite();

	public static AxisCycleDirection between(Direction.Axis from, Direction.Axis to) {
		return VALUES[Math.floorMod(to.ordinal() - from.ordinal(), 3)];
	}
}
