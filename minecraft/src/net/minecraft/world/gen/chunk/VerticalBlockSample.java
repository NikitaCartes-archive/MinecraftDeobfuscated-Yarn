package net.minecraft.world.gen.chunk;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public final class VerticalBlockSample {
	private final int field_28105;
	private final BlockState[] states;

	public VerticalBlockSample(int i, BlockState[] blockStates) {
		this.field_28105 = i;
		this.states = blockStates;
	}

	public BlockState method_32892(BlockPos blockPos) {
		int i = blockPos.getY() - this.field_28105;
		return i >= 0 && i < this.states.length ? this.states[i] : Blocks.AIR.getDefaultState();
	}
}
