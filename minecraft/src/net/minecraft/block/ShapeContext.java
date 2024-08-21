package net.minecraft.block;

import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.CollisionView;

public interface ShapeContext {
	static ShapeContext absent() {
		return EntityShapeContext.ABSENT;
	}

	static ShapeContext of(Entity entity) {
		Objects.requireNonNull(entity);

		return (ShapeContext)(switch (entity) {
			case AbstractMinecartEntity abstractMinecartEntity -> AbstractMinecartEntity.areMinecartImprovementsEnabled(abstractMinecartEntity.getWorld())
			? new ExperimentalMinecartShapeContext(abstractMinecartEntity, false)
			: new EntityShapeContext(entity, false);
			default -> new EntityShapeContext(entity, false);
		});
	}

	static ShapeContext of(Entity entity, boolean collidesWithFluid) {
		return new EntityShapeContext(entity, collidesWithFluid);
	}

	boolean isDescending();

	boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue);

	boolean isHolding(Item item);

	boolean canWalkOnFluid(FluidState stateAbove, FluidState state);

	VoxelShape getCollisionShape(BlockState state, CollisionView world, BlockPos pos);
}
