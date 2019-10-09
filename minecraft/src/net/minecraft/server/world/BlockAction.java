package net.minecraft.server.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class BlockAction {
	private final BlockPos pos;
	private final Block block;
	private final int type;
	private final int data;

	public BlockAction(BlockPos blockPos, Block block, int i, int j) {
		this.pos = blockPos;
		this.block = block;
		this.type = i;
		this.data = j;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public Block getBlock() {
		return this.block;
	}

	public int getType() {
		return this.type;
	}

	public int getData() {
		return this.data;
	}

	public boolean equals(Object object) {
		if (!(object instanceof BlockAction)) {
			return false;
		} else {
			BlockAction blockAction = (BlockAction)object;
			return this.pos.equals(blockAction.pos) && this.type == blockAction.type && this.data == blockAction.data && this.block == blockAction.block;
		}
	}

	public int hashCode() {
		int i = this.pos.hashCode();
		i = 31 * i + this.block.hashCode();
		i = 31 * i + this.type;
		return 31 * i + this.data;
	}

	public String toString() {
		return "TE(" + this.pos + ")," + this.type + "," + this.data + "," + this.block;
	}
}
