package net.minecraft.block.enums;

import net.minecraft.util.SnakeCaseIdentifiable;

public enum ChestType implements SnakeCaseIdentifiable {
	field_12569("single", 0),
	field_12574("left", 2),
	field_12571("right", 1);

	public static final ChestType[] VALUES = values();
	private final String name;
	private final int opposite;

	private ChestType(String string2, int j) {
		this.name = string2;
		this.opposite = j;
	}

	@Override
	public String toSnakeCase() {
		return this.name;
	}

	public ChestType getOpposite() {
		return VALUES[this.opposite];
	}
}
