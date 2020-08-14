package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum WireConnection implements StringIdentifiable {
	UP("up"),
	SIDE("side"),
	NONE("none");

	private final String name;

	private WireConnection(String name) {
		this.name = name;
	}

	public String toString() {
		return this.asString();
	}

	@Override
	public String asString() {
		return this.name;
	}

	public boolean isConnected() {
		return this != NONE;
	}
}
