package net.minecraft.block;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class EntityShapeContext implements ShapeContext {
	protected static final ShapeContext ABSENT = new EntityShapeContext(false, -Double.MAX_VALUE, Items.AIR, fluid -> false) {
		@Override
		public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
			return defaultValue;
		}
	};
	private final boolean descending;
	private final double minY;
	private final Item heldItem;
	private final Predicate<Fluid> field_24425;

	protected EntityShapeContext(boolean descending, double minY, Item heldItem, Predicate<Fluid> predicate) {
		this.descending = descending;
		this.minY = minY;
		this.heldItem = heldItem;
		this.field_24425 = predicate;
	}

	@Deprecated
	protected EntityShapeContext(Entity entity) {
		this(
			entity.isDescending(),
			entity.getY(),
			entity instanceof LivingEntity ? ((LivingEntity)entity).getMainHandStack().getItem() : Items.AIR,
			entity instanceof LivingEntity ? ((LivingEntity)entity)::canWalkOnFluid : fluid -> false
		);
	}

	@Override
	public boolean isHolding(Item item) {
		return this.heldItem == item;
	}

	@Override
	public boolean method_27866(FluidState state, FlowableFluid fluid) {
		return this.field_24425.test(fluid) && !state.getFluid().matchesType(fluid);
	}

	@Override
	public boolean isDescending() {
		return this.descending;
	}

	@Override
	public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
		return this.minY > (double)pos.getY() + shape.getMax(Direction.Axis.Y) - 1.0E-5F;
	}
}
