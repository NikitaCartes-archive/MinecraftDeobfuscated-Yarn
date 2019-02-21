package net.minecraft.entity;

public class EntitySize {
	public final float width;
	public final float height;
	public final boolean constant;

	public EntitySize(float f, float g, boolean bl) {
		this.width = f;
		this.height = g;
		this.constant = bl;
	}

	public EntitySize scaled(float f) {
		return !this.constant && f != 1.0F ? resizeable(this.width * f, this.height * f) : this;
	}

	public static EntitySize resizeable(float f, float g) {
		return new EntitySize(f, g, false);
	}

	public static EntitySize constant(float f, float g) {
		return new EntitySize(f, g, true);
	}
}
