package net.minecraft.entity.passive;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.StringIdentifiable;

public enum HorseColor implements StringIdentifiable {
	WHITE(0, "white"),
	CREAMY(1, "creamy"),
	CHESTNUT(2, "chestnut"),
	BROWN(3, "brown"),
	BLACK(4, "black"),
	GRAY(5, "gray"),
	DARK_BROWN(6, "dark_brown");

	public static final com.mojang.serialization.Codec<HorseColor> CODEC = StringIdentifiable.createCodec(HorseColor::values);
	private static final HorseColor[] VALUES = (HorseColor[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(HorseColor::getIndex))
		.toArray(HorseColor[]::new);
	private final int index;
	private final String name;

	private HorseColor(int index, String name) {
		this.index = index;
		this.name = name;
	}

	public int getIndex() {
		return this.index;
	}

	public static HorseColor byIndex(int index) {
		return VALUES[index % VALUES.length];
	}

	@Override
	public String asString() {
		return this.name;
	}
}
