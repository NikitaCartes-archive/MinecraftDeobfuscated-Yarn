package net.minecraft.block;

public class GlassBlock extends AbstractGlassBlock {
	public GlassBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}
}
