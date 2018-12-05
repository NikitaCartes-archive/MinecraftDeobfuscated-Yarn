package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class class_1919 {
	private final BlockPos field_9173;
	private final Block field_9172;
	private final int field_9171;
	private final int field_9170;

	public class_1919(BlockPos blockPos, Block block, int i, int j) {
		this.field_9173 = blockPos;
		this.field_9172 = block;
		this.field_9171 = i;
		this.field_9170 = j;
	}

	public BlockPos method_8306() {
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
		if (!(object instanceof class_1919)) {
			return false;
		} else {
			class_1919 lv = (class_1919)object;
			return this.field_9173.equals(lv.field_9173) && this.field_9171 == lv.field_9171 && this.field_9170 == lv.field_9170 && this.field_9172 == lv.field_9172;
		}
	}

	public String toString() {
		return "TE(" + this.field_9173 + ")," + this.field_9171 + "," + this.field_9170 + "," + this.field_9172;
	}
}
