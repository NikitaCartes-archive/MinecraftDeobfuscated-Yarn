package net.minecraft.world.gen.chunk;

import net.minecraft.class_6557;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public final class VerticalBlockSample implements class_6557 {
	private final int startY;
	private final BlockState[] states;

	public VerticalBlockSample(int startY, BlockState[] states) {
		this.startY = startY;
		this.states = states;
	}

	@Override
	public BlockState getState(int i) {
		int j = i - this.startY;
		return j >= 0 && j < this.states.length ? this.states[j] : Blocks.AIR.getDefaultState();
	}

	@Override
	public void method_38092(int i, BlockState blockState) {
		int j = i - this.startY;
		if (j >= 0 && j < this.states.length) {
			this.states[j] = blockState;
		} else {
			throw new IllegalArgumentException("Outside of column height: " + i);
		}
	}
}
