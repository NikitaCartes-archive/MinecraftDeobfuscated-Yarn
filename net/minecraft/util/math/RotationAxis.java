/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import org.joml.Quaternionf;
import org.joml.Vector3f;

@FunctionalInterface
public interface RotationAxis {
    public static final RotationAxis NEGATIVE_X = rad -> new Quaternionf().rotationX(-rad);
    public static final RotationAxis POSITIVE_X = rad -> new Quaternionf().rotationX(rad);
    public static final RotationAxis NEGATIVE_Y = rad -> new Quaternionf().rotationY(-rad);
    public static final RotationAxis POSITIVE_Y = rad -> new Quaternionf().rotationY(rad);
    public static final RotationAxis NEGATIVE_Z = rad -> new Quaternionf().rotationZ(-rad);
    public static final RotationAxis POSITIVE_Z = rad -> new Quaternionf().rotationZ(rad);

    public static RotationAxis of(Vector3f axis) {
        return rad -> new Quaternionf().rotationAxis(rad, axis);
    }

    public Quaternionf rotation(float var1);

    default public Quaternionf rotationDegrees(float deg) {
        return this.rotation(deg * ((float)Math.PI / 180));
    }
}

