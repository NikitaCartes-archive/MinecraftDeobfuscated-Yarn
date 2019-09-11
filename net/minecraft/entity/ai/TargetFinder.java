/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai;

import java.util.Random;
import java.util.function.Predicate;
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
    public static Vec3d method_21757(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d, float f, int k, int l) {
        return TargetFinder.findTarget(mobEntityWithAi, i, j, 0, vec3d, true, f, mobEntityWithAi::getPathfindingFavor, true, blockPos -> mobEntityWithAi.getNavigation().isValidPosition((BlockPos)blockPos), k, l, true);
    }

    @Nullable
    public static Vec3d findGroundTarget(MobEntityWithAi mobEntityWithAi, int i, int j) {
        return TargetFinder.findGroundTarget(mobEntityWithAi, i, j, mobEntityWithAi::getPathfindingFavor);
    }

    @Nullable
    public static Vec3d findGroundTarget(MobEntityWithAi mobEntityWithAi, int i, int j, ToDoubleFunction<BlockPos> toDoubleFunction) {
        return TargetFinder.method_21758(mobEntityWithAi, i, j, null, false, 0.0, toDoubleFunction);
    }

    @Nullable
    public static Vec3d findTargetTowards(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d) {
        Vec3d vec3d2 = vec3d.subtract(mobEntityWithAi.x, mobEntityWithAi.y, mobEntityWithAi.z);
        return TargetFinder.findTarget(mobEntityWithAi, i, j, vec3d2);
    }

    @Nullable
    public static Vec3d findTargetTowards(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d, double d) {
        Vec3d vec3d2 = vec3d.subtract(mobEntityWithAi.x, mobEntityWithAi.y, mobEntityWithAi.z);
        return TargetFinder.method_21758(mobEntityWithAi, i, j, vec3d2, true, d, mobEntityWithAi::getPathfindingFavor);
    }

    @Nullable
    public static Vec3d findGroundTargetAwayFrom(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d) {
        Vec3d vec3d2 = new Vec3d(mobEntityWithAi.x, mobEntityWithAi.y, mobEntityWithAi.z).subtract(vec3d);
        return TargetFinder.method_21758(mobEntityWithAi, i, j, vec3d2, false, 1.5707963705062866, mobEntityWithAi::getPathfindingFavor);
    }

    @Nullable
    public static Vec3d findTargetAwayFrom(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d) {
        Vec3d vec3d2 = new Vec3d(mobEntityWithAi.x, mobEntityWithAi.y, mobEntityWithAi.z).subtract(vec3d);
        return TargetFinder.findTarget(mobEntityWithAi, i, j, vec3d2);
    }

    @Nullable
    public static Vec3d method_21756(MobEntityWithAi mobEntityWithAi, int i, int j, int k, @Nullable Vec3d vec3d, double d) {
        return TargetFinder.findTarget(mobEntityWithAi, i, j, k, vec3d, true, d, mobEntityWithAi::getPathfindingFavor, false, blockPos -> false, 0, 0, false);
    }

    @Nullable
    private static Vec3d findTarget(MobEntityWithAi mobEntityWithAi, int i, int j, @Nullable Vec3d vec3d) {
        return TargetFinder.method_21758(mobEntityWithAi, i, j, vec3d, true, 1.5707963705062866, mobEntityWithAi::getPathfindingFavor);
    }

    @Nullable
    private static Vec3d method_21758(MobEntityWithAi mobEntityWithAi, int i, int j, @Nullable Vec3d vec3d, boolean bl, double d, ToDoubleFunction<BlockPos> toDoubleFunction) {
        return TargetFinder.findTarget(mobEntityWithAi, i, j, 0, vec3d, bl, d, toDoubleFunction, !bl, blockPos -> mobEntityWithAi.world.getBlockState((BlockPos)blockPos).getMaterial().isSolid(), 0, 0, true);
    }

    @Nullable
    private static Vec3d findTarget(MobEntityWithAi mobEntityWithAi, int i, int j, int k, @Nullable Vec3d vec3d, boolean bl, double d, ToDoubleFunction<BlockPos> toDoubleFunction, boolean bl2, Predicate<BlockPos> predicate, int l, int m, boolean bl3) {
        EntityNavigation entityNavigation = mobEntityWithAi.getNavigation();
        Random random = mobEntityWithAi.getRand();
        boolean bl4 = mobEntityWithAi.hasPositionTarget() ? mobEntityWithAi.getPositionTarget().isWithinDistance(mobEntityWithAi.getPos(), (double)(mobEntityWithAi.getPositionTargetRange() + (float)i) + 1.0) : false;
        boolean bl5 = false;
        double e = Double.NEGATIVE_INFINITY;
        BlockPos blockPos = new BlockPos(mobEntityWithAi);
        for (int n = 0; n < 10; ++n) {
            double f;
            BlockPos blockPos3;
            BlockPos blockPos2 = TargetFinder.method_6374(random, i, j, k, vec3d, d);
            if (blockPos2 == null) continue;
            int o = blockPos2.getX();
            int p = blockPos2.getY();
            int q = blockPos2.getZ();
            if (mobEntityWithAi.hasPositionTarget() && i > 1) {
                blockPos3 = mobEntityWithAi.getPositionTarget();
                o = mobEntityWithAi.x > (double)blockPos3.getX() ? (o -= random.nextInt(i / 2)) : (o += random.nextInt(i / 2));
                q = mobEntityWithAi.z > (double)blockPos3.getZ() ? (q -= random.nextInt(i / 2)) : (q += random.nextInt(i / 2));
            }
            blockPos3 = new BlockPos((double)o + mobEntityWithAi.x, (double)p + mobEntityWithAi.y, (double)q + mobEntityWithAi.z);
            if (bl4 && !mobEntityWithAi.isInWalkTargetRange(blockPos3) || bl3 && !entityNavigation.isValidPosition(blockPos3)) continue;
            if (bl2) {
                blockPos3 = TargetFinder.method_21761(blockPos3, random.nextInt(l + 1) + m, mobEntityWithAi.world.getHeight(), predicate);
            }
            if (!bl && TargetFinder.isWater(blockPos3, mobEntityWithAi) || !((f = toDoubleFunction.applyAsDouble(blockPos3)) > e)) continue;
            e = f;
            blockPos = blockPos3;
            bl5 = true;
        }
        if (bl5) {
            return new Vec3d(blockPos);
        }
        return null;
    }

    @Nullable
    private static BlockPos method_6374(Random random, int i, int j, int k, @Nullable Vec3d vec3d, double d) {
        if (vec3d == null || d >= Math.PI) {
            int l = random.nextInt(2 * i + 1) - i;
            int m = random.nextInt(2 * j + 1) - j + k;
            int n = random.nextInt(2 * i + 1) - i;
            return new BlockPos(l, m, n);
        }
        double e = MathHelper.atan2(vec3d.z, vec3d.x) - 1.5707963705062866;
        double f = e + (double)(2.0f * random.nextFloat() - 1.0f) * d;
        double g = Math.sqrt(random.nextDouble()) * (double)MathHelper.SQUARE_ROOT_OF_TWO * (double)i;
        double h = -g * Math.sin(f);
        double o = g * Math.cos(f);
        if (Math.abs(h) > (double)i || Math.abs(o) > (double)i) {
            return null;
        }
        int p = random.nextInt(2 * j + 1) - j + k;
        return new BlockPos(h, (double)p, o);
    }

    static BlockPos method_21761(BlockPos blockPos, int i, int j, Predicate<BlockPos> predicate) {
        if (i < 0) {
            throw new IllegalArgumentException("aboveSolidAmount was " + i + ", expected >= 0");
        }
        if (predicate.test(blockPos)) {
            BlockPos blockPos4;
            BlockPos blockPos2 = blockPos.up();
            while (blockPos2.getY() < j && predicate.test(blockPos2)) {
                blockPos2 = blockPos2.up();
            }
            BlockPos blockPos3 = blockPos2;
            while (blockPos3.getY() < j && blockPos3.getY() - blockPos2.getY() < i && !predicate.test(blockPos4 = blockPos3.up())) {
                blockPos3 = blockPos4;
            }
            return blockPos3;
        }
        return blockPos;
    }

    private static boolean isWater(BlockPos blockPos, MobEntityWithAi mobEntityWithAi) {
        return mobEntityWithAi.world.getFluidState(blockPos).matches(FluidTags.WATER);
    }
}

