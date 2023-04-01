package net.minecraft.block;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class StainedGlassPaneBlock extends PaneBlock implements Stainable {
	private final DyeColor color;

	public StainedGlassPaneBlock(DyeColor color, AbstractBlock.Settings settings) {
		super(settings);
		this.color = color;
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(NORTH, Boolean.valueOf(false))
				.with(EAST, Boolean.valueOf(false))
				.with(SOUTH, Boolean.valueOf(false))
				.with(WEST, Boolean.valueOf(false))
				.with(WATERLOGGED, Boolean.valueOf(false))
		);
	}

	@Override
	public DyeColor getColor() {
		return this.color;
	}

	@Override
	public boolean shouldLetAirThrough(BlockState state, ServerWorld world, BlockPos pos, Direction direction) {
		return PaneBlock.method_50864(state, world, pos, direction);
	}
}
