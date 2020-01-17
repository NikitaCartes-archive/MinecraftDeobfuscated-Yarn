package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldView;

public class ChorusPlantBlock extends ConnectingBlock {
	protected ChorusPlantBlock(Block.Settings settings) {
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
		return this.withConnectionProperties(ctx.getWorld(), ctx.getBlockPos());
	}

	public BlockState withConnectionProperties(BlockView view, BlockPos pos) {
		Block block = view.getBlockState(pos.down()).getBlock();
		Block block2 = view.getBlockState(pos.up()).getBlock();
		Block block3 = view.getBlockState(pos.north()).getBlock();
		Block block4 = view.getBlockState(pos.east()).getBlock();
		Block block5 = view.getBlockState(pos.south()).getBlock();
		Block block6 = view.getBlockState(pos.west()).getBlock();
		return this.getDefaultState()
			.with(DOWN, Boolean.valueOf(block == this || block == Blocks.CHORUS_FLOWER || block == Blocks.END_STONE))
			.with(UP, Boolean.valueOf(block2 == this || block2 == Blocks.CHORUS_FLOWER))
			.with(NORTH, Boolean.valueOf(block3 == this || block3 == Blocks.CHORUS_FLOWER))
			.with(EAST, Boolean.valueOf(block4 == this || block4 == Blocks.CHORUS_FLOWER))
			.with(SOUTH, Boolean.valueOf(block5 == this || block5 == Blocks.CHORUS_FLOWER))
			.with(WEST, Boolean.valueOf(block6 == this || block6 == Blocks.CHORUS_FLOWER));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (!state.canPlaceAt(world, pos)) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
			return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
		} else {
			Block block = neighborState.getBlock();
			boolean bl = block == this || block == Blocks.CHORUS_FLOWER || facing == Direction.DOWN && block == Blocks.END_STONE;
			return state.with((Property)FACING_PROPERTIES.get(facing), Boolean.valueOf(bl));
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!state.canPlaceAt(world, pos)) {
			world.breakBlock(pos, true);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.down());
		boolean bl = !world.getBlockState(pos.up()).isAir() && !blockState.isAir();

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			Block block = world.getBlockState(blockPos).getBlock();
			if (block == this) {
				if (bl) {
					return false;
				}

				Block block2 = world.getBlockState(blockPos.down()).getBlock();
				if (block2 == this || block2 == Blocks.END_STONE) {
					return true;
				}
			}
		}

		Block block3 = blockState.getBlock();
		return block3 == this || block3 == Blocks.END_STONE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}

	@Override
	public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
		return false;
	}
}
