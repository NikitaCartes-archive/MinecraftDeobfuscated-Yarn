package net.minecraft.world.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class SimpleNeighborUpdater implements NeighborUpdater {
	private final ServerWorld world;

	public SimpleNeighborUpdater(ServerWorld world) {
		this.world = world;
	}

	@Override
	public void updateNeighbor(BlockPos pos, Block sourceBlock, BlockPos sourcePos) {
		BlockState blockState = this.world.getBlockState(pos);
		this.updateNeighbor(blockState, pos, sourceBlock, sourcePos, false);
	}

	@Override
	public void updateNeighbor(BlockState state, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		NeighborUpdater.tryNeighborUpdate(this.world, state, pos, sourceBlock, sourcePos, notify);
	}
}
