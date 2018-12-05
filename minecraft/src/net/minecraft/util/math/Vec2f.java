package net.minecraft.util.math;

public class Vec2f {
	public static final Vec2f ZERO = new Vec2f(0.0F, 0.0F);
	public static final Vec2f field_1335 = new Vec2f(1.0F, 1.0F);
	public static final Vec2f field_1341 = new Vec2f(1.0F, 0.0F);
	public static final Vec2f field_1338 = new Vec2f(-1.0F, 0.0F);
	public static final Vec2f field_1336 = new Vec2f(0.0F, 1.0F);
	public static final Vec2f field_1344 = new Vec2f(0.0F, -1.0F);
	public static final Vec2f field_1337 = new Vec2f(Float.MAX_VALUE, Float.MAX_VALUE);
	public static final Vec2f field_1339 = new Vec2f(Float.MIN_VALUE, Float.MIN_VALUE);
	public final float x;
	public final float y;

	public Vec2f(float f, float g) {
		this.x = f;
		this.y = g;
	}

	public boolean equals(Vec2f vec2f) {
		return this.x == vec2f.x && this.y == vec2f.y;
	}
}
