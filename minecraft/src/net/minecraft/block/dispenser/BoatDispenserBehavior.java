package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
		Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
		World world = pointer.getWorld();
		double d = pointer.getX() + (double)((float)direction.getOffsetX() * 1.125F);
		double e = pointer.getY() + (double)((float)direction.getOffsetY() * 1.125F);
		double f = pointer.getZ() + (double)((float)direction.getOffsetZ() * 1.125F);
		BlockPos blockPos = pointer.getPos().offset(direction);
		double g;
		if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
			g = 1.0;
		} else {
			if (!world.getBlockState(blockPos).isAir() || !world.getFluidState(blockPos.down()).isIn(FluidTags.WATER)) {
				return this.itemDispenser.dispense(pointer, stack);
			}

			g = 0.0;
		}

		BoatEntity boatEntity = (BoatEntity)(this.chest ? new ChestBoatEntity(world, d, e + g, f) : new BoatEntity(world, d, e + g, f));
		boatEntity.setVariant(this.boatType);
		boatEntity.setYaw(direction.asRotation());
		world.spawnEntity(boatEntity);
		stack.decrement(1);
		return stack;
	}

	@Override
	protected void playSound(BlockPointer pointer) {
		pointer.getWorld().syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pointer.getPos(), 0);
	}
}
