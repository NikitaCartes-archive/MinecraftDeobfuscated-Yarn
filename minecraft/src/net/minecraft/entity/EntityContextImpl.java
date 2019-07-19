package net.minecraft.entity;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class EntityContextImpl implements EntityContext {
	protected static final EntityContext ABSENT = new EntityContextImpl(false, -Double.MAX_VALUE, Items.AIR) {
		@Override
		public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
			return defaultValue;
		}
	};
	private final boolean descending;
	private final double minY;
	private final Item heldItem;

	protected EntityContextImpl(boolean descending, double minY, Item heldItem) {
		this.descending = descending;
		this.minY = minY;
		this.heldItem = heldItem;
	}

	@Deprecated
	protected EntityContextImpl(Entity entity) {
		boolean var10001 = entity.isSneaking();
		Item var10003 = entity instanceof LivingEntity ? ((LivingEntity)entity).getMainHandStack().getItem() : Items.AIR;
		this(var10001, entity.getBoundingBox().y1, var10003);
	}

	@Override
	public boolean isHolding(Item item) {
		return this.heldItem == item;
	}

	@Override
	public boolean isDescending() {
		return this.descending;
	}

	@Override
	public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
		return this.minY > (double)pos.getY() + shape.getMaximum(Direction.Axis.Y) - 1.0E-5F;
	}
}
