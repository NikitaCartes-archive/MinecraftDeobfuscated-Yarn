package net.minecraft.block;

import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;

public class GlazedTerracottaBlock extends HorizontalFacingBlock {
	public GlazedTerracottaBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11177);
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564().method_11657(field_11177, itemPlacementContext.getPlayerFacing().getOpposite());
	}

	@Override
	public PistonBehavior method_9527(BlockState blockState) {
		return PistonBehavior.field_15970;
	}
}
