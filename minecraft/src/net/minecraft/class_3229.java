package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;

public class class_3229 {
	private final BlockState field_14026;
	private final int field_14028;
	private int field_14027;

	public class_3229(int i, Block block) {
		this.field_14028 = i;
		this.field_14026 = block.getDefaultState();
	}

	public int method_14289() {
		return this.field_14028;
	}

	public BlockState method_14286() {
		return this.field_14026;
	}

	public int method_14288() {
		return this.field_14027;
	}

	public void method_14287(int i) {
		this.field_14027 = i;
	}

	public String toString() {
		return (this.field_14028 > 1 ? this.field_14028 + "*" : "") + Registry.BLOCK.getId(this.field_14026.getBlock());
	}
}
