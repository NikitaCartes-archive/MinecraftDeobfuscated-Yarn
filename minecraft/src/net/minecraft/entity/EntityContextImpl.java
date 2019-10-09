package net.minecraft.entity;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class EntityContextImpl implements EntityContext {
	protected static final EntityContext ABSENT = new EntityContextImpl(false, -Double.MAX_VALUE, Items.AIR) {
		@Override
		public boolean isAbove(VoxelShape voxelShape, BlockPos blockPos, boolean bl) {
			return bl;
		}
	};
	private final boolean sneaking;
	private final double minY;
	private final Item heldItem;

	protected EntityContextImpl(boolean bl, double d, Item item) {
		this.sneaking = bl;
		this.minY = d;
		this.heldItem = item;
	}

	@Deprecated
	protected EntityContextImpl(Entity entity) {
		this(entity.method_21752(), entity.getY(), entity instanceof LivingEntity ? ((LivingEntity)entity).getMainHandStack().getItem() : Items.AIR);
	}

	@Override
	public boolean isHolding(Item item) {
		return this.heldItem == item;
	}

	@Override
	public boolean isSneaking() {
		return this.sneaking;
	}

	@Override
	public boolean isAbove(VoxelShape voxelShape, BlockPos blockPos, boolean bl) {
		return this.minY > (double)blockPos.getY() + voxelShape.getMaximum(Direction.Axis.Y) - 1.0E-5F;
	}
}
