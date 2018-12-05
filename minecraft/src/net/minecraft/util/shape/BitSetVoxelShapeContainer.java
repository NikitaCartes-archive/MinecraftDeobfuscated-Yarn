package net.minecraft.util.shape;

import java.util.BitSet;
import net.minecraft.class_251;
import net.minecraft.class_255;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.Direction;

public final class BitSetVoxelShapeContainer extends class_251 {
	private final BitSet storage;
	private int xMin;
	private int yMin;
	private int zMin;
	private int xMax;
	private int yMax;
	private int zMax;

	public BitSetVoxelShapeContainer(int i, int j, int k) {
		this(i, j, k, i, j, k, 0, 0, 0);
	}

	public BitSetVoxelShapeContainer(int i, int j, int k, int l, int m, int n, int o, int p, int q) {
		super(i, j, k);
		this.storage = new BitSet(i * j * k);
		this.xMin = l;
		this.yMin = m;
		this.zMin = n;
		this.xMax = o;
		this.yMax = p;
		this.zMax = q;
	}

	public BitSetVoxelShapeContainer(class_251 arg) {
		super(arg.field_1374, arg.field_1373, arg.field_1372);
		if (arg instanceof BitSetVoxelShapeContainer) {
			this.storage = (BitSet)((BitSetVoxelShapeContainer)arg).storage.clone();
		} else {
			this.storage = new BitSet(this.field_1374 * this.field_1373 * this.field_1372);

			for (int i = 0; i < this.field_1374; i++) {
				for (int j = 0; j < this.field_1373; j++) {
					for (int k = 0; k < this.field_1372; k++) {
						if (arg.method_1063(i, j, k)) {
							this.storage.set(this.getIndex(i, j, k));
						}
					}
				}
			}
		}

		this.xMin = arg.method_1055(Direction.Axis.X);
		this.yMin = arg.method_1055(Direction.Axis.Y);
		this.zMin = arg.method_1055(Direction.Axis.Z);
		this.xMax = arg.method_1045(Direction.Axis.X);
		this.yMax = arg.method_1045(Direction.Axis.Y);
		this.zMax = arg.method_1045(Direction.Axis.Z);
	}

	protected int getIndex(int i, int j, int k) {
		return (i * this.field_1373 + j) * this.field_1372 + k;
	}

	@Override
	public boolean method_1063(int i, int j, int k) {
		return this.storage.get(this.getIndex(i, j, k));
	}

	@Override
	public void method_1049(int i, int j, int k, boolean bl, boolean bl2) {
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
	public boolean method_1056() {
		return this.storage.isEmpty();
	}

	@Override
	public int method_1055(Direction.Axis axis) {
		return axis.choose(this.xMin, this.yMin, this.zMin);
	}

	@Override
	public int method_1045(Direction.Axis axis) {
		return axis.choose(this.xMax, this.yMax, this.zMax);
	}

	@Override
	protected boolean method_1059(int i, int j, int k, int l) {
		if (k < 0 || l < 0 || i < 0) {
			return false;
		} else {
			return k < this.field_1374 && l < this.field_1373 && j <= this.field_1372
				? this.storage.nextClearBit(this.getIndex(k, l, i)) >= this.getIndex(k, l, j)
				: false;
		}
	}

	@Override
	protected void method_1060(int i, int j, int k, int l, boolean bl) {
		this.storage.set(this.getIndex(k, l, i), this.getIndex(k, l, j), bl);
	}

	static BitSetVoxelShapeContainer method_1040(
		class_251 arg, class_251 arg2, class_255 arg3, class_255 arg4, class_255 arg5, BooleanBiFunction booleanBiFunction
	) {
		BitSetVoxelShapeContainer bitSetVoxelShapeContainer = new BitSetVoxelShapeContainer(
			arg3.method_1066().size() - 1, arg4.method_1066().size() - 1, arg5.method_1066().size() - 1
		);
		int[] is = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
		arg3.method_1065((i, j, k) -> {
			boolean[] bls = new boolean[]{false};
			boolean bl = arg4.method_1065((l, m, n) -> {
				boolean[] bls2 = new boolean[]{false};
				boolean blx = arg5.method_1065((o, p, q) -> {
					boolean blxx = booleanBiFunction.apply(arg.method_1044(i, l, o), arg2.method_1044(j, m, p));
					if (blxx) {
						bitSetVoxelShapeContainer.storage.set(bitSetVoxelShapeContainer.getIndex(k, n, q));
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
		bitSetVoxelShapeContainer.xMin = is[0];
		bitSetVoxelShapeContainer.yMin = is[1];
		bitSetVoxelShapeContainer.zMin = is[2];
		bitSetVoxelShapeContainer.xMax = is[3] + 1;
		bitSetVoxelShapeContainer.yMax = is[4] + 1;
		bitSetVoxelShapeContainer.zMax = is[5] + 1;
		return bitSetVoxelShapeContainer;
	}
}
