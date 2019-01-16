package net.minecraft.block.enums;

import net.minecraft.util.StringRepresentable;

public enum DoorHinge implements StringRepresentable {
	field_12588,
	field_12586;

	public String toString() {
		return this.asString();
	}

	@Override
	public String asString() {
		return this == field_12588 ? "left" : "right";
	}
}
