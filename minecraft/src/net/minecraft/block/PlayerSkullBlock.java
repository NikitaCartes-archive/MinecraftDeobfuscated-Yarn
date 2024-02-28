package net.minecraft.block;

import com.mojang.serialization.MapCodec;

public class PlayerSkullBlock extends SkullBlock {
	public static final MapCodec<PlayerSkullBlock> CODEC = createCodec(PlayerSkullBlock::new);

	@Override
	public MapCodec<PlayerSkullBlock> getCodec() {
		return CODEC;
	}

	protected PlayerSkullBlock(AbstractBlock.Settings settings) {
		super(SkullBlock.Type.PLAYER, settings);
	}
}
