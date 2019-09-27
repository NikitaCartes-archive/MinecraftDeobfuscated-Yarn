package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_4538;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class ChorusPlantBlock extends ConnectedPlantBlock {
	protected ChorusPlantBlock(Block.Settings settings) {
		super(0.3125F, settings);
		this.setDefaultState(
			this.stateFactory
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
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.withConnectionProperties(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos());
	}

	public BlockState withConnectionProperties(BlockView blockView, BlockPos blockPos) {
		Block block = blockView.getBlockState(blockPos.method_10074()).getBlock();
		Block block2 = blockView.getBlockState(blockPos.up()).getBlock();
		Block block3 = blockView.getBlockState(blockPos.north()).getBlock();
		Block block4 = blockView.getBlockState(blockPos.east()).getBlock();
		Block block5 = blockView.getBlockState(blockPos.south()).getBlock();
		Block block6 = blockView.getBlockState(blockPos.west()).getBlock();
		return this.getDefaultState()
			.with(DOWN, Boolean.valueOf(block == this || block == Blocks.CHORUS_FLOWER || block == Blocks.END_STONE))
			.with(UP, Boolean.valueOf(block2 == this || block2 == Blocks.CHORUS_FLOWER))
			.with(NORTH, Boolean.valueOf(block3 == this || block3 == Blocks.CHORUS_FLOWER))
			.with(EAST, Boolean.valueOf(block4 == this || block4 == Blocks.CHORUS_FLOWER))
			.with(SOUTH, Boolean.valueOf(block5 == this || block5 == Blocks.CHORUS_FLOWER))
			.with(WEST, Boolean.valueOf(block6 == this || block6 == Blocks.CHORUS_FLOWER));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (!blockState.canPlaceAt(iWorld, blockPos)) {
			iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		} else {
			Block block = blockState2.getBlock();
			boolean bl = block == this || block == Blocks.CHORUS_FLOWER || direction == Direction.DOWN && block == Blocks.END_STONE;
			return blockState.with((Property)FACING_PROPERTIES.get(direction), Boolean.valueOf(bl));
		}
	}

	@Override
	public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		if (!blockState.canPlaceAt(serverWorld, blockPos)) {
			serverWorld.breakBlock(blockPos, true);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, class_4538 arg, BlockPos blockPos) {
		BlockState blockState2 = arg.getBlockState(blockPos.method_10074());
		boolean bl = !arg.getBlockState(blockPos.up()).isAir() && !blockState2.isAir();

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos2 = blockPos.offset(direction);
			Block block = arg.getBlockState(blockPos2).getBlock();
			if (block == this) {
				if (bl) {
					return false;
				}

				Block block2 = arg.getBlockState(blockPos2.method_10074()).getBlock();
				if (block2 == this || block2 == Blocks.END_STONE) {
					return true;
				}
			}
		}

		Block block3 = blockState2.getBlock();
		return block3 == this || block3 == Blocks.END_STONE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
