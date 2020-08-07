package net.minecraft.entity.passive;

import java.util.Arrays;
import java.util.Comparator;

public enum HorseMarking {
	field_23808(0),
	field_23809(1),
	field_23810(2),
	field_23811(3),
	field_23812(4);

	private static final HorseMarking[] VALUES = (HorseMarking[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(HorseMarking::getIndex))
		.toArray(HorseMarking[]::new);
	private final int index;

	private HorseMarking(int index) {
		this.index = index;
	}

	public int getIndex() {
		return this.index;
	}

	public static HorseMarking byIndex(int index) {
		return VALUES[index % VALUES.length];
	}
}
