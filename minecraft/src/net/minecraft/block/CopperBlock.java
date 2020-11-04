package net.minecraft.block;

import java.util.Random;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CopperBlock extends Block implements Oxidizable {
	private final Block oxidationResult;

	protected CopperBlock(AbstractBlock.Settings settings, Block block) {
		super(settings);
		this.oxidationResult = block;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onBlockAdded(state, world, pos, oldState, notify);
		this.scheduleOxidation(world, this, pos);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		super.scheduledTick(state, world, pos, random);
		this.oxidize(world, world.getBlockState(pos), pos);
	}

	@Override
	public BlockState getOxidationResult(BlockState state) {
		return this.oxidationResult.getDefaultState();
	}
}
