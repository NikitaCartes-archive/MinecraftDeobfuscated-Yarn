package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class BedItem extends BlockItem {
	public BedItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Override
	protected boolean place(ItemPlacementContext context, BlockState state) {
		return context.getWorld().setBlockState(context.getBlockPos(), state, Block.NOTIFY_LISTENERS | Block.REDRAW_ON_MAIN_THREAD | Block.FORCE_STATE);
	}
}
