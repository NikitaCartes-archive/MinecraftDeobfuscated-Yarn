package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class EntityShapeContext implements ShapeContext {
	protected static final ShapeContext ABSENT = new EntityShapeContext(false, -Double.MAX_VALUE, Items.AIR) {
		@Override
		public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
			return defaultValue;
		}
	};
	private final boolean descending;
	private final double minY;
	private final Item heldItem;

	protected EntityShapeContext(boolean descending, double minY, Item heldItem) {
		this.descending = descending;
		this.minY = minY;
		this.heldItem = heldItem;
	}

	@Deprecated
	protected EntityShapeContext(Entity entity) {
		this(entity.isDescending(), entity.getY(), entity instanceof LivingEntity ? ((LivingEntity)entity).getMainHandStack().getItem() : Items.AIR);
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