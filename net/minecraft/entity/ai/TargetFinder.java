/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai;

import java.util.Random;
import java.util.function.ToDoubleFunction;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class TargetFinder {
    @Nullable
    public static Vec3d findTarget(MobEntityWithAi mobEntityWithAi, int i, int j) {
        return TargetFinder.findTarget(mobEntityWithAi, i, j, null);
    }

    @Nullable
    public static Vec3d findGroundTarget(MobEntityWithAi mobEntityWithAi, int i, int j) {
        return TargetFinder.findGroundTarget(mobEntityWithAi, i, j, mobEntityWithAi::getPathfindingFavor);
    }

    @Nullable
    public static Vec3d findGroundTarget(MobEntityWithAi mobEntityWithAi, int i, int j, ToDoubleFunction<BlockPos> toDoubleFunction) {
        return TargetFinder.findTarget(mobEntityWithAi, i, j, null, false, 0.0, toDoubleFunction);
    }

    @Nullable
    public static Vec3d method_6373(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d) {
        Vec3d vec3d2 = vec3d.subtract(mobEntityWithAi.x, mobEntityWithAi.y, mobEntityWithAi.z);
        return TargetFinder.findTarget(mobEntityWithAi, i, j, vec3d2);
    }

    @Nullable
    public static Vec3d method_6377(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d, double d) {
        Vec3d vec3d2 = vec3d.subtract(mobEntityWithAi.x, mobEntityWithAi.y, mobEntityWithAi.z);
        return TargetFinder.findTarget(mobEntityWithAi, i, j, vec3d2, true, d, mobEntityWithAi::getPathfindingFavor);
    }

    @Nullable
    public static Vec3d method_20658(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d) {
        Vec3d vec3d2 = new Vec3d(mobEntityWithAi.x, mobEntityWithAi.y, mobEntityWithAi.z).subtract(vec3d);
        return TargetFinder.findTarget(mobEntityWithAi, i, j, vec3d2, false, 1.5707963705062866, mobEntityWithAi::getPathfindingFavor);
    }

    @Nullable
    public static Vec3d method_6379(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d) {
        Vec3d vec3d2 = new Vec3d(mobEntityWithAi.x, mobEntityWithAi.y, mobEntityWithAi.z).subtract(vec3d);
        return TargetFinder.findTarget(mobEntityWithAi, i, j, vec3d2);
    }

    @Nullable
    private static Vec3d findTarget(MobEntityWithAi mobEntityWithAi, int i, int j, @Nullable Vec3d vec3d) {
        return TargetFinder.findTarget(mobEntityWithAi, i, j, vec3d, true, 1.5707963705062866, mobEntityWithAi::getPathfindingFavor);
    }

    @Nullable
    private static Vec3d findTarget(MobEntityWithAi mobEntityWithAi, int i, int j, @Nullable Vec3d vec3d, boolean bl, double d, ToDoubleFunction<BlockPos> toDoubleFunction) {
        EntityNavigation entityNavigation = mobEntityWithAi.getNavigation();
        Random random = mobEntityWithAi.getRandom();
        boolean bl2 = mobEntityWithAi.hasPositionTarget() ? mobEntityWithAi.getPositionTarget().isWithinDistance(mobEntityWithAi.getPos(), (double)(mobEntityWithAi.getPositionTargetRange() + (float)i) + 1.0) : false;
        boolean bl3 = false;
        double e = Double.NEGATIVE_INFINITY;
        BlockPos blockPos = new BlockPos(mobEntityWithAi);
        for (int k = 0; k < 10; ++k) {
            double f;
            BlockPos blockPos3;
            BlockPos blockPos2 = TargetFinder.method_6374(random, i, j, vec3d, d);
            if (blockPos2 == null) continue;
            int l = blockPos2.getX();
            int m = blockPos2.getY();
            int n = blockPos2.getZ();
            if (mobEntityWithAi.hasPositionTarget() && i > 1) {
                blockPos3 = mobEntityWithAi.getPositionTarget();
                l = mobEntityWithAi.x > (double)blockPos3.getX() ? (l -= random.nextInt(i / 2)) : (l += random.nextInt(i / 2));
                n = mobEntityWithAi.z > (double)blockPos3.getZ() ? (n -= random.nextInt(i / 2)) : (n += random.nextInt(i / 2));
            }
            blockPos3 = new BlockPos((double)l + mobEntityWithAi.x, (double)m + mobEntityWithAi.y, (double)n + mobEntityWithAi.z);
            if (bl2 && !mobEntityWithAi.isInWalkTargetRange(blockPos3) || !entityNavigation.isValidPosition(blockPos3) || !bl && TargetFinder.isWater(blockPos3 = TargetFinder.method_6372(blockPos3, mobEntityWithAi), mobEntityWithAi) || !((f = toDoubleFunction.applyAsDouble(blockPos3)) > e)) continue;
            e = f;
            blockPos = blockPos3;
            bl3 = true;
        }
        if (bl3) {
            return new Vec3d(blockPos);
        }
        return null;
    }

    @Nullable
    private static BlockPos method_6374(Random random, int i, int j, @Nullable Vec3d vec3d, double d) {
        if (vec3d == null || d >= Math.PI) {
            int k = random.nextInt(2 * i + 1) - i;
            int l = random.nextInt(2 * j + 1) - j;
            int m = random.nextInt(2 * i + 1) - i;
            return new BlockPos(k, l, m);
        }
        double e = MathHelper.atan2(vec3d.z, vec3d.x) - 1.5707963705062866;
        double f = e + (double)(2.0f * random.nextFloat() - 1.0f) * d;
        double g = Math.sqrt(random.nextDouble()) * (double)MathHelper.SQUARE_ROOT_OF_TWO * (double)i;
        double h = -g * Math.sin(f);
        double n = g * Math.cos(f);
        if (Math.abs(h) > (double)i || Math.abs(n) > (double)i) {
            return null;
        }
        int o = random.nextInt(2 * j + 1) - j;
        return new BlockPos(h, (double)o, n);
    }

    private static BlockPos method_6372(BlockPos blockPos, MobEntityWithAi mobEntityWithAi) {
        if (mobEntityWithAi.world.getBlockState(blockPos).getMaterial().isSolid()) {
            BlockPos blockPos2 = blockPos.up();
            while (blockPos2.getY() < mobEntityWithAi.world.getHeight() && mobEntityWithAi.world.getBlockState(blockPos2).getMaterial().isSolid()) {
                blockPos2 = blockPos2.up();
            }
            return blockPos2;
        }
        return blockPos;
    }

    private static boolean isWater(BlockPos blockPos, MobEntityWithAi mobEntityWithAi) {
        return mobEntityWithAi.world.getFluidState(blockPos).matches(FluidTags.WATER);
    }
}

