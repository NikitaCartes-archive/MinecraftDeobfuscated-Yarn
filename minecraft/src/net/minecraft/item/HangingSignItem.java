package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallHangingSignBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

public class HangingSignItem extends SignItem {
	public HangingSignItem(Block hangingSign, Block wallHangingSign, Item.Settings settings) {
		super(settings, hangingSign, wallHangingSign, Direction.UP);
	}

	@Override
	protected boolean canPlaceAt(WorldView world, BlockState state, BlockPos pos) {
		if (state.getBlock() instanceof WallHangingSignBlock wallHangingSignBlock && !wallHangingSignBlock.canAttachAt(state, world, pos)) {
			return false;
		}

		return super.canPlaceAt(world, state, pos);
	}
}
