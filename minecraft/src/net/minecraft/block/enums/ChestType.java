package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum ChestType implements StringRepresentable {
	field_12569("single", 0),
	field_12574("left", 2),
	field_12571("right", 1);

	public static final ChestType[] field_12570 = values();
	private final String name;
	private final int field_12568;

	private ChestType(String string2, int j) {
		this.name = string2;
		this.field_12568 = j;
	}

	@Override
	public String asString() {
		return this.name;
	}

	public ChestType method_11824() {
		return field_12570[this.field_12568];
	}
}
