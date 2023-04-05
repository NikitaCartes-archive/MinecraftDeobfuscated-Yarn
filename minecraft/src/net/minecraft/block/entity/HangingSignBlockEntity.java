package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class HangingSignBlockEntity extends SignBlockEntity {
	private static final int MAX_TEXT_WIDTH = 60;
	private static final int TEXT_LINE_HEIGHT = 9;

	public HangingSignBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityType.HANGING_SIGN, blockPos, blockState);
	}

	@Override
	public int getTextLineHeight() {
		return 9;
	}

	@Override
	public int getMaxTextWidth() {
		return 60;
	}
}
