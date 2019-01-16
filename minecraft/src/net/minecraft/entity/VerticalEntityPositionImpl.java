package net.minecraft.entity;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class VerticalEntityPositionImpl implements VerticalEntityPosition {
	protected static final VerticalEntityPosition field_17593 = new VerticalEntityPositionImpl(false, -Double.MAX_VALUE, Items.AIR) {
		@Override
		public boolean isAboveBlock(VoxelShape voxelShape, BlockPos blockPos, boolean bl) {
			return bl;
		}
	};
	private final boolean sneaking;
	private final double posY;
	private final Item field_17594;

	protected VerticalEntityPositionImpl(boolean bl, double d, Item item) {
		this.sneaking = bl;
		this.posY = d;
		this.field_17594 = item;
	}

	@Deprecated
	protected VerticalEntityPositionImpl(Entity entity) {
		boolean var10001 = entity.isSneaking();
		Item var10003 = entity instanceof LivingEntity ? ((LivingEntity)entity).getMainHandStack().getItem() : Items.AIR;
		this(var10001, entity.getBoundingBox().minY, var10003);
	}

	@Override
	public boolean method_17785(Item item) {
		return this.field_17594 == item;
	}

	@Override
	public boolean isSneaking() {
		return this.sneaking;
	}

	@Override
	public boolean isAboveBlock(VoxelShape voxelShape, BlockPos blockPos, boolean bl) {
		return this.posY > (double)blockPos.getY() + voxelShape.getMaximum(Direction.Axis.Y) - 1.0E-5F;
	}
}
