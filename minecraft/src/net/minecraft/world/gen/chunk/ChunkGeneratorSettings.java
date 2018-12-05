package net.minecraft.world.gen.chunk;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class ChunkGeneratorSettings {
	protected int field_13146 = 32;
	protected int field_13145 = 8;
	protected int field_13144 = 32;
	protected int field_13143 = 5;
	protected int field_13142 = 32;
	protected int field_13141 = 128;
	protected int field_13140 = 3;
	protected int field_13139 = 32;
	protected int field_13137 = 8;
	protected int field_13155 = 16;
	protected int field_13153 = 8;
	protected int field_13152 = 20;
	protected int field_13151 = 11;
	protected int field_13150 = 16;
	protected int field_13149 = 8;
	protected int field_13148 = 80;
	protected int field_13147 = 20;
	protected BlockState defaultBlock = Blocks.field_10340.getDefaultState();
	protected BlockState defaultFluid = Blocks.field_10382.getDefaultState();

	public int method_12558() {
		return this.field_13146;
	}

	public int method_12559() {
		return this.field_13145;
	}

	public int method_12553() {
		return this.field_13144;
	}

	public int method_12556() {
		return this.field_13143;
	}

	public int method_12563() {
		return this.field_13142;
	}

	public int method_12561() {
		return this.field_13141;
	}

	public int method_12565() {
		return this.field_13140;
	}

	public int method_12567() {
		return this.field_13139;
	}

	public int method_12568() {
		return this.field_13137;
	}

	public int method_12566() {
		return this.field_13150;
	}

	public int method_12562() {
		return this.field_13149;
	}

	public int method_12564() {
		return this.field_13155;
	}

	public int method_12555() {
		return this.field_13153;
	}

	public int method_12554() {
		return this.field_13152;
	}

	public int method_12557() {
		return this.field_13151;
	}

	public int method_12560() {
		return this.field_13148;
	}

	public int method_12552() {
		return this.field_13147;
	}

	public BlockState getDefaultBlock() {
		return this.defaultBlock;
	}

	public BlockState getDefaultFluid() {
		return this.defaultFluid;
	}

	public void setDefaultBlock(BlockState blockState) {
		this.defaultBlock = blockState;
	}

	public void setDefaultFluid(BlockState blockState) {
		this.defaultFluid = blockState;
	}

	public int method_16400() {
		return 0;
	}

	public int method_16401() {
		return 256;
	}
}
