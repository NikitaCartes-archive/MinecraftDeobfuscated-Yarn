/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.class_5493;
import net.minecraft.class_5534;
import net.minecraft.class_5535;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class class_5533 {
    @Nullable
    public static Vec3d method_31524(PathAwareEntity pathAwareEntity, int i, int j, double d, double e, float f, int k, int l) {
        boolean bl = class_5493.method_31517(pathAwareEntity, i);
        return class_5535.method_31538(pathAwareEntity, () -> {
            BlockPos blockPos2 = class_5535.method_31542(pathAwareEntity.getRandom(), i, j, 0, d, e, f);
            if (blockPos2 == null) {
                return null;
            }
            BlockPos blockPos22 = class_5534.method_31532(pathAwareEntity, i, bl, blockPos2);
            if (blockPos22 == null) {
                return null;
            }
            if (class_5493.method_31518(pathAwareEntity, blockPos22 = class_5535.method_31539(blockPos22, pathAwareEntity.getRandom().nextInt(k - l + 1) + l, pathAwareEntity.world.getTopY(), blockPos -> class_5493.method_31523(pathAwareEntity, blockPos))) || class_5493.method_31522(pathAwareEntity, blockPos22)) {
                return null;
            }
            return blockPos22;
        });
    }
}

