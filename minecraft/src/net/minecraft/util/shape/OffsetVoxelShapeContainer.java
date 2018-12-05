package net.minecraft.util.shape;

import net.minecraft.class_251;
import net.minecraft.util.math.Direction;

public final class OffsetVoxelShapeContainer extends class_251 {
	private final class_251 parent;
	private final int xMin;
	private final int yMin;
	private final int zMin;
	private final int xMax;
	private final int yMax;
	private final int zMax;

	public OffsetVoxelShapeContainer(class_251 arg, int i, int j, int k, int l, int m, int n) {
		super(l - i, m - j, n - k);
		this.parent = arg;
		this.xMin = i;
		this.yMin = j;
		this.zMin = k;
		this.xMax = l;
		this.yMax = m;
		this.zMax = n;
	}

	@Override
	public boolean method_1063(int i, int j, int k) {
		return this.parent.method_1063(this.xMin + i, this.yMin + j, this.zMin + k);
	}

	@Override
	public void method_1049(int i, int j, int k, boolean bl, boolean bl2) {
		this.parent.method_1049(this.xMin + i, this.yMin + j, this.zMin + k, bl, bl2);
	}

	@Override
	public int method_1055(Direction.Axis axis) {
		return Math.max(0, this.parent.method_1055(axis) - axis.choose(this.xMin, this.yMin, this.zMin));
	}

	@Override
	public int method_1045(Direction.Axis axis) {
		return Math.min(axis.choose(this.xMax, this.yMax, this.zMax), this.parent.method_1045(axis) - axis.choose(this.xMin, this.yMin, this.zMin));
	}
}
