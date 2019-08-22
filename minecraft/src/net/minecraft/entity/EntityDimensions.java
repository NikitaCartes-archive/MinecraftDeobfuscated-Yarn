package net.minecraft.entity;

public class EntityDimensions {
	public final float width;
	public final float height;
	public final boolean fixed;

	public EntityDimensions(float f, float g, boolean bl) {
		this.width = f;
		this.height = g;
		this.fixed = bl;
	}

	public EntityDimensions scaled(float f) {
		return this.scaled(f, f);
	}

	public EntityDimensions scaled(float f, float g) {
		return !this.fixed && (f != 1.0F || g != 1.0F) ? changing(this.width * f, this.height * g) : this;
	}

	public static EntityDimensions changing(float f, float g) {
		return new EntityDimensions(f, g, false);
	}

	public static EntityDimensions fixed(float f, float g) {
		return new EntityDimensions(f, g, true);
	}

	public String toString() {
		return "EntityDimensions w=" + this.width + ", h=" + this.height + ", fixed=" + this.fixed;
	}
}
