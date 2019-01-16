package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum StairShape implements StringRepresentable {
	field_12710("straight"),
	field_12712("inner_left"),
	field_12713("inner_right"),
	field_12708("outer_left"),
	field_12709("outer_right");

	private final String name;

	private StairShape(String string2) {
		this.name = string2;
	}

	public String toString() {
		return this.name;
	}

	@Override
	public String asString() {
		return this.name;
	}
}
