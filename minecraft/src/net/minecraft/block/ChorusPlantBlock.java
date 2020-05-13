package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class ChorusPlantBlock extends ConnectingBlock {
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
		return this.withConnectionProperties(ctx.getWorld(), ctx.getBlockPos());
	}

	public BlockState withConnectionProperties(BlockView world, BlockPos pos) {
		Block block = world.getBlockState(pos.down()).getBlock();
		Block block2 = world.getBlockState(pos.up()).getBlock();
		Block block3 = world.getBlockState(pos.north()).getBlock();
		Block block4 = world.getBlockState(pos.east()).getBlock();
		Block block5 = world.getBlockState(pos.south()).getBlock();
		Block block6 = world.getBlockState(pos.west()).getBlock();
		return this.getDefaultState()
			.with(DOWN, Boolean.valueOf(block == this || block == Blocks.CHORUS_FLOWER || block == Blocks.END_STONE))
			.with(UP, Boolean.valueOf(block2 == this || block2 == Blocks.CHORUS_FLOWER))
			.with(NORTH, Boolean.valueOf(block3 == this || block3 == Blocks.CHORUS_FLOWER))
			.with(EAST, Boolean.valueOf(block4 == this || block4 == Blocks.CHORUS_FLOWER))
			.with(SOUTH, Boolean.valueOf(block5 == this || block5 == Blocks.CHORUS_FLOWER))
			.with(WEST, Boolean.valueOf(block6 == this || block6 == Blocks.CHORUS_FLOWER));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (!state.canPlaceAt(world, pos)) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
			return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
		} else {
			boolean bl = newState.getBlock() == this || newState.isOf(Blocks.CHORUS_FLOWER) || direction == Direction.DOWN && newState.isOf(Blocks.END_STONE);
			return state.with((Property)FACING_PROPERTIES.get(direction), Boolean.valueOf(bl));
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
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
