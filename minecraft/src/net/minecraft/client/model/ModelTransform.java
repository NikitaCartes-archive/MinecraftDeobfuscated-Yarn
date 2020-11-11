package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModelTransform {
	public static final ModelTransform NONE = of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	public final float pivotX;
	public final float pivotY;
	public final float pivotZ;
	public final float pitch;
	public final float yaw;
	public final float roll;

	private ModelTransform(float pivotX, float pivotY, float pivotZ, float pitch, float yaw, float roll) {
		this.pivotX = pivotX;
		this.pivotY = pivotY;
		this.pivotZ = pivotZ;
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}

	public static ModelTransform pivot(float pivotX, float pivotY, float pivotZ) {
		return of(pivotX, pivotY, pivotZ, 0.0F, 0.0F, 0.0F);
	}

	public static ModelTransform rotation(float pitch, float yaw, float roll) {
		return of(0.0F, 0.0F, 0.0F, pitch, yaw, roll);
	}

	public static ModelTransform of(float pivotX, float pivotY, float pivotZ, float pitch, float yaw, float roll) {
		return new ModelTransform(pivotX, pivotY, pivotZ, pitch, yaw, roll);
	}
}
