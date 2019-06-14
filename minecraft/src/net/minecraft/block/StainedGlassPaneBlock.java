package net.minecraft.block;

import net.minecraft.client.block.ColoredBlock;
import net.minecraft.util.DyeColor;

public class StainedGlassPaneBlock extends PaneBlock implements ColoredBlock {
	private final DyeColor color;

	public StainedGlassPaneBlock(DyeColor dyeColor, Block.Settings settings) {
		super(settings);
		this.color = dyeColor;
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_10905, Boolean.valueOf(false))
				.method_11657(field_10907, Boolean.valueOf(false))
				.method_11657(field_10904, Boolean.valueOf(false))
				.method_11657(field_10903, Boolean.valueOf(false))
				.method_11657(field_10900, Boolean.valueOf(false))
		);
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
