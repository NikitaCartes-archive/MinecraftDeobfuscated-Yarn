package net.minecraft.item.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;

public class TallBlockItem extends BlockItem {
	public TallBlockItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Override
	protected boolean method_7708(ItemPlacementContext itemPlacementContext, BlockState blockState) {
		itemPlacementContext.method_8045().method_8652(itemPlacementContext.method_8037().up(), Blocks.field_10124.method_9564(), 27);
		return super.method_7708(itemPlacementContext, blockState);
	}
}
