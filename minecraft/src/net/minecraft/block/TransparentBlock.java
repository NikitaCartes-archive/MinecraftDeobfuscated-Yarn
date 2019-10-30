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
	public boolean isSideInvisible(BlockState state, BlockState neighbor, Direction facing) {
		return neighbor.getBlock() == this ? true : super.isSideInvisible(state, neighbor, facing);
	}
}
