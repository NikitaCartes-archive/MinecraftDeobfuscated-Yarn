package net.minecraft.block;

import net.minecraft.util.DyeColor;

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
}
