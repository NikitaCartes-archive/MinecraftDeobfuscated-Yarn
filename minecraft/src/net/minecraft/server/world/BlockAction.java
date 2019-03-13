package net.minecraft.server.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class BlockAction {
	private final BlockPos field_9173;
	private final Block field_9172;
	private final int type;
	private final int data;

	public BlockAction(BlockPos blockPos, Block block, int i, int j) {
		this.field_9173 = blockPos;
		this.field_9172 = block;
		this.type = i;
		this.data = j;
	}

	public BlockPos method_8306() {
		return this.field_9173;
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
			return this.field_9173.equals(blockAction.field_9173)
				&& this.type == blockAction.type
				&& this.data == blockAction.data
				&& this.field_9172 == blockAction.field_9172;
		}
	}

	public String toString() {
		return "TE(" + this.field_9173 + ")," + this.type + "," + this.data + "," + this.field_9172;
	}
}
