/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.function.ToDoubleFunction;
import net.minecraft.class_5493;
import net.minecraft.class_5535;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class class_5534 {
    @Nullable
    public static Vec3d method_31527(PathAwareEntity pathAwareEntity, int i, int j) {
        return class_5534.method_31530(pathAwareEntity, i, j, pathAwareEntity::getPathfindingFavor);
    }

    @Nullable
    public static Vec3d method_31530(PathAwareEntity pathAwareEntity, int i, int j, ToDoubleFunction<BlockPos> toDoubleFunction) {
        boolean bl = class_5493.method_31517(pathAwareEntity, i);
        return class_5535.method_31543(() -> {
            BlockPos blockPos = class_5535.method_31541(pathAwareEntity.getRandom(), i, j);
            BlockPos blockPos2 = class_5534.method_31532(pathAwareEntity, i, bl, blockPos);
            if (blockPos2 == null) {
                return null;
            }
            return class_5534.method_31533(pathAwareEntity, blockPos2);
        }, toDoubleFunction);
    }

    @Nullable
    public static Vec3d method_31528(PathAwareEntity pathAwareEntity, int i, int j, Vec3d vec3d) {
        Vec3d vec3d2 = vec3d.subtract(pathAwareEntity.getX(), pathAwareEntity.getY(), pathAwareEntity.getZ());
        boolean bl = class_5493.method_31517(pathAwareEntity, i);
        return class_5534.method_31529(pathAwareEntity, i, j, vec3d2, bl);
    }

    @Nullable
    public static Vec3d method_31534(PathAwareEntity pathAwareEntity, int i, int j, Vec3d vec3d) {
        Vec3d vec3d2 = pathAwareEntity.getPos().subtract(vec3d);
        boolean bl = class_5493.method_31517(pathAwareEntity, i);
        return class_5534.method_31529(pathAwareEntity, i, j, vec3d2, bl);
    }

    @Nullable
    private static Vec3d method_31529(PathAwareEntity pathAwareEntity, int i, int j, Vec3d vec3d, boolean bl) {
        return class_5535.method_31538(pathAwareEntity, () -> {
            BlockPos blockPos = class_5535.method_31542(pathAwareEntity.getRandom(), i, j, 0, vec3d.x, vec3d.z, 1.5707963705062866);
            if (blockPos == null) {
                return null;
            }
            BlockPos blockPos2 = class_5534.method_31532(pathAwareEntity, i, bl, blockPos);
            if (blockPos2 == null) {
                return null;
            }
            return class_5534.method_31533(pathAwareEntity, blockPos2);
        });
    }

    @Nullable
    public static BlockPos method_31533(PathAwareEntity pathAwareEntity, BlockPos blockPos2) {
        if (class_5493.method_31518(pathAwareEntity, blockPos2 = class_5535.method_31540(blockPos2, pathAwareEntity.world.getTopHeightLimit(), blockPos -> class_5493.method_31523(pathAwareEntity, blockPos))) || class_5493.method_31522(pathAwareEntity, blockPos2)) {
            return null;
        }
        return blockPos2;
    }

    @Nullable
    public static BlockPos method_31532(PathAwareEntity pathAwareEntity, int i, boolean bl, BlockPos blockPos) {
        BlockPos blockPos2 = class_5535.method_31537(pathAwareEntity, i, pathAwareEntity.getRandom(), blockPos);
        if (class_5493.method_31520(blockPos2, pathAwareEntity) || class_5493.method_31521(bl, pathAwareEntity, blockPos2) || class_5493.method_31519(pathAwareEntity.getNavigation(), blockPos2)) {
            return null;
        }
        return blockPos2;
    }
}

