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

public class class_5532 {
    @Nullable
    public static Vec3d method_31510(PathAwareEntity pathAwareEntity, int i, int j) {
        boolean bl = class_5493.method_31517(pathAwareEntity, i);
        return class_5535.method_31538(pathAwareEntity, () -> {
            BlockPos blockPos = class_5535.method_31541(pathAwareEntity.getRandom(), i, j);
            return class_5532.method_31516(pathAwareEntity, i, bl, blockPos);
        });
    }

    @Nullable
    public static Vec3d method_31512(PathAwareEntity pathAwareEntity, int i, int j, Vec3d vec3d, double d) {
        Vec3d vec3d2 = vec3d.subtract(pathAwareEntity.getX(), pathAwareEntity.getY(), pathAwareEntity.getZ());
        boolean bl = class_5493.method_31517(pathAwareEntity, i);
        return class_5535.method_31538(pathAwareEntity, () -> {
            BlockPos blockPos = class_5535.method_31542(pathAwareEntity.getRandom(), i, j, 0, vec3d.x, vec3d.z, d);
            if (blockPos == null) {
                return null;
            }
            return class_5532.method_31516(pathAwareEntity, i, bl, blockPos);
        });
    }

    @Nullable
    public static Vec3d method_31511(PathAwareEntity pathAwareEntity, int i, int j, Vec3d vec3d) {
        Vec3d vec3d2 = pathAwareEntity.getPos().subtract(vec3d);
        boolean bl = class_5493.method_31517(pathAwareEntity, i);
        return class_5535.method_31538(pathAwareEntity, () -> {
            BlockPos blockPos = class_5535.method_31542(pathAwareEntity.getRandom(), i, j, 0, vec3d.x, vec3d.z, 1.5707963705062866);
            if (blockPos == null) {
                return null;
            }
            return class_5532.method_31516(pathAwareEntity, i, bl, blockPos);
        });
    }

    @Nullable
    private static BlockPos method_31516(PathAwareEntity pathAwareEntity, int i, boolean bl, BlockPos blockPos) {
        BlockPos blockPos2 = class_5535.method_31537(pathAwareEntity, i, pathAwareEntity.getRandom(), blockPos);
        if (class_5493.method_31520(blockPos2, pathAwareEntity) || class_5493.method_31521(bl, pathAwareEntity, blockPos2) || class_5493.method_31519(pathAwareEntity.getNavigation(), blockPos2) || class_5493.method_31522(pathAwareEntity, blockPos2)) {
            return null;
        }
        return blockPos2;
    }
}

