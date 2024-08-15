package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrappedChestBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;

public class TrappedChestBlockEntity extends ChestBlockEntity {
	public TrappedChestBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityType.TRAPPED_CHEST, blockPos, blockState);
	}

	@Override
	protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
		super.onViewerCountUpdate(world, pos, state, oldViewerCount, newViewerCount);
		if (oldViewerCount != newViewerCount) {
			WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation(
				world, ((Direction)state.get(TrappedChestBlock.FACING)).getOpposite(), Direction.UP
			);
			Block block = state.getBlock();
			world.updateNeighborsAlways(pos, block, wireOrientation);
			world.updateNeighborsAlways(pos.down(), block, wireOrientation);
		}
	}
}
