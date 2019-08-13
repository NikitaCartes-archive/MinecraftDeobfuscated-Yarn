package net.minecraft.util.shape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.Direction;

public abstract class VoxelSet {
	private static final Direction.Axis[] AXES = Direction.Axis.values();
	protected final int xSize;
	protected final int ySize;
	protected final int zSize;

	protected VoxelSet(int i, int j, int k) {
		this.xSize = i;
		this.ySize = j;
		this.zSize = k;
	}

	public boolean inBoundsAndContains(AxisCycleDirection axisCycleDirection, int i, int j, int k) {
		return this.inBoundsAndContains(
			axisCycleDirection.choose(i, j, k, Direction.Axis.field_11048),
			axisCycleDirection.choose(i, j, k, Direction.Axis.field_11052),
			axisCycleDirection.choose(i, j, k, Direction.Axis.field_11051)
		);
	}

	public boolean inBoundsAndContains(int i, int j, int k) {
		if (i < 0 || j < 0 || k < 0) {
			return false;
		} else {
			return i < this.xSize && j < this.ySize && k < this.zSize ? this.contains(i, j, k) : false;
		}
	}

	public boolean contains(AxisCycleDirection axisCycleDirection, int i, int j, int k) {
		return this.contains(
			axisCycleDirection.choose(i, j, k, Direction.Axis.field_11048),
			axisCycleDirection.choose(i, j, k, Direction.Axis.field_11052),
			axisCycleDirection.choose(i, j, k, Direction.Axis.field_11051)
		);
	}

	public abstract boolean contains(int i, int j, int k);

