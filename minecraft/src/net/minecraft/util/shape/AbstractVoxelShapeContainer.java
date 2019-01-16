package net.minecraft.util.shape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.AxisCycle;
import net.minecraft.util.math.Direction;

public abstract class AbstractVoxelShapeContainer {
	private static final Direction.Axis[] AXES = Direction.Axis.values();
	public final int xSize;
	public final int ySize;
	public final int zSize;

	protected AbstractVoxelShapeContainer(int i, int j, int k) {
		this.xSize = i;
		this.ySize = j;
		this.zSize = k;
	}

	public boolean method_1062(AxisCycle axisCycle, int i, int j, int k) {
		return this.method_1044(
			axisCycle.method_10056(i, j, k, Direction.Axis.X), axisCycle.method_10056(i, j, k, Direction.Axis.Y), axisCycle.method_10056(i, j, k, Direction.Axis.Z)
		);
	}

	public boolean method_1044(int i, int j, int k) {
		if (i < 0 || j < 0 || k < 0) {
			return false;
		} else {
			return i < this.xSize && j < this.ySize && k < this.zSize ? this.contains(i, j, k) : false;
		}
	}

	public boolean method_1057(AxisCycle axisCycle, int i, int j, int k) {
		return this.contains(
			axisCycle.method_10056(i, j, k, Direction.Axis.X), axisCycle.method_10056(i, j, k, Direction.Axis.Y), axisCycle.method_10056(i, j, k, Direction.Axis.Z)
		);
	}

	public abstract boolean contains(int i, int j, int k);

	public abstract void modify(int i, int j, int k, boolean bl, boolean bl2);

	public boolean isEmpty() {
		for (Direction.Axis axis : AXES) {
			if (this.getMin(axis) >= this.getMax(axis)) {
				return true;
			}
		}

		return false;
	}

	public abstract int getMin(Direction.Axis axis);

	public abstract int getMax(Direction.Axis axis);

	@Environment(EnvType.CLIENT)
	public int method_1043(Direction.Axis axis, int i, int j) {
		int k = this.getSize(axis);
		if (i >= 0 && j >= 0) {
			Direction.Axis axis2 = AxisCycle.NEXT.cycle(axis);
			Direction.Axis axis3 = AxisCycle.PREVIOUS.cycle(axis);
			if (i < this.getSize(axis2) && j < this.getSize(axis3)) {
				AxisCycle axisCycle = AxisCycle.between(Direction.Axis.X, axis);

				for (int l = 0; l < k; l++) {
					if (this.method_1057(axisCycle, l, i, j)) {
						return l;
					}
				}

				return k;
			} else {
				return k;
			}
		} else {
			return k;
		}
	}

