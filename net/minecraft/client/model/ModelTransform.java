/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class ModelTransform {
    public static final ModelTransform NONE = ModelTransform.of(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
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
        return ModelTransform.of(pivotX, pivotY, pivotZ, 0.0f, 0.0f, 0.0f);
    }

    public static ModelTransform rotation(float pitch, float yaw, float roll) {
        return ModelTransform.of(0.0f, 0.0f, 0.0f, pitch, yaw, roll);
    }

    public static ModelTransform of(float pivotX, float pivotY, float pivotZ, float pitch, float yaw, float roll) {
        return new ModelTransform(pivotX, pivotY, pivotZ, pitch, yaw, roll);
    }
}

