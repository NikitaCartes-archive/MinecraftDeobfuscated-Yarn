package net.minecraft.world.gen.chunk;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public final class VerticalBlockSample implements BlockColumn {
	private final int startY;
	private final BlockState[] states;

	public VerticalBlockSample(int startY, BlockState[] states) {
		this.startY = startY;
		this.states = states;
	}

	@Override
	public BlockState getState(int y) {
		int i = y - this.startY;
		return i >= 0 && i < this.states.length ? this.states[i] : Blocks.AIR.getDefaultState();
	}

	@Override
	public void setState(int y, BlockState state) {
		int i = y - this.startY;
		if (i >= 0 && i < this.states.length) {
			this.states[i] = state;
		} else {
			throw new IllegalArgumentException("Outside of column height: " + y);
		}
	}
}
