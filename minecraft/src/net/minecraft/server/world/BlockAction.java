package net.minecraft.server.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class BlockAction {
	private final BlockPos pos;
	private final Block field_9172;
	private final int type;
	private final int data;

	public BlockAction(BlockPos blockPos, Block block, int i, int j) {
		this.pos = blockPos;
		this.field_9172 = block;
		this.type = i;
		this.data = j;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public Block method_8309() {
		return this.field_9172;
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
			return this.pos.equals(blockAction.pos) && this.type == blockAction.type && this.data == blockAction.data && this.field_9172 == blockAction.field_9172;
		}
	}

	public String toString() {
		return "TE(" + this.pos + ")," + this.type + "," + this.data + "," + this.field_9172;
	}
}
