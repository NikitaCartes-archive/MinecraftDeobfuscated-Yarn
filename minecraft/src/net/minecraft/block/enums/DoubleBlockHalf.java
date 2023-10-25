package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public enum DoubleBlockHalf implements StringIdentifiable {
	UPPER(Direction.DOWN),
	LOWER(Direction.UP);

	private final Direction field_47101;

	private DoubleBlockHalf(Direction direction) {
		this.field_47101 = direction;
	}

	public Direction method_54779() {
		return this.field_47101;
	}

	public String toString() {
		return this.asString();
	}

	@Override
	public String asString() {
		return this == UPPER ? "upper" : "lower";
	}

	public DoubleBlockHalf method_54780() {
		return this == UPPER ? LOWER : UPPER;
	}
}
