package net.minecraft.vote;

import net.minecraft.class_8293;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BlockApproval {
	public static boolean isApproved(Block block) {
		if (block == Blocks.PICKAXE_BLOCK) {
			return class_8293.field_43550.method_50116();
		} else if (block == Blocks.PLACE_BLOCK) {
			return class_8293.field_43551.method_50116();
		} else if (block == Blocks.COPPER_SINK) {
			return class_8293.field_43664.method_50116();
		} else {
			return block == Blocks.PACKED_AIR ? class_8293.field_43539.method_50116() : true;
		}
	}

	public static boolean isApproved(Item item) {
		if (item instanceof BlockItem blockItem) {
			return isApproved(blockItem.getBlock());
		} else {
			return item == Items.AIR_BLOCK ? class_8293.field_43539.method_50116() : true;
		}
	}

	public static boolean isApproved(ItemStack stack) {
		return isApproved(stack.getItem());
	}
}
