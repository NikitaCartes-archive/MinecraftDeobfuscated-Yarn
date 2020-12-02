package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum SculkSensorPhase implements StringIdentifiable {
	INACTIVE("inactive"),
	ACTIVE("active"),
	COOLDOWN("cooldown");

	private final String name;

	private SculkSensorPhase(String name) {
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
