package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BoatDispenserBehavior extends ItemDispenserBehavior {
	private final ItemDispenserBehavior itemDispenser = new ItemDispenserBehavior();
	private final BoatEntity.Type boatType;

	public BoatDispenserBehavior(BoatEntity.Type type) {
		this.boatType = type;
	}

	@Override
	public ItemStack dispenseSilently(BlockPointer blockPointer, ItemStack itemStack) {
		Direction direction = blockPointer.getBlockState().method_11654(DispenserBlock.field_10918);
		World world = blockPointer.getWorld();
		double d = blockPointer.getX() + (double)((float)direction.getOffsetX() * 1.125F);
		double e = blockPointer.getY() + (double)((float)direction.getOffsetY() * 1.125F);
		double f = blockPointer.getZ() + (double)((float)direction.getOffsetZ() * 1.125F);
		BlockPos blockPos = blockPointer.getBlockPos().offset(direction);
		double g;
		if (world.method_8316(blockPos).matches(FluidTags.field_15517)) {
			g = 1.0;
		} else {
			if (!world.method_8320(blockPos).isAir() || !world.method_8316(blockPos.down()).matches(FluidTags.field_15517)) {
				return this.itemDispenser.dispense(blockPointer, itemStack);
			}

			g = 0.0;
		}

		BoatEntity boatEntity = new BoatEntity(world, d, e + g, f);
		boatEntity.setBoatType(this.boatType);
		boatEntity.yaw = direction.asRotation();
		world.spawnEntity(boatEntity);
		itemStack.decrement(1);
		return itemStack;
	}

	@Override
	protected void playSound(BlockPointer blockPointer) {
		blockPointer.getWorld().playLevelEvent(1000, blockPointer.getBlockPos(), 0);
	}
}
