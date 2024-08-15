package net.minecraft.block;

import com.mojang.serialization.MapCodec;

public class TintedGlassBlock extends TransparentBlock {
	public static final MapCodec<TintedGlassBlock> CODEC = createCodec(TintedGlassBlock::new);

	@Override
	public MapCodec<TintedGlassBlock> getCodec() {
		return CODEC;
	}

	public TintedGlassBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected boolean isTransparent(BlockState state) {
		return false;
	}

	@Override
	protected int getOpacity(BlockState state) {
		return 15;
	}
}
