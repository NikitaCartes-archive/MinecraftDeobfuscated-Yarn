package net.minecraft.server.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class BlockAction {
	private final BlockPos field_9173;
	private final Block field_9172;
	private final int field_9171;
	private final int field_9170;

	public BlockAction(BlockPos blockPos, Block block, int i, int j) {
		this.field_9173 = blockPos;
		this.field_9172 = block;
		this.field_9171 = i;
		this.field_9170 = j;
	}

	public BlockPos getPos() {
		return this.field_9173;
	}

	public Block method_8309() {
		return this.field_9172;
	}

	public int method_8307() {
		return this.field_9171;
	}

	public int method_8308() {
		return this.field_9170;
	}

	public boolean equals(Object object) {
		if (!(object instanceof BlockAction)) {
			return false;
		} else {
			BlockAction blockAction = (BlockAction)object;
			return this.field_9173.equals(blockAction.field_9173)
				&& this.field_9171 == blockAction.field_9171
				&& this.field_9170 == blockAction.field_9170
				&& this.field_9172 == blockAction.field_9172;
		}
	}

	public String toString() {
		return "TE(" + this.field_9173 + ")," + this.field_9171 + "," + this.field_9170 + "," + this.field_9172;
	}
}
