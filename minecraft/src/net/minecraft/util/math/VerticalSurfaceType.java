package net.minecraft.util.math;

import net.minecraft.util.StringIdentifiable;

public enum VerticalSurfaceType implements StringIdentifiable {
	CEILING(Direction.UP, 1, "ceiling"),
	FLOOR(Direction.DOWN, -1, "floor");

	public static final com.mojang.serialization.Codec<VerticalSurfaceType> CODEC = StringIdentifiable.createCodec(VerticalSurfaceType::values);
	private final Direction direction;
	private final int offset;
	private final String name;

	private VerticalSurfaceType(Direction direction, int offset, String name) {
		this.direction = direction;
		this.offset = offset;
		this.name = name;
	}

	public Direction getDirection() {
		return this.direction;
	}

	public int getOffset() {
		return this.offset;
	}

	@Override
	public String asString() {
		return this.name;
	}
}
