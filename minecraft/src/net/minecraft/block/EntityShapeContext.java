package net.minecraft.block;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class EntityShapeContext implements ShapeContext {
	protected static final ShapeContext ABSENT = new EntityShapeContext(false, -Double.MAX_VALUE, ItemStack.EMPTY, fluid -> false, null) {
		@Override
		public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
			return defaultValue;
		}
	};
	private final boolean descending;
	private final double minY;
	private final ItemStack heldItem;
	private final Predicate<Fluid> walkOnFluidPredicate;
	@Nullable
	private final Entity entity;

	protected EntityShapeContext(boolean descending, double minY, ItemStack heldItem, Predicate<Fluid> walkOnFluidPredicate, @Nullable Entity entity) {
		this.descending = descending;
		this.minY = minY;
		this.heldItem = heldItem;
		this.walkOnFluidPredicate = walkOnFluidPredicate;
		this.entity = entity;
	}

	@Deprecated
	protected EntityShapeContext(Entity entity) {
		this(
			entity.isDescending(),
			entity.getY(),
			entity instanceof LivingEntity ? ((LivingEntity)entity).getMainHandStack() : ItemStack.EMPTY,
			entity instanceof LivingEntity ? ((LivingEntity)entity)::canWalkOnFluid : fluid -> false,
			entity
		);
	}

	@Override
	public boolean isHolding(Item item) {
		return this.heldItem.isOf(item);
	}

	@Override
	public boolean canWalkOnFluid(FluidState state, FlowableFluid fluid) {
		return this.walkOnFluidPredicate.test(fluid) && !state.getFluid().matchesType(fluid);
	}

	@Override
	public boolean isDescending() {
		return this.descending;
	}

	@Override
	public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
		return this.minY > (double)pos.getY() + shape.getMax(Direction.Axis.Y) - 1.0E-5F;
	}

	@Nullable
	public Entity getEntity() {
		return this.entity;
	}
}
