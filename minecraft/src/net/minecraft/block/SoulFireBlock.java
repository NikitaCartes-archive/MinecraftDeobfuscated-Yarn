package net.minecraft.block;

import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public class SoulFireBlock extends AbstractFireBlock {
	public SoulFireBlock(AbstractBlock.Settings settings) {
		super(settings, 2.0F);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, IWorld world, BlockPos pos, BlockPos posFrom) {
		return this.canPlaceAt(state, world, pos) ? this.getDefaultState() : Blocks.AIR.getDefaultState();
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return method_26158(world.getBlockState(pos.down()).getBlock());
	}

	public static boolean method_26158(Block block) {
		return block.isIn(BlockTags.SOUL_FIRE_BASE_BLOCKS);
	}

	@Override
	protected boolean isFlammable(BlockState state) {
		return true;
	}
}
