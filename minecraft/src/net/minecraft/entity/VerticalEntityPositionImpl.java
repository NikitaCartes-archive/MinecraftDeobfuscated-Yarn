package net.minecraft.entity;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class VerticalEntityPositionImpl implements VerticalEntityPosition {
	private final boolean sneaking;
	private final double posY;

	protected VerticalEntityPositionImpl(boolean bl, double d) {
		this.sneaking = bl;
		this.posY = d;
	}

	public VerticalEntityPositionImpl(Entity entity) {
		this(entity.isSneaking(), entity.getBoundingBox().minY);
	}

	@Override
	public boolean isSneaking() {
		return this.sneaking;
	}

	@Override
	public boolean isAboveBlock(VoxelShape voxelShape, BlockPos blockPos) {
		return this.posY > (double)blockPos.getY() + voxelShape.method_1105(Direction.Axis.Y) - 1.0E-5F;
	}
}
