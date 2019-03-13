package net.minecraft;

import net.minecraft.block.DispenserBlock;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.block.BlockItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class class_2970 extends class_2969 {
	@Override
	protected ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
		this.field_13364 = false;
		Item item = itemStack.getItem();
		if (item instanceof BlockItem) {
			Direction direction = blockPointer.getBlockState().method_11654(DispenserBlock.field_10918);
			BlockPos blockPos = blockPointer.getBlockPos().method_10093(direction);
			Direction direction2 = blockPointer.getWorld().method_8623(blockPos.down()) ? direction : Direction.UP;
			this.field_13364 = ((BlockItem)item).method_7712(new AutomaticItemPlacementContext(blockPointer.getWorld(), blockPos, direction, itemStack, direction2))
				== ActionResult.field_5812;
			if (this.field_13364) {
				itemStack.subtractAmount(1);
			}
		}

		return itemStack;
	}
}
