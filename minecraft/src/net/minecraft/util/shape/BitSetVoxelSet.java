package net.minecraft.util.shape;

import java.util.BitSet;
import net.minecraft.util.BooleanBiFunction;
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

	public BitSetVoxelSet(int i, int j, int k, int l, int m, int n, int o, int p, int q) {
		super(i, j, k);
		this.storage = new BitSet(i * j * k);
		this.xMin = l;
		this.yMin = m;
		this.zMin = n;
		this.xMax = o;
		this.yMax = p;
		this.zMax = q;
	}

	public BitSetVoxelSet(VoxelSet voxelSet) {
		super(voxelSet.xSize, voxelSet.ySize, voxelSet.zSize);
		if (voxelSet instanceof BitSetVoxelSet) {
			this.storage = (BitSet)((BitSetVoxelSet)voxelSet).storage.clone();
		} else {
			this.storage = new BitSet(this.xSize * this.ySize * this.zSize);

			for (int i = 0; i < this.xSize; i++) {
				for (int j = 0; j < this.ySize; j++) {
					for (int k = 0; k < this.zSize; k++) {
						if (voxelSet.contains(i, j, k)) {
							this.storage.set(this.getIndex(i, j, k));
						}
					}
				}
			}
		}

		this.xMin = voxelSet.getMin(Direction.Axis.field_11048);
		this.yMin = voxelSet.getMin(Direction.Axis.field_11052);
		this.zMin = voxelSet.getMin(Direction.Axis.field_11051);
		this.xMax = voxelSet.getMax(Direction.Axis.field_11048);
		this.yMax = voxelSet.getMax(Direction.Axis.field_11052);
		this.zMax = voxelSet.getMax(Direction.Axis.field_11051);
	}

	protected int getIndex(int i, int j, int k) {
		return (i * this.ySize + j) * this.zSize + k;
	}

	@Override
	public boolean contains(int i, int j, int k) {
		return this.storage.get(this.getIndex(i, j, k));
	}

	@Override
	public void set(int i, int j, int k, boolean bl, boolean bl2) {
		this.storage.set(this.getIndex(i, j, k), bl2);
		if (bl && bl2) {
			this.xMin = Math.min(this.xMin, i);
			this.yMin = Math.min(this.yMin, j);
			this.zMin = Math.min(this.zMin, k);
			this.xMax = Math.max(this.xMax, i + 1);
			this.yMax = Math.max(this.yMax, j + 1);
			this.zMax = Math.max(this.zMax, k + 1);
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
	protected boolean isColumnFull(int i, int j, int k, int l) {
		if (k < 0 || l < 0 || i < 0) {
			return false;
		} else {
			return k < this.xSize && l < this.ySize && j <= this.zSize ? this.storage.nextClearBit(this.getIndex(k, l, i)) >= this.getIndex(k, l, j) : false;
		}
	}

	@Override
	protected void setColumn(int i, int j, int k, int l, boolean bl) {
		this.storage.set(this.getIndex(k, l, i), this.getIndex(k, l, j), bl);
	}

	static BitSetVoxelSet combine(
		VoxelSet voxelSet,
		VoxelSet voxelSet2,
		DoubleListPair doubleListPair,
		DoubleListPair doubleListPair2,
		DoubleListPair doubleListPair3,
		BooleanBiFunction booleanBiFunction
	) {
		BitSetVoxelSet bitSetVoxelSet = new BitSetVoxelSet(
			doubleListPair.getMergedList().size() - 1, doubleListPair2.getMergedList().size() - 1, doubleListPair3.getMergedList().size() - 1
		);
		int[] is = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
		doubleListPair.forAllOverlappingSections((i, j, k) -> {
			boolean[] bls = new boolean[]{false};
			boolean bl = doubleListPair2.forAllOverlappingSections((l, m, n) -> {
				boolean[] bls2 = new boolean[]{false};
				boolean blx = doubleListPair3.forAllOverlappingSections((o, p, q) -> {
					boolean blxx = booleanBiFunction.apply(voxelSet.inBoundsAndContains(i, l, o), voxelSet2.inBoundsAndContains(j, m, p));
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
