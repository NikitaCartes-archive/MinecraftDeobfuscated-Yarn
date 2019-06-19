package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BlockPlacementDispenserBehavior extends FallibleItemDispenserBehavior {
	@Override
	protected ItemStack dispenseSilently(BlockPointer blockPointer, ItemStack itemStack) {
		this.success = false;
		Item item = itemStack.getItem();
		if (item instanceof BlockItem) {
			Direction direction = blockPointer.getBlockState().get(DispenserBlock.FACING);
			BlockPos blockPos = blockPointer.getBlockPos().offset(direction);
			Direction direction2 = blockPointer.getWorld().isAir(blockPos.down()) ? direction : Direction.field_11036;
			this.success = ((BlockItem)item).place(new AutomaticItemPlacementContext(blockPointer.getWorld(), blockPos, direction, itemStack, direction2))
				== ActionResult.field_5812;
		}

		return itemStack;
	}
}
