package net.minecraft;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.SequentialDoubleList;
import net.minecraft.util.shape.VoxelShape;

public final class class_264 extends VoxelShape {
	private final int xOffset;
	private final int yOffset;
	private final int zOffset;

	public class_264(class_251 arg, int i, int j, int k) {
		super(arg);
		this.xOffset = i;
		this.yOffset = j;
		this.zOffset = k;
	}

	@Override
	protected DoubleList method_1109(Direction.Axis axis) {
		return new SequentialDoubleList(this.shape.method_1051(axis), axis.choose(this.xOffset, this.yOffset, this.zOffset));
	}
}