	@Environment(EnvType.CLIENT)
	public int method_1058(Direction.Axis axis, int i, int j) {
		if (i >= 0 && j >= 0) {
			Direction.Axis axis2 = AxisCycle.NEXT.cycle(axis);
			Direction.Axis axis3 = AxisCycle.PREVIOUS.cycle(axis);
			if (i < this.getSize(axis2) && j < this.getSize(axis3)) {
				int k = this.getSize(axis);
				AxisCycle axisCycle = AxisCycle.between(Direction.Axis.X, axis);

				for (int l = k - 1; l >= 0; l--) {
					if (this.method_1057(axisCycle, l, i, j)) {
						return l + 1;
					}
				}

				return 0;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	public int getSize(Direction.Axis axis) {
		return axis.choose(this.xSize, this.ySize, this.zSize);
	}

	public int getXSize() {
		return this.getSize(Direction.Axis.X);
	}

	public int getYSize() {
		return this.getSize(Direction.Axis.Y);
	}

	public int getZSize() {
		return this.getSize(Direction.Axis.Z);
	}

	@Environment(EnvType.CLIENT)
	public void method_1064(AbstractVoxelShapeContainer.class_253 arg, boolean bl) {
		this.method_1052(arg, AxisCycle.NONE, bl);
		this.method_1052(arg, AxisCycle.NEXT, bl);
		this.method_1052(arg, AxisCycle.PREVIOUS, bl);
	}

	@Environment(EnvType.CLIENT)
	private void method_1052(AbstractVoxelShapeContainer.class_253 arg, AxisCycle axisCycle, boolean bl) {
		AxisCycle axisCycle2 = axisCycle.opposite();
		int i = this.getSize(axisCycle2.cycle(Direction.Axis.X));
		int j = this.getSize(axisCycle2.cycle(Direction.Axis.Y));
		int k = this.getSize(axisCycle2.cycle(Direction.Axis.Z));

		for (int l = 0; l <= i; l++) {
			for (int m = 0; m <= j; m++) {
				int n = -1;

				for (int o = 0; o <= k; o++) {
					int p = 0;
					int q = 0;

					for (int r = 0; r <= 1; r++) {
						for (int s = 0; s <= 1; s++) {
							if (this.method_1062(axisCycle2, l + r - 1, m + s - 1, o)) {
								p++;
								q ^= r ^ s;
							}
						}
					}

					if (p == 1 || p == 3 || p == 2 && (q & 1) == 0) {
						if (bl) {
							if (n == -1) {
								n = o;
							}
						} else {
							arg.consume(
								axisCycle2.method_10056(l, m, o, Direction.Axis.X),
								axisCycle2.method_10056(l, m, o, Direction.Axis.Y),
								axisCycle2.method_10056(l, m, o, Direction.Axis.Z),
								axisCycle2.method_10056(l, m, o + 1, Direction.Axis.X),
								axisCycle2.method_10056(l, m, o + 1, Direction.Axis.Y),
								axisCycle2.method_10056(l, m, o + 1, Direction.Axis.Z)
							);
						}
					} else if (n != -1) {
						arg.consume(
							axisCycle2.method_10056(l, m, n, Direction.Axis.X),
							axisCycle2.method_10056(l, m, n, Direction.Axis.Y),
							axisCycle2.method_10056(l, m, n, Direction.Axis.Z),
							axisCycle2.method_10056(l, m, o, Direction.Axis.X),
							axisCycle2.method_10056(l, m, o, Direction.Axis.Y),
							axisCycle2.method_10056(l, m, o, Direction.Axis.Z)
						);
						n = -1;
					}
				}
			}
		}
	}

	protected boolean method_1059(int i, int j, int k, int l) {
		for (int m = i; m < j; m++) {
			if (!this.method_1044(k, l, m)) {
				return false;
			}
		}

		return true;
	}

	protected void method_1060(int i, int j, int k, int l, boolean bl) {
		for (int m = i; m < j; m++) {
			this.modify(k, l, m, false, bl);
		}
	}

	protected boolean method_1054(int i, int j, int k, int l, int m) {
		for (int n = i; n < j; n++) {
			if (!this.method_1059(k, l, n, m)) {
				return false;
			}
		}

		return true;
	}

	public void method_1053(AbstractVoxelShapeContainer.class_253 arg, boolean bl) {
		AbstractVoxelShapeContainer abstractVoxelShapeContainer = new BitSetVoxelShapeContainer(this);

		for (int i = 0; i <= this.xSize; i++) {
			for (int j = 0; j <= this.ySize; j++) {
				int k = -1;

				for (int l = 0; l <= this.zSize; l++) {
					if (abstractVoxelShapeContainer.method_1044(i, j, l)) {
						if (bl) {
							if (k == -1) {
								k = l;
							}
						} else {
							arg.consume(i, j, l, i + 1, j + 1, l + 1);
						}
					} else if (k != -1) {
						int m = i;
						int n = i;
						int o = j;
						int p = j;
						abstractVoxelShapeContainer.method_1060(k, l, i, j, false);

						while (abstractVoxelShapeContainer.method_1059(k, l, m - 1, o)) {
							abstractVoxelShapeContainer.method_1060(k, l, m - 1, o, false);
							m--;
						}

						while (abstractVoxelShapeContainer.method_1059(k, l, n + 1, o)) {
							abstractVoxelShapeContainer.method_1060(k, l, n + 1, o, false);
							n++;
						}

						while (abstractVoxelShapeContainer.method_1054(m, n + 1, k, l, o - 1)) {
							for (int q = m; q <= n; q++) {
								abstractVoxelShapeContainer.method_1060(k, l, q, o - 1, false);
							}

							o--;
						}

						while (abstractVoxelShapeContainer.method_1054(m, n + 1, k, l, p + 1)) {
							for (int q = m; q <= n; q++) {
								abstractVoxelShapeContainer.method_1060(k, l, q, p + 1, false);
							}

							p++;
						}

						arg.consume(m, o, k, n + 1, p + 1, l);
						k = -1;
					}
				}
			}
		}
	}

	public void method_1046(AbstractVoxelShapeContainer.class_252 arg) {
		this.method_1061(arg, AxisCycle.NONE);
		this.method_1061(arg, AxisCycle.NEXT);
		this.method_1061(arg, AxisCycle.PREVIOUS);
	}

	private void method_1061(AbstractVoxelShapeContainer.class_252 arg, AxisCycle axisCycle) {
		AxisCycle axisCycle2 = axisCycle.opposite();
		Direction.Axis axis = axisCycle2.cycle(Direction.Axis.Z);
		int i = this.getSize(axisCycle2.cycle(Direction.Axis.X));
		int j = this.getSize(axisCycle2.cycle(Direction.Axis.Y));
		int k = this.getSize(axis);
		Direction direction = Direction.from(axis, Direction.AxisDirection.NEGATIVE);
		Direction direction2 = Direction.from(axis, Direction.AxisDirection.POSITIVE);

		for (int l = 0; l < i; l++) {
			for (int m = 0; m < j; m++) {
				boolean bl = false;

				for (int n = 0; n <= k; n++) {
					boolean bl2 = n != k && this.method_1057(axisCycle2, l, m, n);
					if (!bl && bl2) {
						arg.consume(
							direction,
							axisCycle2.method_10056(l, m, n, Direction.Axis.X),
							axisCycle2.method_10056(l, m, n, Direction.Axis.Y),
							axisCycle2.method_10056(l, m, n, Direction.Axis.Z)
						);
					}

					if (bl && !bl2) {
						arg.consume(
							direction2,
							axisCycle2.method_10056(l, m, n - 1, Direction.Axis.X),
							axisCycle2.method_10056(l, m, n - 1, Direction.Axis.Y),
							axisCycle2.method_10056(l, m, n - 1, Direction.Axis.Z)
						);
					}

					bl = bl2;
				}
			}
		}
	}

	public interface class_252 {
		void consume(Direction direction, int i, int j, int k);
	}

	public interface class_253 {
		void consume(int i, int j, int k, int l, int m, int n);
	}
}
