package net.minecraft.block;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.DyeColor;

public class StainedGlassBlock extends AbstractGlassBlock implements Stainable {
	private final DyeColor color;

	public StainedGlassBlock(DyeColor color, Block.Settings settings) {
		super(settings);
		this.color = color;
	}

	@Override
	public DyeColor getColor() {
		return this.color;
	}

	@Override
	public RenderLayer getRenderLayer() {
		return RenderLayer.TRANSLUCENT;
	}
}
