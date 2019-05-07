package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum DoubleBlockHalf implements StringIdentifiable {
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
