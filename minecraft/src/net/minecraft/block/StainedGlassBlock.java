package net.minecraft.block;

import net.minecraft.util.DyeColor;

public class StainedGlassBlock extends AbstractGlassBlock implements Stainable {
	private final DyeColor color;

	public StainedGlassBlock(DyeColor color, AbstractBlock.Settings settings) {
		super(settings);
		this.color = color;
	}

	@Override
	public DyeColor getColor() {
		return this.color;
	}
}
