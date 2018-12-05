package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum BlockHalf implements StringRepresentable {
	field_12609,
	field_12607;

	public String toString() {
		return this.asString();
	}

	@Override
	public String asString() {
		return this == field_12609 ? "upper" : "lower";
	}
}
