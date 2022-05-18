package net.minecraft.block;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class OxidizableStairsBlock extends StairsBlock implements Oxidizable {
	private final Oxidizable.OxidationLevel oxidationLevel;

	public OxidizableStairsBlock(Oxidizable.OxidationLevel oxidationLevel, BlockState baseBlockState, AbstractBlock.Settings settings) {
		super(baseBlockState, settings);
		this.oxidationLevel = oxidationLevel;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.tickDegradation(state, world, pos, random);
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).isPresent();
	}

	public Oxidizable.OxidationLevel getDegradationLevel() {
		return this.oxidationLevel;
	}
}
