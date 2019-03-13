package net.minecraft.item.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;

public class BedItem extends BlockItem {
	public BedItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Override
	protected boolean method_7708(ItemPlacementContext itemPlacementContext, BlockState blockState) {
		return itemPlacementContext.method_8045().method_8652(itemPlacementContext.method_8037(), blockState, 26);
	}
}
