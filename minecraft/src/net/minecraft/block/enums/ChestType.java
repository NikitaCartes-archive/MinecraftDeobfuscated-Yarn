package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum ChestType implements StringIdentifiable {
	field_12569("single", 0),
	field_12574("left", 2),
	field_12571("right", 1);

	public static final ChestType[] VALUES = values();
	private final String name;
	private final int opposite;

	private ChestType(String name, int opposite) {
		this.name = name;
		this.opposite = opposite;
	}

	@Override
	public String asString() {
		return this.name;
	}

	public ChestType getOpposite() {
		return VALUES[this.opposite];
	}
}
