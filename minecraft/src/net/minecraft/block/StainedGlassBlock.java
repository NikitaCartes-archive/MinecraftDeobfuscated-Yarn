package net.minecraft.block;

import net.minecraft.client.block.ColoredBlock;
import net.minecraft.util.DyeColor;

public class StainedGlassBlock extends AbstractGlassBlock implements ColoredBlock {
	private final DyeColor color;

	public StainedGlassBlock(DyeColor dyeColor, Block.Settings settings) {
		super(settings);
		this.color = dyeColor;
	}

	@Override
	public DyeColor getColor() {
		return this.color;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9179;
	}
}
