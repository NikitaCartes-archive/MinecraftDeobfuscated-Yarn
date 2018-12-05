package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum WireConnection implements StringRepresentable {
	field_12686("up"),
	field_12689("side"),
	field_12687("none");

	private final String name;

	private WireConnection(String string2) {
		this.name = string2;
	}

	public String toString() {
		return this.asString();
	}

	@Override
	public String asString() {
		return this.name;
	}
}
