package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum PistonType implements StringIdentifiable {
	DEFAULT("normal"),
	STICKY("sticky");

	private final String name;

	private PistonType(String name) {
		this.name = name;
	}

	public String toString() {
		return this.name;
	}

	@Override
	public String asString() {
		return this.name;
	}
}
