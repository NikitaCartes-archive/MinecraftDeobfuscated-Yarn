package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;

public class TransparentBlock extends Block {
	protected TransparentBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		return stateFrom.getBlock() == this ? true : super.isSideInvisible(state, stateFrom, direction);
	}
}
