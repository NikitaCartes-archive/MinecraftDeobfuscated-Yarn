package net.minecraft.util.shape;

import java.util.BitSet;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Direction;

public final class BitSetVoxelSet extends VoxelSet {
	private final BitSet storage;
	private int xMin;
	private int yMin;
	private int zMin;
	private int xMax;
	private int yMax;
	private int zMax;

	public BitSetVoxelSet(int i, int j, int k) {
		super(i, j, k);
		this.storage = new BitSet(i * j * k);
		this.xMin = i;
		this.yMin = j;
		this.zMin = k;
	}

	public static BitSetVoxelSet method_31939(int i, int j, int k, int l, int m, int n, int o, int p, int q) {
		BitSetVoxelSet bitSetVoxelSet = new BitSetVoxelSet(i, j, k);
		bitSetVoxelSet.xMin = l;
		bitSetVoxelSet.yMin = m;
		bitSetVoxelSet.zMin = n;
		bitSetVoxelSet.xMax = o;
		bitSetVoxelSet.yMax = p;
		bitSetVoxelSet.zMax = q;

		for (int r = l; r < o; r++) {
			for (int s = m; s < p; s++) {
				for (int t = n; t < q; t++) {
					bitSetVoxelSet.method_31940(r, s, t, false);
				}
			}
		}

		return bitSetVoxelSet;
	}

	public BitSetVoxelSet(VoxelSet other) {
		super(other.xSize, other.ySize, other.zSize);
		if (other instanceof BitSetVoxelSet) {
			this.storage = (BitSet)((BitSetVoxelSet)other).storage.clone();
		} else {
			this.storage = new BitSet(this.xSize * this.ySize * this.zSize);

			for (int i = 0; i < this.xSize; i++) {
				for (int j = 0; j < this.ySize; j++) {
					for (int k = 0; k < this.zSize; k++) {
						if (other.contains(i, j, k)) {
							this.storage.set(this.getIndex(i, j, k));
						}
					}
				}
			}
		}

		this.xMin = other.getMin(Direction.Axis.X);
		this.yMin = other.getMin(Direction.Axis.Y);
		this.zMin = other.getMin(Direction.Axis.Z);
		this.xMax = other.getMax(Direction.Axis.X);
		this.yMax = other.getMax(Direction.Axis.Y);
		this.zMax = other.getMax(Direction.Axis.Z);
	}

	protected int getIndex(int x, int y, int z) {
		return (x * this.ySize + y) * this.zSize + z;
	}

	@Override
	public boolean contains(int x, int y, int z) {
		return this.storage.get(this.getIndex(x, y, z));
	}

	private void method_31940(int i, int j, int k, boolean bl) {
		this.storage.set(this.getIndex(i, j, k));
		if (bl) {
			this.xMin = Math.min(this.xMin, i);
			this.yMin = Math.min(this.yMin, j);
			this.zMin = Math.min(this.zMin, k);
			this.xMax = Math.max(this.xMax, i + 1);
			this.yMax = Math.max(this.yMax, j + 1);
			this.zMax = Math.max(this.zMax, k + 1);
		}
	}

	@Override
	public void set(int x, int y, int z) {
		this.method_31940(x, y, z, true);
	}

	@Override
	public boolean isEmpty() {
		return this.storage.isEmpty();
	}

	@Override
	public int getMin(Direction.Axis axis) {
		return axis.choose(this.xMin, this.yMin, this.zMin);
	}

	@Override
	public int getMax(Direction.Axis axis) {
		return axis.choose(this.xMax, this.yMax, this.zMax);
	}

	static BitSetVoxelSet combine(VoxelSet first, VoxelSet second, PairList xPoints, PairList yPoints, PairList zPoints, BooleanBiFunction function) {
		BitSetVoxelSet bitSetVoxelSet = new BitSetVoxelSet(xPoints.size() - 1, yPoints.size() - 1, zPoints.size() - 1);
		int[] is = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
		xPoints.forEachPair((i, j, k) -> {
			boolean[] bls = new boolean[]{false};
			yPoints.forEachPair((l, m, n) -> {
				boolean[] bls2 = new boolean[]{false};
				zPoints.forEachPair((o, p, q) -> {
					if (function.apply(first.inBoundsAndContains(i, l, o), second.inBoundsAndContains(j, m, p))) {
						bitSetVoxelSet.storage.set(bitSetVoxelSet.getIndex(k, n, q));
						is[2] = Math.min(is[2], q);
						is[5] = Math.max(is[5], q);
						bls2[0] = true;
					}

					return true;
				});
				if (bls2[0]) {
					is[1] = Math.min(is[1], n);
					is[4] = Math.max(is[4], n);
					bls[0] = true;
				}

				return true;
			});
			if (bls[0]) {
				is[0] = Math.min(is[0], k);
				is[3] = Math.max(is[3], k);
			}

			return true;
		});
		bitSetVoxelSet.xMin = is[0];
		bitSetVoxelSet.yMin = is[1];
		bitSetVoxelSet.zMin = is[2];
		bitSetVoxelSet.xMax = is[3] + 1;
		bitSetVoxelSet.yMax = is[4] + 1;
		bitSetVoxelSet.zMax = is[5] + 1;
		return bitSetVoxelSet;
	}

	protected static void method_31941(VoxelSet voxelSet, VoxelSet.PositionBiConsumer positionBiConsumer, boolean bl) {
		BitSetVoxelSet bitSetVoxelSet = new BitSetVoxelSet(voxelSet);

		for (int i = 0; i < bitSetVoxelSet.xSize; i++) {
			for (int j = 0; j < bitSetVoxelSet.ySize; j++) {
				int k = -1;

				for (int l = 0; l <= bitSetVoxelSet.zSize; l++) {
					if (bitSetVoxelSet.inBoundsAndContains(i, j, l)) {
						if (bl) {
							if (k == -1) {
								k = l;
							}
						} else {
							positionBiConsumer.consume(i, j, l, i + 1, j + 1, l + 1);
						}
					} else if (k != -1) {
						int m = i;
						int n = j;
						bitSetVoxelSet.method_31942(k, l, i, j);

						while (bitSetVoxelSet.isColumnFull(k, l, m + 1, j)) {
							bitSetVoxelSet.method_31942(k, l, m + 1, j);
							m++;
						}

						while (bitSetVoxelSet.method_31938(i, m + 1, k, l, n + 1)) {
							for (int o = i; o <= m; o++) {
								bitSetVoxelSet.method_31942(k, l, o, n + 1);
							}

							n++;
						}

						positionBiConsumer.consume(i, j, k, m + 1, n + 1, l);
						k = -1;
					}
				}
			}
		}
	}

	private boolean isColumnFull(int i, int j, int k, int l) {
		return k < this.xSize && l < this.ySize ? this.storage.nextClearBit(this.getIndex(k, l, i)) >= this.getIndex(k, l, j) : false;
	}

	private boolean method_31938(int i, int j, int k, int l, int m) {
		for (int n = i; n < j; n++) {
			if (!this.isColumnFull(k, l, n, m)) {
				return false;
			}
		}

		return true;
	}

	private void method_31942(int i, int j, int k, int l) {
		this.storage.clear(this.getIndex(k, l, i), this.getIndex(k, l, j));
	}
}
