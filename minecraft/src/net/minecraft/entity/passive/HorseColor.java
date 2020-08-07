package net.minecraft.entity.passive;

import java.util.Arrays;
import java.util.Comparator;

public enum HorseColor {
	field_23816(0),
	field_23817(1),
	field_23818(2),
	field_23819(3),
	field_23820(4),
	field_23821(5),
	field_23822(6);

	private static final HorseColor[] VALUES = (HorseColor[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(HorseColor::getIndex))
		.toArray(HorseColor[]::new);
	private final int index;

	private HorseColor(int index) {
		this.index = index;
	}

	public int getIndex() {
		return this.index;
	}

	public static HorseColor byIndex(int index) {
		return VALUES[index % VALUES.length];
	}
}
