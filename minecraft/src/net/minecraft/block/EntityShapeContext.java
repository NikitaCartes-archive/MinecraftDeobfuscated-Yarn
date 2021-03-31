package net.minecraft.block;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
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
	protected static final ShapeContext ABSENT = new EntityShapeContext(
		false, -Double.MAX_VALUE, ItemStack.EMPTY, ItemStack.EMPTY, fluid -> false, Optional.empty()
	) {
		@Override
		public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
			return defaultValue;
		}
	};
	private final boolean descending;
	private final double minY;
	private final ItemStack heldItem;
	private final ItemStack boots;
	private final Predicate<Fluid> walkOnFluidPredicate;
	private final Optional<Entity> entity;

	protected EntityShapeContext(
		boolean descending, double minY, ItemStack boots, ItemStack heldItem, Predicate<Fluid> walkOnFluidPredicate, Optional<Entity> entity
	) {
		this.descending = descending;
		this.minY = minY;
		this.boots = boots;
		this.heldItem = heldItem;
		this.walkOnFluidPredicate = walkOnFluidPredicate;
		this.entity = entity;
	}

	@Deprecated
	protected EntityShapeContext(Entity entity) {
		this(
			entity.isDescending(),
			entity.getY(),
			entity instanceof LivingEntity ? ((LivingEntity)entity).getEquippedStack(EquipmentSlot.FEET) : ItemStack.EMPTY,
			entity instanceof LivingEntity ? ((LivingEntity)entity).getMainHandStack() : ItemStack.EMPTY,
			entity instanceof LivingEntity ? ((LivingEntity)entity)::canWalkOnFluid : fluid -> false,
			Optional.of(entity)
		);
	}

	@Override
	public boolean isWearingOnFeet(Item item) {
		return this.boots.isOf(item);
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

	public Optional<Entity> getEntity() {
		return this.entity;
	}
}
