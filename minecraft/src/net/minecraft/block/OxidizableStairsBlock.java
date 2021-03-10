package net.minecraft.block;

import java.util.Random;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class OxidizableStairsBlock extends StairsBlock implements Oxidizable {
	private final Oxidizable.OxidizationLevel oxidizationLevel;
	private final Block degraded;

	public OxidizableStairsBlock(BlockState blockState, AbstractBlock.Settings settings) {
		super(blockState, settings);
		this.oxidizationLevel = Oxidizable.OxidizationLevel.values()[Oxidizable.OxidizationLevel.values().length - 1];
		this.degraded = this;
	}

	public OxidizableStairsBlock(BlockState baseBlockState, AbstractBlock.Settings settings, Oxidizable.OxidizationLevel oxidizationLevel, Block degraded) {
		super(baseBlockState, settings);
		this.oxidizationLevel = oxidizationLevel;
		this.degraded = degraded;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.tickDegradation(state, world, pos, random);
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return this.degraded != this;
	}

	public Oxidizable.OxidizationLevel getDegradationLevel() {
		return this.oxidizationLevel;
	}

	@Override
	public BlockState getDegradationResult(BlockState state) {
		return this.degraded
			.getDefaultState()
			.with(FACING, state.get(FACING))
			.with(HALF, state.get(HALF))
			.with(SHAPE, state.get(SHAPE))
			.with(WATERLOGGED, state.get(WATERLOGGED));
	}
}
