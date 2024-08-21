package net.minecraft.block;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.CollisionView;

public class EntityShapeContext implements ShapeContext {
	protected static final ShapeContext ABSENT = new EntityShapeContext(false, -Double.MAX_VALUE, ItemStack.EMPTY, fluidState -> false, null) {
		@Override
		public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
			return defaultValue;
		}
	};
	private final boolean descending;
	private final double minY;
	private final ItemStack heldItem;
	private final Predicate<FluidState> walkOnFluidPredicate;
	@Nullable
	private final Entity entity;

	protected EntityShapeContext(boolean descending, double minY, ItemStack heldItem, Predicate<FluidState> walkOnFluidPredicate, @Nullable Entity entity) {
		this.descending = descending;
		this.minY = minY;
		this.heldItem = heldItem;
		this.walkOnFluidPredicate = walkOnFluidPredicate;
		this.entity = entity;
	}

	@Deprecated
	protected EntityShapeContext(Entity entity, boolean collidesWithFluid) {
		this(
			entity.isDescending(),
			entity.getY(),
			entity instanceof LivingEntity ? ((LivingEntity)entity).getMainHandStack() : ItemStack.EMPTY,
			collidesWithFluid ? state -> true : (entity instanceof LivingEntity ? ((LivingEntity)entity)::canWalkOnFluid : state -> false),
			entity
		);
	}

	@Override
	public boolean isHolding(Item item) {
		return this.heldItem.isOf(item);
	}

	@Override
	public boolean canWalkOnFluid(FluidState stateAbove, FluidState state) {
		return this.walkOnFluidPredicate.test(state) && !stateAbove.getFluid().matchesType(state.getFluid());
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, CollisionView world, BlockPos pos) {
		return state.getCollisionShape(world, pos, this);
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
