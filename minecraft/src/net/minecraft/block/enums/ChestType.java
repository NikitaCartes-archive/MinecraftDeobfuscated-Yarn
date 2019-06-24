package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum ChestType implements StringIdentifiable {
	SINGLE("single", 0),
	LEFT("left", 2),
	RIGHT("right", 1);

	public static final ChestType[] VALUES = values();
	private final String name;
	private final int opposite;

	private ChestType(String string2, int j) {
		this.name = string2;
		this.opposite = j;
	}

	@Override
	public String asString() {
		return this.name;
	}

	public ChestType getOpposite() {
		return VALUES[this.opposite];
	}
}
