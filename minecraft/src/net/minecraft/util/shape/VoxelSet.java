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

	protected VoxelSet(int xSize, int ySize, int zSize) {
		if (xSize >= 0 && ySize >= 0 && zSize >= 0) {
			this.xSize = xSize;
			this.ySize = ySize;
			this.zSize = zSize;
		} else {
			throw new IllegalArgumentException("Need all positive sizes: x: " + xSize + ", y: " + ySize + ", z: " + zSize);
		}
	}

	public boolean inBoundsAndContains(AxisCycleDirection cycle, int x, int y, int z) {
		return this.inBoundsAndContains(cycle.choose(x, y, z, Direction.Axis.X), cycle.choose(x, y, z, Direction.Axis.Y), cycle.choose(x, y, z, Direction.Axis.Z));
	}

	public boolean inBoundsAndContains(int x, int y, int z) {
		if (x < 0 || y < 0 || z < 0) {
			return false;
		} else {
			return x < this.xSize && y < this.ySize && z < this.zSize ? this.contains(x, y, z) : false;
		}
	}

	public boolean contains(AxisCycleDirection cycle, int x, int y, int z) {
		return this.contains(cycle.choose(x, y, z, Direction.Axis.X), cycle.choose(x, y, z, Direction.Axis.Y), cycle.choose(x, y, z, Direction.Axis.Z));
	}

	public abstract boolean contains(int x, int y, int z);

	public abstract void set(int x, int y, int z);

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
	public int getEndingAxisCoord(Direction.Axis axis, int from, int to) {
		if (from >= 0 && to >= 0) {
			Direction.Axis axis2 = AxisCycleDirection.FORWARD.cycle(axis);
			Direction.Axis axis3 = AxisCycleDirection.BACKWARD.cycle(axis);
			if (from < this.getSize(axis2) && to < this.getSize(axis3)) {
				int i = this.getSize(axis);
				AxisCycleDirection axisCycleDirection = AxisCycleDirection.between(Direction.Axis.X, axis);

				for (int j = i - 1; j >= 0; j--) {
					if (this.contains(axisCycleDirection, j, from, to)) {
						return j + 1;
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
	public void forEachEdge(VoxelSet.PositionBiConsumer positionBiConsumer, boolean bl) {
		this.forEachEdge(positionBiConsumer, AxisCycleDirection.NONE, bl);
		this.forEachEdge(positionBiConsumer, AxisCycleDirection.FORWARD, bl);
		this.forEachEdge(positionBiConsumer, AxisCycleDirection.BACKWARD, bl);
	}

	@Environment(EnvType.CLIENT)
	private void forEachEdge(VoxelSet.PositionBiConsumer positionBiConsumer, AxisCycleDirection direction, boolean bl) {
		AxisCycleDirection axisCycleDirection = direction.opposite();
		int i = this.getSize(axisCycleDirection.cycle(Direction.Axis.X));
		int j = this.getSize(axisCycleDirection.cycle(Direction.Axis.Y));
		int k = this.getSize(axisCycleDirection.cycle(Direction.Axis.Z));

		for (int l = 0; l <= i; l++) {
			for (int m = 0; m <= j; m++) {
				int n = -1;

				for (int o = 0; o <= k; o++) {
					int p = 0;
					int q = 0;

					for (int r = 0; r <= 1; r++) {
						for (int s = 0; s <= 1; s++) {
							if (this.inBoundsAndContains(axisCycleDirection, l + r - 1, m + s - 1, o)) {
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
							positionBiConsumer.consume(
								axisCycleDirection.choose(l, m, o, Direction.Axis.X),
								axisCycleDirection.choose(l, m, o, Direction.Axis.Y),
								axisCycleDirection.choose(l, m, o, Direction.Axis.Z),
								axisCycleDirection.choose(l, m, o + 1, Direction.Axis.X),
								axisCycleDirection.choose(l, m, o + 1, Direction.Axis.Y),
								axisCycleDirection.choose(l, m, o + 1, Direction.Axis.Z)
							);
						}
					} else if (n != -1) {
						positionBiConsumer.consume(
							axisCycleDirection.choose(l, m, n, Direction.Axis.X),
							axisCycleDirection.choose(l, m, n, Direction.Axis.Y),
							axisCycleDirection.choose(l, m, n, Direction.Axis.Z),
							axisCycleDirection.choose(l, m, o, Direction.Axis.X),
							axisCycleDirection.choose(l, m, o, Direction.Axis.Y),
							axisCycleDirection.choose(l, m, o, Direction.Axis.Z)
						);
						n = -1;
					}
				}
			}
		}
	}

	public void forEachBox(VoxelSet.PositionBiConsumer consumer, boolean largest) {
		BitSetVoxelSet.method_31941(this, consumer, largest);
	}

	public void forEachDirection(VoxelSet.PositionConsumer positionConsumer) {
		this.forEachDirection(positionConsumer, AxisCycleDirection.NONE);
		this.forEachDirection(positionConsumer, AxisCycleDirection.FORWARD);
		this.forEachDirection(positionConsumer, AxisCycleDirection.BACKWARD);
	}

	private void forEachDirection(VoxelSet.PositionConsumer positionConsumer, AxisCycleDirection direction) {
		AxisCycleDirection axisCycleDirection = direction.opposite();
		Direction.Axis axis = axisCycleDirection.cycle(Direction.Axis.Z);
		int i = this.getSize(axisCycleDirection.cycle(Direction.Axis.X));
		int j = this.getSize(axisCycleDirection.cycle(Direction.Axis.Y));
		int k = this.getSize(axis);
		Direction direction2 = Direction.from(axis, Direction.AxisDirection.NEGATIVE);
		Direction direction3 = Direction.from(axis, Direction.AxisDirection.POSITIVE);

		for (int l = 0; l < i; l++) {
			for (int m = 0; m < j; m++) {
				boolean bl = false;

				for (int n = 0; n <= k; n++) {
					boolean bl2 = n != k && this.contains(axisCycleDirection, l, m, n);
					if (!bl && bl2) {
						positionConsumer.consume(
							direction2,
							axisCycleDirection.choose(l, m, n, Direction.Axis.X),
							axisCycleDirection.choose(l, m, n, Direction.Axis.Y),
							axisCycleDirection.choose(l, m, n, Direction.Axis.Z)
						);
					}

					if (bl && !bl2) {
						positionConsumer.consume(
							direction3,
							axisCycleDirection.choose(l, m, n - 1, Direction.Axis.X),
							axisCycleDirection.choose(l, m, n - 1, Direction.Axis.Y),
							axisCycleDirection.choose(l, m, n - 1, Direction.Axis.Z)
						);
					}

					bl = bl2;
				}
			}
		}
	}

	public interface PositionBiConsumer {
		void consume(int x1, int y1, int z1, int x2, int y2, int z2);
	}

	public interface PositionConsumer {
		void consume(Direction direction, int x, int y, int z);
	}
}
