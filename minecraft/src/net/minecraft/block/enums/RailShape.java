package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum RailShape implements StringIdentifiable {
	field_12665("north_south"),
	field_12674("east_west"),
	field_12667("ascending_east"),
	field_12666("ascending_west"),
	field_12670("ascending_north"),
	field_12668("ascending_south"),
	field_12664("south_east"),
	field_12671("south_west"),
	field_12672("north_west"),
	field_12663("north_east");

	private final String name;

	private RailShape(String name) {
		this.name = name;
	}

	public String toString() {
		return this.name;
	}

	public boolean isAscending() {
		return this == field_12670 || this == field_12667 || this == field_12668 || this == field_12666;
	}

	@Override
	public String asString() {
		return this.name;
	}
}
