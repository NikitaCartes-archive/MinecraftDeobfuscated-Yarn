package net.minecraft.block;

import java.util.Random;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class OxidizableSlabBlock extends SlabBlock implements Oxidizable {
	private final Oxidizable.OxidizationLevel oxidizationLevel;
	private final Block degraded;

	public OxidizableSlabBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.oxidizationLevel = Oxidizable.OxidizationLevel.values()[Oxidizable.OxidizationLevel.values().length - 1];
		this.degraded = this;
	}

	public OxidizableSlabBlock(AbstractBlock.Settings settings, Oxidizable.OxidizationLevel oxidizationLevel, Block degraded) {
		super(settings);
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
		return this.degraded.getDefaultState().with(TYPE, state.get(TYPE)).with(WATERLOGGED, state.get(WATERLOGGED));
	}
}
