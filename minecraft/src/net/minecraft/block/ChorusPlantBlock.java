package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class ChorusPlantBlock extends ConnectingBlock {
	public static final MapCodec<ChorusPlantBlock> CODEC = createCodec(ChorusPlantBlock::new);

	@Override
	public MapCodec<ChorusPlantBlock> getCodec() {
		return CODEC;
	}

	protected ChorusPlantBlock(AbstractBlock.Settings settings) {
		super(0.3125F, settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(NORTH, Boolean.valueOf(false))
				.with(EAST, Boolean.valueOf(false))
				.with(SOUTH, Boolean.valueOf(false))
				.with(WEST, Boolean.valueOf(false))
				.with(UP, Boolean.valueOf(false))
				.with(DOWN, Boolean.valueOf(false))
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return withConnectionProperties(ctx.getWorld(), ctx.getBlockPos(), this.getDefaultState());
	}

	public static BlockState withConnectionProperties(BlockView world, BlockPos pos, BlockState state) {
		BlockState blockState = world.getBlockState(pos.down());
		BlockState blockState2 = world.getBlockState(pos.up());
		BlockState blockState3 = world.getBlockState(pos.north());
		BlockState blockState4 = world.getBlockState(pos.east());
		BlockState blockState5 = world.getBlockState(pos.south());
		BlockState blockState6 = world.getBlockState(pos.west());
		Block block = state.getBlock();
		return state.withIfExists(DOWN, Boolean.valueOf(blockState.isOf(block) || blockState.isOf(Blocks.CHORUS_FLOWER) || blockState.isOf(Blocks.END_STONE)))
			.withIfExists(UP, Boolean.valueOf(blockState2.isOf(block) || blockState2.isOf(Blocks.CHORUS_FLOWER)))
			.withIfExists(NORTH, Boolean.valueOf(blockState3.isOf(block) || blockState3.isOf(Blocks.CHORUS_FLOWER)))
			.withIfExists(EAST, Boolean.valueOf(blockState4.isOf(block) || blockState4.isOf(Blocks.CHORUS_FLOWER)))
			.withIfExists(SOUTH, Boolean.valueOf(blockState5.isOf(block) || blockState5.isOf(Blocks.CHORUS_FLOWER)))
			.withIfExists(WEST, Boolean.valueOf(blockState6.isOf(block) || blockState6.isOf(Blocks.CHORUS_FLOWER)));
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if (!state.canPlaceAt(world, pos)) {
			world.scheduleBlockTick(pos, this, 1);
			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		} else {
			boolean bl = neighborState.isOf(this) || neighborState.isOf(Blocks.CHORUS_FLOWER) || direction == Direction.DOWN && neighborState.isOf(Blocks.END_STONE);
			return state.with((Property)FACING_PROPERTIES.get(direction), Boolean.valueOf(bl));
		}
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!state.canPlaceAt(world, pos)) {
			world.breakBlock(pos, true);
		}
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.down());
		boolean bl = !world.getBlockState(pos.up()).isAir() && !blockState.isAir();

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			BlockState blockState2 = world.getBlockState(blockPos);
			if (blockState2.isOf(this)) {
				if (bl) {
					return false;
				}

				BlockState blockState3 = world.getBlockState(blockPos.down());
				if (blockState3.isOf(this) || blockState3.isOf(Blocks.END_STONE)) {
					return true;
				}
			}
		}

		return blockState.isOf(this) || blockState.isOf(Blocks.END_STONE);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
