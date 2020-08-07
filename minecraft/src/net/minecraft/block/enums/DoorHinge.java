package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum DoorHinge implements StringIdentifiable {
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
