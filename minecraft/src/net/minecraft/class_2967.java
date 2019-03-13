package net.minecraft;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class class_2967 extends ItemDispenserBehavior {
	private final ItemDispenserBehavior field_13360 = new ItemDispenserBehavior();
	private final BoatEntity.Type field_13361;

	public class_2967(BoatEntity.Type type) {
		this.field_13361 = type;
	}

	@Override
	public ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
		Direction direction = blockPointer.getBlockState().method_11654(DispenserBlock.field_10918);
		World world = blockPointer.getWorld();
		double d = blockPointer.getX() + (double)((float)direction.getOffsetX() * 1.125F);
		double e = blockPointer.getY() + (double)((float)direction.getOffsetY() * 1.125F);
		double f = blockPointer.getZ() + (double)((float)direction.getOffsetZ() * 1.125F);
		BlockPos blockPos = blockPointer.getBlockPos().method_10093(direction);
		double g;
		if (world.method_8316(blockPos).method_15767(FluidTags.field_15517)) {
			g = 1.0;
		} else {
			if (!world.method_8320(blockPos).isAir() || !world.method_8316(blockPos.down()).method_15767(FluidTags.field_15517)) {
				return this.field_13360.dispense(blockPointer, itemStack);
			}

			g = 0.0;
		}

		BoatEntity boatEntity = new BoatEntity(world, d, e + g, f);
		boatEntity.method_7541(this.field_13361);
		boatEntity.yaw = direction.asRotation();
		world.spawnEntity(boatEntity);
		itemStack.subtractAmount(1);
		return itemStack;
	}

	@Override
	protected void playSound(BlockPointer blockPointer) {
		blockPointer.getWorld().method_8535(1000, blockPointer.getBlockPos(), 0);
	}
}
