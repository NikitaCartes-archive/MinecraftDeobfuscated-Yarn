package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum StairShape implements StringIdentifiable {
	STRAIGHT("straight"),
	INNER_LEFT("inner_left"),
	INNER_RIGHT("inner_right"),
	OUTER_LEFT("outer_left"),
	OUTER_RIGHT("outer_right");

	private final String name;

	private StairShape(String name) {
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
