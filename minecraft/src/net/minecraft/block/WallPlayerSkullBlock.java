package net.minecraft.block;

import com.mojang.serialization.MapCodec;

public class WallPlayerSkullBlock extends WallSkullBlock {
	public static final MapCodec<WallPlayerSkullBlock> CODEC = createCodec(WallPlayerSkullBlock::new);

	@Override
	public MapCodec<WallPlayerSkullBlock> getCodec() {
		return CODEC;
	}

	protected WallPlayerSkullBlock(AbstractBlock.Settings settings) {
		super(SkullBlock.Type.PLAYER, settings);
	}
}
