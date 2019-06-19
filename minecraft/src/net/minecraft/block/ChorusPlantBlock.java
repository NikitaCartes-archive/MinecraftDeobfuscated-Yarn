package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

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
		Block block = blockView.getBlockState(blockPos.down()).getBlock();
		Block block2 = blockView.getBlockState(blockPos.up()).getBlock();
		Block block3 = blockView.getBlockState(blockPos.north()).getBlock();
		Block block4 = blockView.getBlockState(blockPos.east()).getBlock();
		Block block5 = blockView.getBlockState(blockPos.south()).getBlock();
		Block block6 = blockView.getBlockState(blockPos.west()).getBlock();
		return this.getDefaultState()
			.with(DOWN, Boolean.valueOf(block == this || block == Blocks.field_10528 || block == Blocks.field_10471))
			.with(UP, Boolean.valueOf(block2 == this || block2 == Blocks.field_10528))
			.with(NORTH, Boolean.valueOf(block3 == this || block3 == Blocks.field_10528))
			.with(EAST, Boolean.valueOf(block4 == this || block4 == Blocks.field_10528))
			.with(SOUTH, Boolean.valueOf(block5 == this || block5 == Blocks.field_10528))
			.with(WEST, Boolean.valueOf(block6 == this || block6 == Blocks.field_10528));
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
			boolean bl = block == this || block == Blocks.field_10528 || direction == Direction.field_11033 && block == Blocks.field_10471;
			return blockState.with((Property)FACING_PROPERTIES.get(direction), Boolean.valueOf(bl));
		}
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!blockState.canPlaceAt(world, blockPos)) {
			world.breakBlock(blockPos, true);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockState blockState2 = viewableWorld.getBlockState(blockPos.down());
		boolean bl = !viewableWorld.getBlockState(blockPos.up()).isAir() && !blockState2.isAir();

		for (Direction direction : Direction.Type.field_11062) {
			BlockPos blockPos2 = blockPos.offset(direction);
			Block block = viewableWorld.getBlockState(blockPos2).getBlock();
			if (block == this) {
				if (bl) {
					return false;
				}

				Block block2 = viewableWorld.getBlockState(blockPos2.down()).getBlock();
				if (block2 == this || block2 == Blocks.field_10471) {
					return true;
				}
			}
		}

		Block block3 = blockState2.getBlock();
		return block3 == this || block3 == Blocks.field_10471;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
