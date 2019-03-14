package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;

public class TransparentBlock extends Block {
	protected TransparentBlock(Block.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean skipRenderingSide(BlockState blockState, BlockState blockState2, Direction direction) {
		return blockState2.getBlock() == this ? true : super.skipRenderingSide(blockState, blockState2, direction);
	}
}
