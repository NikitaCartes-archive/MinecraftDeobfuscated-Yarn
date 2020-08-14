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
		this(i, j, k, i, j, k, 0, 0, 0);
	}

	public BitSetVoxelSet(int xMask, int yMask, int zMask, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
		super(xMask, yMask, zMask);
		this.storage = new BitSet(xMask * yMask * zMask);
		this.xMin = xMin;
		this.yMin = yMin;
		this.zMin = zMin;
		this.xMax = xMax;
		this.yMax = yMax;
		this.zMax = zMax;
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

	@Override
	public void set(int x, int y, int z, boolean resize, boolean included) {
		this.storage.set(this.getIndex(x, y, z), included);
		if (resize && included) {
			this.xMin = Math.min(this.xMin, x);
			this.yMin = Math.min(this.yMin, y);
			this.zMin = Math.min(this.zMin, z);
			this.xMax = Math.max(this.xMax, x + 1);
			this.yMax = Math.max(this.yMax, y + 1);
			this.zMax = Math.max(this.zMax, z + 1);
		}
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

	@Override
	protected boolean isColumnFull(int minZ, int maxZ, int x, int y) {
		if (x < 0 || y < 0 || minZ < 0) {
			return false;
		} else {
			return x < this.xSize && y < this.ySize && maxZ <= this.zSize ? this.storage.nextClearBit(this.getIndex(x, y, minZ)) >= this.getIndex(x, y, maxZ) : false;
		}
	}

	@Override
	protected void setColumn(int minZ, int maxZ, int x, int y, boolean included) {
		this.storage.set(this.getIndex(x, y, minZ), this.getIndex(x, y, maxZ), included);
	}

	static BitSetVoxelSet combine(VoxelSet first, VoxelSet second, PairList xPoints, PairList yPoints, PairList zPoints, BooleanBiFunction function) {
		BitSetVoxelSet bitSetVoxelSet = new BitSetVoxelSet(xPoints.getPairs().size() - 1, yPoints.getPairs().size() - 1, zPoints.getPairs().size() - 1);
		int[] is = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
		xPoints.forEachPair((i, j, k) -> {
			boolean[] bls = new boolean[]{false};
			boolean bl = yPoints.forEachPair((l, m, n) -> {
				boolean[] bls2 = new boolean[]{false};
				boolean blx = zPoints.forEachPair((o, p, q) -> {
					boolean blxx = function.apply(first.inBoundsAndContains(i, l, o), second.inBoundsAndContains(j, m, p));
					if (blxx) {
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

				return blx;
			});
			if (bls[0]) {
				is[0] = Math.min(is[0], k);
				is[3] = Math.max(is[3], k);
			}

			return bl;
		});
		bitSetVoxelSet.xMin = is[0];
		bitSetVoxelSet.yMin = is[1];
		bitSetVoxelSet.zMin = is[2];
		bitSetVoxelSet.xMax = is[3] + 1;
		bitSetVoxelSet.yMax = is[4] + 1;
		bitSetVoxelSet.zMax = is[5] + 1;
		return bitSetVoxelSet;
	}
}
