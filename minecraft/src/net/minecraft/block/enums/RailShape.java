package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum RailShape implements StringIdentifiable {
	field_12665(0, "north_south"),
	field_12674(1, "east_west"),
	field_12667(2, "ascending_east"),
	field_12666(3, "ascending_west"),
	field_12670(4, "ascending_north"),
	field_12668(5, "ascending_south"),
	field_12664(6, "south_east"),
	field_12671(7, "south_west"),
	field_12672(8, "north_west"),
	field_12663(9, "north_east");

	private final int id;
	private final String name;

	private RailShape(int j, String string2) {
		this.id = j;
		this.name = string2;
	}

	public int getId() {
		return this.id;
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
