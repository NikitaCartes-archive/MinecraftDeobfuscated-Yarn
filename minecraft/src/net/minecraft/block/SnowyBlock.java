package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class SnowyBlock extends Block {
	public static final MapCodec<SnowyBlock> CODEC = createCodec(SnowyBlock::new);
	public static final BooleanProperty SNOWY = Properties.SNOWY;

	@Override
	protected MapCodec<? extends SnowyBlock> getCodec() {
		return CODEC;
	}

	protected SnowyBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(SNOWY, Boolean.valueOf(false)));
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state,
		WorldView world,
		ScheduledTickView tickView,
		BlockPos pos,
		Direction direction,
		BlockPos neighborPos,
		BlockState neighborState,
		Random random
	) {
		return direction == Direction.UP
			? state.with(SNOWY, Boolean.valueOf(isSnow(neighborState)))
			: super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().up());
		return this.getDefaultState().with(SNOWY, Boolean.valueOf(isSnow(blockState)));
	}

	private static boolean isSnow(BlockState state) {
		return state.isIn(BlockTags.SNOW);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(SNOWY);
	}
}
