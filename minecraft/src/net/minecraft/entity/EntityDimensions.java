package net.minecraft.entity;

import net.minecraft.util.math.Box;

public class EntityDimensions {
	public final float width;
	public final float height;
	public final boolean fixed;

	public EntityDimensions(float width, float height, boolean fixed) {
		this.width = width;
		this.height = height;
		this.fixed = fixed;
	}

	public Box method_30231(double d, double e, double f) {
		float g = this.width / 2.0F;
		float h = this.height;
		return new Box(d - (double)g, e, f - (double)g, d + (double)g, e + (double)h, f + (double)g);
	}

	public EntityDimensions scaled(float ratio) {
		return this.scaled(ratio, ratio);
	}

	public EntityDimensions scaled(float widthRatio, float heightRatio) {
		return !this.fixed && (widthRatio != 1.0F || heightRatio != 1.0F) ? changing(this.width * widthRatio, this.height * heightRatio) : this;
	}

	public static EntityDimensions changing(float width, float height) {
		return new EntityDimensions(width, height, false);
	}

	public static EntityDimensions fixed(float width, float height) {
		return new EntityDimensions(width, height, true);
	}

	public String toString() {
		return "EntityDimensions w=" + this.width + ", h=" + this.height + ", fixed=" + this.fixed;
	}
}
