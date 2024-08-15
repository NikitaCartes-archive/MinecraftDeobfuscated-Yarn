package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record ModelTransform(float pivotX, float pivotY, float pivotZ, float pitch, float yaw, float roll, float xScale, float yScale, float zScale) {
	public static final ModelTransform NONE = of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

	public static ModelTransform pivot(float pivotX, float pivotY, float pivotZ) {
		return of(pivotX, pivotY, pivotZ, 0.0F, 0.0F, 0.0F);
	}

	public static ModelTransform rotation(float pitch, float yaw, float roll) {
		return of(0.0F, 0.0F, 0.0F, pitch, yaw, roll);
	}

	public static ModelTransform of(float pivotX, float pivotY, float pivotZ, float pitch, float yaw, float roll) {
		return new ModelTransform(pivotX, pivotY, pivotZ, pitch, yaw, roll, 1.0F, 1.0F, 1.0F);
	}

	public ModelTransform addPivot(float pivotX, float pivotY, float pivotZ) {
		return new ModelTransform(
			this.pivotX + pivotX, this.pivotY + pivotY, this.pivotZ + pivotZ, this.pitch, this.yaw, this.roll, this.xScale, this.yScale, this.zScale
		);
	}

	public ModelTransform withScale(float scale) {
		return new ModelTransform(this.pivotX, this.pivotY, this.pivotZ, this.pitch, this.yaw, this.roll, scale, scale, scale);
	}

	public ModelTransform scaled(float scale) {
		return scale == 1.0F ? this : this.scaled(scale, scale, scale);
	}

	public ModelTransform scaled(float xScale, float yScale, float zScale) {
		return new ModelTransform(
			this.pivotX * xScale,
			this.pivotY * yScale,
			this.pivotZ * zScale,
			this.pitch,
			this.yaw,
			this.roll,
			this.xScale * xScale,
			this.yScale * yScale,
			this.zScale * zScale
		);
	}
}
