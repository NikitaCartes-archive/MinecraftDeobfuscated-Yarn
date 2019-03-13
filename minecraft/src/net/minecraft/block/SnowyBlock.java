package net.minecraft.block;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class SnowyBlock extends Block {
	public static final BooleanProperty field_11522 = Properties.field_12512;

	protected SnowyBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11522, Boolean.valueOf(false)));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (direction != Direction.UP) {
			return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			Block block = blockState2.getBlock();
			return blockState.method_11657(field_11522, Boolean.valueOf(block == Blocks.field_10491 || block == Blocks.field_10477));
		}
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		Block block = itemPlacementContext.method_8045().method_8320(itemPlacementContext.method_8037().up()).getBlock();
		return this.method_9564().method_11657(field_11522, Boolean.valueOf(block == Blocks.field_10491 || block == Blocks.field_10477));
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11522);
	}
}
