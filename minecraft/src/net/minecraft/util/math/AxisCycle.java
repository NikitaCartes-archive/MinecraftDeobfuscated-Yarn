package net.minecraft.util.math;

public enum AxisCycle {
	field_10962 {
		@Override
		public int method_10056(int i, int j, int k, Direction.Axis axis) {
			return axis.choose(i, j, k);
		}

		@Override
		public Direction.Axis method_10058(Direction.Axis axis) {
			return axis;
		}

		@Override
		public AxisCycle opposite() {
			return this;
		}
	},
	field_10963 {
		@Override
		public int method_10056(int i, int j, int k, Direction.Axis axis) {
			return axis.choose(k, i, j);
		}

		@Override
		public Direction.Axis method_10058(Direction.Axis axis) {
			return field_10961[Math.floorMod(axis.ordinal() + 1, 3)];
		}

		@Override
		public AxisCycle opposite() {
			return field_10965;
		}
	},
	field_10965 {
		@Override
		public int method_10056(int i, int j, int k, Direction.Axis axis) {
			return axis.choose(j, k, i);
		}

		@Override
		public Direction.Axis method_10058(Direction.Axis axis) {
			return field_10961[Math.floorMod(axis.ordinal() - 1, 3)];
		}

		@Override
		public AxisCycle opposite() {
			return field_10963;
		}
	};

	public static final Direction.Axis[] field_10961 = Direction.Axis.values();
	public static final AxisCycle[] VALUES = values();

	private AxisCycle() {
	}

	public abstract int method_10056(int i, int j, int k, Direction.Axis axis);

	public abstract Direction.Axis method_10058(Direction.Axis axis);

	public abstract AxisCycle opposite();

	public static AxisCycle method_10057(Direction.Axis axis, Direction.Axis axis2) {
		return VALUES[Math.floorMod(axis2.ordinal() - axis.ordinal(), 3)];
	}
}
