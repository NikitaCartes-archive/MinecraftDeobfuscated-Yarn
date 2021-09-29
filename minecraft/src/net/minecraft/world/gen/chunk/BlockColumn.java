package net.minecraft.world.gen.chunk;

import net.minecraft.block.BlockState;

public interface BlockColumn {
	BlockState getState(int y);

	void setState(int y, BlockState state);
}
