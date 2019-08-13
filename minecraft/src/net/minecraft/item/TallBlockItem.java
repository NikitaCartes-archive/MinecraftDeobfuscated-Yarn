package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class TallBlockItem extends BlockItem {
	public TallBlockItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Override
	protected boolean place(ItemPlacementContext itemPlacementContext, BlockState blockState) {
		itemPlacementContext.getWorld().setBlockState(itemPlacementContext.getBlockPos().up(), Blocks.field_10124.getDefaultState(), 27);
		return super.place(itemPlacementContext, blockState);
	}
}