	public abstract void set(int i, int j, int k, boolean bl, boolean bl2);

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
			Direction.Axis axis2 = AxisCycleDirection.field_10963.cycle(axis);
			Direction.Axis axis3 = AxisCycleDirection.field_10965.cycle(axis);
			if (i < this.getSize(axis2) && j < this.getSize(axis3)) {
				AxisCycleDirection axisCycleDirection = AxisCycleDirection.between(Direction.Axis.field_11048, axis);

				for (int l = 0; l < k; l++) {
					if (this.contains(axisCycleDirection, l, i, j)) {
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
			Direction.Axis axis2 = AxisCycleDirection.field_10963.cycle(axis);
			Direction.Axis axis3 = AxisCycleDirection.field_10965.cycle(axis);
			if (i < this.getSize(axis2) && j < this.getSize(axis3)) {
				int k = this.getSize(axis);
				AxisCycleDirection axisCycleDirection = AxisCycleDirection.between(Direction.Axis.field_11048, axis);

				for (int l = k - 1; l >= 0; l--) {
					if (this.contains(axisCycleDirection, l, i, j)) {
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
		return this.getSize(Direction.Axis.field_11048);
	}

	public int getYSize() {
		return this.getSize(Direction.Axis.field_11052);
	}

	public int getZSize() {
		return this.getSize(Direction.Axis.field_11051);
	}

	@Environment(EnvType.CLIENT)
	public void forEachEdge(VoxelSet.class_253 arg, boolean bl) {
		this.forEachEdge(arg, AxisCycleDirection.field_10962, bl);
		this.forEachEdge(arg, AxisCycleDirection.field_10963, bl);
		this.forEachEdge(arg, AxisCycleDirection.field_10965, bl);
	}

	@Environment(EnvType.CLIENT)
	private void forEachEdge(VoxelSet.class_253 arg, AxisCycleDirection axisCycleDirection, boolean bl) {
		AxisCycleDirection axisCycleDirection2 = axisCycleDirection.opposite();
		int i = this.getSize(axisCycleDirection2.cycle(Direction.Axis.field_11048));
		int j = this.getSize(axisCycleDirection2.cycle(Direction.Axis.field_11052));
		int k = this.getSize(axisCycleDirection2.cycle(Direction.Axis.field_11051));

		for (int l = 0; l <= i; l++) {
			for (int m = 0; m <= j; m++) {
				int n = -1;

				for (int o = 0; o <= k; o++) {
					int p = 0;
					int q = 0;

					for (int r = 0; r <= 1; r++) {
						for (int s = 0; s <= 1; s++) {
							if (this.inBoundsAndContains(axisCycleDirection2, l + r - 1, m + s - 1, o)) {
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
								axisCycleDirection2.choose(l, m, o, Direction.Axis.field_11048),
								axisCycleDirection2.choose(l, m, o, Direction.Axis.field_11052),
								axisCycleDirection2.choose(l, m, o, Direction.Axis.field_11051),
								axisCycleDirection2.choose(l, m, o + 1, Direction.Axis.field_11048),
								axisCycleDirection2.choose(l, m, o + 1, Direction.Axis.field_11052),
								axisCycleDirection2.choose(l, m, o + 1, Direction.Axis.field_11051)
							);
						}
					} else if (n != -1) {
						arg.consume(
							axisCycleDirection2.choose(l, m, n, Direction.Axis.field_11048),
							axisCycleDirection2.choose(l, m, n, Direction.Axis.field_11052),
							axisCycleDirection2.choose(l, m, n, Direction.Axis.field_11051),
							axisCycleDirection2.choose(l, m, o, Direction.Axis.field_11048),
							axisCycleDirection2.choose(l, m, o, Direction.Axis.field_11052),
							axisCycleDirection2.choose(l, m, o, Direction.Axis.field_11051)
						);
						n = -1;
					}
				}
			}
		}
	}

	protected boolean isColumnFull(int i, int j, int k, int l) {
		for (int m = i; m < j; m++) {
			if (!this.inBoundsAndContains(k, l, m)) {
				return false;
			}
		}

		return true;
	}

	protected void setColumn(int i, int j, int k, int l, boolean bl) {
		for (int m = i; m < j; m++) {
			this.set(k, l, m, false, bl);
		}
	}

	protected boolean isRectangleFull(int i, int j, int k, int l, int m) {
		for (int n = i; n < j; n++) {
			if (!this.isColumnFull(k, l, n, m)) {
				return false;
			}
		}

		return true;
	}

	public void forEachBox(VoxelSet.class_253 arg, boolean bl) {
		VoxelSet voxelSet = new BitSetVoxelSet(this);

		for (int i = 0; i <= this.xSize; i++) {
			for (int j = 0; j <= this.ySize; j++) {
				int k = -1;

				for (int l = 0; l <= this.zSize; l++) {
					if (voxelSet.inBoundsAndContains(i, j, l)) {
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
						voxelSet.setColumn(k, l, i, j, false);

						while (voxelSet.isColumnFull(k, l, m - 1, o)) {
							voxelSet.setColumn(k, l, m - 1, o, false);
							m--;
						}

						while (voxelSet.isColumnFull(k, l, n + 1, o)) {
							voxelSet.setColumn(k, l, n + 1, o, false);
							n++;
						}

						while (voxelSet.isRectangleFull(m, n + 1, k, l, o - 1)) {
							for (int q = m; q <= n; q++) {
								voxelSet.setColumn(k, l, q, o - 1, false);
							}

							o--;
						}

						while (voxelSet.isRectangleFull(m, n + 1, k, l, p + 1)) {
							for (int q = m; q <= n; q++) {
								voxelSet.setColumn(k, l, q, p + 1, false);
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

	public void method_1046(VoxelSet.class_252 arg) {
		this.method_1061(arg, AxisCycleDirection.field_10962);
		this.method_1061(arg, AxisCycleDirection.field_10963);
		this.method_1061(arg, AxisCycleDirection.field_10965);
	}

	private void method_1061(VoxelSet.class_252 arg, AxisCycleDirection axisCycleDirection) {
		AxisCycleDirection axisCycleDirection2 = axisCycleDirection.opposite();
		Direction.Axis axis = axisCycleDirection2.cycle(Direction.Axis.field_11051);
		int i = this.getSize(axisCycleDirection2.cycle(Direction.Axis.field_11048));
		int j = this.getSize(axisCycleDirection2.cycle(Direction.Axis.field_11052));
		int k = this.getSize(axis);
		Direction direction = Direction.from(axis, Direction.AxisDirection.field_11060);
		Direction direction2 = Direction.from(axis, Direction.AxisDirection.field_11056);

		for (int l = 0; l < i; l++) {
			for (int m = 0; m < j; m++) {
				boolean bl = false;

				for (int n = 0; n <= k; n++) {
					boolean bl2 = n != k && this.contains(axisCycleDirection2, l, m, n);
					if (!bl && bl2) {
						arg.consume(
							direction,
							axisCycleDirection2.choose(l, m, n, Direction.Axis.field_11048),
							axisCycleDirection2.choose(l, m, n, Direction.Axis.field_11052),
							axisCycleDirection2.choose(l, m, n, Direction.Axis.field_11051)
						);
					}

					if (bl && !bl2) {
						arg.consume(
							direction2,
							axisCycleDirection2.choose(l, m, n - 1, Direction.Axis.field_11048),
							axisCycleDirection2.choose(l, m, n - 1, Direction.Axis.field_11052),
							axisCycleDirection2.choose(l, m, n - 1, Direction.Axis.field_11051)
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
