package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum RailShape implements StringIdentifiable {
	NORTH_SOUTH("north_south"),
	EAST_WEST("east_west"),
	ASCENDING_EAST("ascending_east"),
	ASCENDING_WEST("ascending_west"),
	ASCENDING_NORTH("ascending_north"),
	ASCENDING_SOUTH("ascending_south"),
	SOUTH_EAST("south_east"),
	SOUTH_WEST("south_west"),
	NORTH_WEST("north_west"),
	NORTH_EAST("north_east");

	private final String name;

	private RailShape(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.name;
	}

	public boolean isAscending() {
		return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
	}

	@Override
	public String asString() {
		return this.name;
	}
}
