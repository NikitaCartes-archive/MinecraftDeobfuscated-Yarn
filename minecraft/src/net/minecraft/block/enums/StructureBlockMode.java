package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum StructureBlockMode implements StringRepresentable {
	field_12695("save"),
	field_12697("load"),
	field_12699("corner"),
	field_12696("data");

	private final String name;

	private StructureBlockMode(String string2) {
		this.name = string2;
	}

	@Override
	public String asString() {
		return this.name;
	}
}
