package net.minecraft.block;

import com.mojang.serialization.MapCodec;

public class GlassBlock extends AbstractGlassBlock {
	public static final MapCodec<GlassBlock> CODEC = createCodec(GlassBlock::new);

	@Override
	public MapCodec<GlassBlock> getCodec() {
		return CODEC;
	}

	public GlassBlock(AbstractBlock.Settings settings) {
		super(settings);
	}
}
