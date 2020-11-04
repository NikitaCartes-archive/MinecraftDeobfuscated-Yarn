package net.minecraft.block;

import java.util.Random;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CopperStairsBlock extends StairsBlock implements Oxidizable {
	private final Block oxidationResult;

	public CopperStairsBlock(BlockState state, AbstractBlock.Settings settings, Block block) {
		super(state, settings);
		this.oxidationResult = block;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onBlockAdded(state, world, pos, oldState, notify);
		this.scheduleOxidation(world, this, pos);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.oxidize(world, state, pos);
	}

	@Override
	public BlockState getOxidationResult(BlockState state) {
		return this.oxidationResult
			.getDefaultState()
			.with(FACING, state.get(FACING))
			.with(HALF, state.get(HALF))
			.with(SHAPE, state.get(SHAPE))
			.with(WATERLOGGED, state.get(WATERLOGGED));
	}
}
