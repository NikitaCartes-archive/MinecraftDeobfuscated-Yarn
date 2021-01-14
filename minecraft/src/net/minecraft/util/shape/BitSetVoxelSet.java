package net.minecraft.util.shape;

import java.util.BitSet;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Direction;

public final class BitSetVoxelSet extends VoxelSet {
	private final BitSet storage;
	private int minX;
	private int minY;
	private int minZ;
	private int maxX;
	private int maxY;
	private int maxZ;

	public BitSetVoxelSet(int i, int j, int k) {
		this(i, j, k, i, j, k, 0, 0, 0);
	}

	public BitSetVoxelSet(int xMask, int yMask, int zMask, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
		super(xMask, yMask, zMask);
		this.storage = new BitSet(xMask * yMask * zMask);
		this.minX = xMin;
		this.minY = yMin;
		this.minZ = zMin;
		this.maxX = xMax;
		this.maxY = yMax;
		this.maxZ = zMax;
	}

	public BitSetVoxelSet(VoxelSet other) {
		super(other.sizeX, other.sizeY, other.sizeZ);
		if (other instanceof BitSetVoxelSet) {
			this.storage = (BitSet)((BitSetVoxelSet)other).storage.clone();
		} else {
			this.storage = new BitSet(this.sizeX * this.sizeY * this.sizeZ);

			for (int i = 0; i < this.sizeX; i++) {
				for (int j = 0; j < this.sizeY; j++) {
					for (int k = 0; k < this.sizeZ; k++) {
						if (other.contains(i, j, k)) {
							this.storage.set(this.getIndex(i, j, k));
						}
					}
				}
			}
		}

		this.minX = other.getMin(Direction.Axis.X);
		this.minY = other.getMin(Direction.Axis.Y);
		this.minZ = other.getMin(Direction.Axis.Z);
		this.maxX = other.getMax(Direction.Axis.X);
		this.maxY = other.getMax(Direction.Axis.Y);
		this.maxZ = other.getMax(Direction.Axis.Z);
	}

	protected int getIndex(int x, int y, int z) {
		return (x * this.sizeY + y) * this.sizeZ + z;
	}

	@Override
	public boolean contains(int x, int y, int z) {
		return this.storage.get(this.getIndex(x, y, z));
	}

	@Override
	public void set(int x, int y, int z, boolean resize, boolean included) {
		this.storage.set(this.getIndex(x, y, z), included);
		if (resize && included) {
			this.minX = Math.min(this.minX, x);
			this.minY = Math.min(this.minY, y);
			this.minZ = Math.min(this.minZ, z);
			this.maxX = Math.max(this.maxX, x + 1);
			this.maxY = Math.max(this.maxY, y + 1);
			this.maxZ = Math.max(this.maxZ, z + 1);
		}
	}

	@Override
	public boolean isEmpty() {
		return this.storage.isEmpty();
	}

	@Override
	public int getMin(Direction.Axis axis) {
		return axis.choose(this.minX, this.minY, this.minZ);
	}

	@Override
	public int getMax(Direction.Axis axis) {
		return axis.choose(this.maxX, this.maxY, this.maxZ);
	}

	@Override
	protected boolean isColumnFull(int minZ, int maxZ, int x, int y) {
		if (x < 0 || y < 0 || minZ < 0) {
			return false;
		} else {
			return x < this.sizeX && y < this.sizeY && maxZ <= this.sizeZ ? this.storage.nextClearBit(this.getIndex(x, y, minZ)) >= this.getIndex(x, y, maxZ) : false;
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
		bitSetVoxelSet.minX = is[0];
		bitSetVoxelSet.minY = is[1];
		bitSetVoxelSet.minZ = is[2];
		bitSetVoxelSet.maxX = is[3] + 1;
		bitSetVoxelSet.maxY = is[4] + 1;
		bitSetVoxelSet.maxZ = is[5] + 1;
		return bitSetVoxelSet;
	}
}
