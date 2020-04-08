package net.minecraft.entity.passive;

import java.util.Arrays;
import java.util.Comparator;

public enum HorseColor {
	WHITE(0),
	CREAMY(1),
	CHESTNUT(2),
	BROWN(3),
	BLACK(4),
	GRAY(5),
	DARKBROWN(6);

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
