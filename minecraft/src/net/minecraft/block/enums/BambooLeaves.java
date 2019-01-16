package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum BambooLeaves implements StringRepresentable {
	field_12469("none"),
	field_12466("small"),
	field_12468("large");

	private final String name;

	private BambooLeaves(String string2) {
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
