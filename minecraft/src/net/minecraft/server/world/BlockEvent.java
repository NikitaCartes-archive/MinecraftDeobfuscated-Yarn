package net.minecraft.server.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public record BlockEvent() {
	private final BlockPos pos;
	private final Block block;
	private final int type;
	private final int data;

	public BlockEvent(BlockPos pos, Block block, int type, int data) {
		this.pos = pos;
		this.block = block;
		this.type = type;
		this.data = data;
	}
}
