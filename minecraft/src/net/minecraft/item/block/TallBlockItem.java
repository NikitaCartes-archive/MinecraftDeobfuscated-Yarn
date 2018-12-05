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
	protected boolean setBlockState(ItemPlacementContext itemPlacementContext, BlockState blockState) {
		itemPlacementContext.getWorld().setBlockState(itemPlacementContext.getPos().up(), Blocks.field_10124.getDefaultState(), 27);
		return super.setBlockState(itemPlacementContext, blockState);
	}
}
