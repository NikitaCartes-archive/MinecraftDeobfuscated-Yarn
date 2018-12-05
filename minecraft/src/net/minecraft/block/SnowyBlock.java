package net.minecraft.block;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class SnowyBlock extends Block {
	public static final BooleanProperty snowy = Properties.SNOWY;

	protected SnowyBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(snowy, Boolean.valueOf(false)));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (direction != Direction.UP) {
			return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			Block block = blockState2.getBlock();
			return blockState.with(snowy, Boolean.valueOf(block == Blocks.field_10491 || block == Blocks.field_10477));
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		Block block = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getPos().up()).getBlock();
		return this.getDefaultState().with(snowy, Boolean.valueOf(block == Blocks.field_10491 || block == Blocks.field_10477));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(snowy);
	}
}
