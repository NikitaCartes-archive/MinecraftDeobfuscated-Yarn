package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class BedItem extends BlockItem {
	public BedItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Override
	protected boolean method_7708(ItemPlacementContext itemPlacementContext, BlockState blockState) {
		return itemPlacementContext.method_8045().method_8652(itemPlacementContext.getBlockPos(), blockState, 26);
	}
}
