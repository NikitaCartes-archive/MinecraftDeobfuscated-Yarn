package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class BoatDispenserBehavior extends ItemDispenserBehavior {
	private final ItemDispenserBehavior itemDispenser = new ItemDispenserBehavior();
	private final BoatEntity.Type boatType;
	private final boolean chest;

	public BoatDispenserBehavior(BoatEntity.Type type) {
		this(type, false);
	}

	public BoatDispenserBehavior(BoatEntity.Type boatType, boolean chest) {
		this.boatType = boatType;
		this.chest = chest;
	}

	@Override
	public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		Direction direction = pointer.state().get(DispenserBlock.FACING);
		World world = pointer.world();
		Vec3d vec3d = pointer.centerPos();
		double d = 0.5625 + (double)EntityType.BOAT.getWidth() / 2.0;
		double e = vec3d.getX() + (double)direction.getOffsetX() * d;
		double f = vec3d.getY() + (double)((float)direction.getOffsetY() * 1.125F);
		double g = vec3d.getZ() + (double)direction.getOffsetZ() * d;
		BlockPos blockPos = pointer.pos().offset(direction);
		double h;
		if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
			h = 1.0;
		} else {
			if (!world.getBlockState(blockPos).isAir() || !world.getFluidState(blockPos.down()).isIn(FluidTags.WATER)) {
				return this.itemDispenser.dispense(pointer, stack);
			}

			h = 0.0;
		}

		BoatEntity boatEntity = (BoatEntity)(this.chest ? new ChestBoatEntity(world, e, f + h, g) : new BoatEntity(world, e, f + h, g));
		boatEntity.setVariant(this.boatType);
		boatEntity.setYaw(direction.asRotation());
		world.spawnEntity(boatEntity);
		stack.decrement(1);
		return stack;
	}

	@Override
	protected void playSound(BlockPointer pointer) {
		pointer.world().syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pointer.pos(), 0);
	}
}
