/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.class_5493;
import net.minecraft.class_5535;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class class_5530 {
    @Nullable
    public static Vec3d method_31504(PathAwareEntity pathAwareEntity, int i, int j, int k, double d, double e, double f) {
        boolean bl = class_5493.method_31517(pathAwareEntity, i);
        return class_5535.method_31538(pathAwareEntity, () -> class_5530.method_31505(pathAwareEntity, i, j, k, d, e, f, bl));
    }

    @Nullable
    public static BlockPos method_31505(PathAwareEntity pathAwareEntity, int i, int j, int k, double d, double e, double f, boolean bl) {
        BlockPos blockPos2 = class_5535.method_31542(pathAwareEntity.getRandom(), i, j, k, d, e, f);
        if (blockPos2 == null) {
            return null;
        }
        BlockPos blockPos22 = class_5535.method_31537(pathAwareEntity, i, pathAwareEntity.getRandom(), blockPos2);
        if (class_5493.method_31520(blockPos22, pathAwareEntity) || class_5493.method_31521(bl, pathAwareEntity, blockPos22)) {
            return null;
        }
        if (class_5493.method_31522(pathAwareEntity, blockPos22 = class_5535.method_31540(blockPos22, pathAwareEntity.world.getTopHeightLimit(), blockPos -> class_5493.method_31523(pathAwareEntity, blockPos)))) {
            return null;
        }
        return blockPos22;
    }
}

