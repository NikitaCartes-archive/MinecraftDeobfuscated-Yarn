package net.minecraft.item;

import net.fabricmc.yarn.constants.SetBlockStateFlags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class TallBlockItem extends BlockItem {
	public TallBlockItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Override
	protected boolean place(ItemPlacementContext context, BlockState state) {
		context.getWorld()
			.setBlockState(
				context.getBlockPos().up(),
				Blocks.AIR.getDefaultState(),
				SetBlockStateFlags.DEFAULT | SetBlockStateFlags.REDRAW_ON_MAIN_THREAD | SetBlockStateFlags.FORCE_STATE
			);
		return super.place(context, state);
	}
}
