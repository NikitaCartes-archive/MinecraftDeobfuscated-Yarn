package net.minecraft.util.math;

public class Vec2f {
	public static final Vec2f ZERO = new Vec2f(0.0F, 0.0F);
	public static final Vec2f SOUTH_EAST_UNIT = new Vec2f(1.0F, 1.0F);
	public static final Vec2f EAST_UNIT = new Vec2f(1.0F, 0.0F);
	public static final Vec2f WEST_UNIT = new Vec2f(-1.0F, 0.0F);
	public static final Vec2f SOUTH_UNIT = new Vec2f(0.0F, 1.0F);
	public static final Vec2f NORTH_UNIT = new Vec2f(0.0F, -1.0F);
	public static final Vec2f MAX_SOUTH_EAST = new Vec2f(Float.MAX_VALUE, Float.MAX_VALUE);
	public static final Vec2f MIN_SOUTH_EAST = new Vec2f(Float.MIN_VALUE, Float.MIN_VALUE);
	public final float x;
	public final float y;

	public Vec2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public boolean equals(Vec2f other) {
		return this.x == other.x && this.y == other.y;
	}
}
