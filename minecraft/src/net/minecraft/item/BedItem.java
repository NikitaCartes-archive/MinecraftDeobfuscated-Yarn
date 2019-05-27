package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class BedItem extends BlockItem {
	public BedItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Override
	protected boolean place(ItemPlacementContext itemPlacementContext, BlockState blockState) {
		return itemPlacementContext.getWorld().setBlockState(itemPlacementContext.getBlockPos(), blockState, 26);
	}
}
