package net.minecraft.entity;

import net.minecraft.class_9066;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public record EntityDimensions(float width, float height, float eyeHeight, class_9066 attachments, boolean fixed) {
	private EntityDimensions(float width, float height, boolean fixed) {
		this(width, height, method_55686(height), class_9066.field_47751, fixed);
	}

	private static float method_55686(float f) {
		return f * 0.85F;
	}

	public Box getBoxAt(Vec3d pos) {
		return this.getBoxAt(pos.x, pos.y, pos.z);
	}

	public Box getBoxAt(double x, double y, double z) {
		float f = this.width / 2.0F;
		float g = this.height;
		return new Box(x - (double)f, y, z - (double)f, x + (double)f, y + (double)g, z + (double)f);
	}

	public EntityDimensions scaled(float ratio) {
		return this.scaled(ratio, ratio);
	}

	public EntityDimensions scaled(float widthRatio, float heightRatio) {
		return !this.fixed && (widthRatio != 1.0F || heightRatio != 1.0F)
			? new EntityDimensions(
				this.width * widthRatio, this.height * heightRatio, this.eyeHeight * heightRatio, this.attachments.method_55674(widthRatio, heightRatio, widthRatio), false
			)
			: this;
	}

	public static EntityDimensions changing(float width, float height) {
		return new EntityDimensions(width, height, false);
	}

	public static EntityDimensions fixed(float width, float height) {
		return new EntityDimensions(width, height, true);
	}

	public EntityDimensions method_55685(float f) {
		return new EntityDimensions(this.width, this.height, f, this.attachments, this.fixed);
	}

	public EntityDimensions method_55684(class_9066.class_9067 arg) {
		return new EntityDimensions(this.width, this.height, this.eyeHeight, arg.method_55680(this.width, this.height), this.fixed);
	}
}
