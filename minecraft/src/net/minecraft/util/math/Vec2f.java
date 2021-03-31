package net.minecraft.util.math;

/**
 * An immutable vector composed of 2 floats.
 */
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

	public Vec2f method_35582(float f) {
		return new Vec2f(this.x * f, this.y * f);
	}

	public float method_35583(Vec2f vec2f) {
		return this.x * vec2f.x + this.y * vec2f.y;
	}

	public Vec2f method_35586(Vec2f vec2f) {
		return new Vec2f(this.x + vec2f.x, this.y + vec2f.y);
	}

	public Vec2f method_35585(float f) {
		return new Vec2f(this.x + f, this.y + f);
	}

	public boolean equals(Vec2f other) {
		return this.x == other.x && this.y == other.y;
	}

	public Vec2f method_35581() {
		float f = MathHelper.sqrt(this.x * this.x + this.y * this.y);
		return f < 1.0E-4F ? ZERO : new Vec2f(this.x / f, this.y / f);
	}

	public float method_35584() {
		return MathHelper.sqrt(this.x * this.x + this.y * this.y);
	}

	public float method_35587() {
		return this.x * this.x + this.y * this.y;
	}

	public float method_35589(Vec2f vec2f) {
		float f = vec2f.x - this.x;
		float g = vec2f.y - this.y;
		return f * f + g * g;
	}

	public Vec2f method_35588() {
		return new Vec2f(-this.x, -this.y);
	}
}
