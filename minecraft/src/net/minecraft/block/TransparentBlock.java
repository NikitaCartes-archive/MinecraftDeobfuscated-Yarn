package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.Direction;

public class TransparentBlock extends Block {
	public static final MapCodec<TransparentBlock> CODEC = createCodec(TransparentBlock::new);

	@Override
	protected MapCodec<? extends TransparentBlock> getCodec() {
		return CODEC;
	}

	protected TransparentBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		return stateFrom.isOf(this) ? true : super.isSideInvisible(state, stateFrom, direction);
	}
}
