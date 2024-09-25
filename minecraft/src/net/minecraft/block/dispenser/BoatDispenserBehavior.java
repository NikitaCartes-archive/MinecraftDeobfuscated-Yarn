package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldEvents;

public class BoatDispenserBehavior extends ItemDispenserBehavior {
	private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();
	private final EntityType<? extends AbstractBoatEntity> boatType;

	public BoatDispenserBehavior(EntityType<? extends AbstractBoatEntity> boatType) {
		this.boatType = boatType;
	}

	@Override
	public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		Direction direction = pointer.state().get(DispenserBlock.FACING);
		ServerWorld serverWorld = pointer.world();
		Vec3d vec3d = pointer.centerPos();
		double d = 0.5625 + (double)this.boatType.getWidth() / 2.0;
		double e = vec3d.getX() + (double)direction.getOffsetX() * d;
		double f = vec3d.getY() + (double)((float)direction.getOffsetY() * 1.125F);
		double g = vec3d.getZ() + (double)direction.getOffsetZ() * d;
		BlockPos blockPos = pointer.pos().offset(direction);
		double h;
		if (serverWorld.getFluidState(blockPos).isIn(FluidTags.WATER)) {
			h = 1.0;
		} else {
			if (!serverWorld.getBlockState(blockPos).isAir() || !serverWorld.getFluidState(blockPos.down()).isIn(FluidTags.WATER)) {
				return this.fallbackBehavior.dispense(pointer, stack);
			}

			h = 0.0;
		}

		AbstractBoatEntity abstractBoatEntity = this.boatType.create(serverWorld, SpawnReason.DISPENSER);
		if (abstractBoatEntity != null) {
			abstractBoatEntity.initPosition(e, f + h, g);
			EntityType.copier(serverWorld, stack, null).accept(abstractBoatEntity);
			abstractBoatEntity.setYaw(direction.asRotation());
			serverWorld.spawnEntity(abstractBoatEntity);
			stack.decrement(1);
		}

		return stack;
	}

	@Override
	protected void playSound(BlockPointer pointer) {
		pointer.world().syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pointer.pos(), 0);
	}
}
