package net.minecraft.block;

import java.util.Random;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class OxidizableStairsBlock extends StairsBlock implements Oxidizable {
	private final Oxidizable.OxidizationLevel oxidizationLevel;
	private final Block waxed;

	public OxidizableStairsBlock(BlockState blockState, AbstractBlock.Settings settings) {
		super(blockState, settings);
		this.oxidizationLevel = Oxidizable.OxidizationLevel.values()[Oxidizable.OxidizationLevel.values().length - 1];
		this.waxed = this;
	}

	public OxidizableStairsBlock(BlockState baseBlockState, AbstractBlock.Settings settings, Oxidizable.OxidizationLevel oxidizationLevel, Block waxed) {
		super(baseBlockState, settings);
		this.oxidizationLevel = oxidizationLevel;
		this.waxed = waxed;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.tickDegradation(state, world, pos, random);
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return this.waxed != this;
	}

	public Oxidizable.OxidizationLevel getDegradationLevel() {
		return this.oxidizationLevel;
	}

	@Override
	public BlockState getDegradationResult(BlockState state) {
		return this.waxed
			.getDefaultState()
			.with(FACING, state.get(FACING))
			.with(HALF, state.get(HALF))
			.with(SHAPE, state.get(SHAPE))
			.with(WATERLOGGED, state.get(WATERLOGGED));
	}
}
