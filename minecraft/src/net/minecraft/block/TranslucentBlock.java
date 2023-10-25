package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.Direction;

public class TranslucentBlock extends Block {
	public static final MapCodec<TranslucentBlock> CODEC = createCodec(TranslucentBlock::new);

	@Override
	protected MapCodec<? extends TranslucentBlock> getCodec() {
		return CODEC;
	}

	protected TranslucentBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		return stateFrom.isOf(this) ? true : super.isSideInvisible(state, stateFrom, direction);
	}
}
