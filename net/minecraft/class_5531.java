/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.class_5493;
import net.minecraft.class_5530;
import net.minecraft.class_5535;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class class_5531 {
    @Nullable
    public static Vec3d method_31508(PathAwareEntity pathAwareEntity, int i, int j, int k, Vec3d vec3d, double d) {
        Vec3d vec3d2 = vec3d.subtract(pathAwareEntity.getX(), pathAwareEntity.getY(), pathAwareEntity.getZ());
        boolean bl = class_5493.method_31517(pathAwareEntity, i);
        return class_5535.method_31538(pathAwareEntity, () -> {
            BlockPos blockPos = class_5530.method_31505(pathAwareEntity, i, j, k, vec3d.x, vec3d.z, d, bl);
            if (blockPos == null || class_5493.method_31518(pathAwareEntity, blockPos)) {
                return null;
            }
            return blockPos;
        });
    }
}

