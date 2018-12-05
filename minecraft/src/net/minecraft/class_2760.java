package net.minecraft;

import net.minecraft.util.StringRepresentable;

public enum class_2760 implements StringRepresentable {
	TOP("top"),
	BOTTOM("bottom");

	private final String name;

	private class_2760(String string2) {
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
