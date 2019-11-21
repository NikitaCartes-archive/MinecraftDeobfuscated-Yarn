/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai;

import java.util.Random;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class TargetFinder {
    @Nullable
    public static Vec3d findTarget(MobEntityWithAi mobEntityWithAi, int i, int j) {
        return TargetFinder.findTarget(mobEntityWithAi, i, j, 0, null, true, 1.5707963705062866, mobEntityWithAi::getPathfindingFavor, false, 0, 0, true);
    }

    @Nullable
    public static Vec3d findGroundTarget(MobEntityWithAi mobEntityWithAi, int i, int j, int k, @Nullable Vec3d vec3d, double d) {
        return TargetFinder.findTarget(mobEntityWithAi, i, j, k, vec3d, true, d, mobEntityWithAi::getPathfindingFavor, true, 0, 0, false);
    }

    @Nullable
    public static Vec3d findGroundTarget(MobEntityWithAi mobEntityWithAi, int i, int j) {
        return TargetFinder.findGroundTarget(mobEntityWithAi, i, j, mobEntityWithAi::getPathfindingFavor);
    }

    @Nullable
    public static Vec3d findGroundTarget(MobEntityWithAi mobEntityWithAi, int i, int j, ToDoubleFunction<BlockPos> toDoubleFunction) {
        return TargetFinder.findTarget(mobEntityWithAi, i, j, 0, null, false, 0.0, toDoubleFunction, true, 0, 0, true);
    }

    @Nullable
    public static Vec3d findAirTarget(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d, float f, int k, int l) {
        return TargetFinder.findTarget(mobEntityWithAi, i, j, 0, vec3d, false, f, mobEntityWithAi::getPathfindingFavor, true, k, l, true);
    }

    @Nullable
    public static Vec3d findTargetTowards(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d) {
        Vec3d vec3d2 = vec3d.subtract(mobEntityWithAi.getX(), mobEntityWithAi.getY(), mobEntityWithAi.getZ());
        return TargetFinder.findTarget(mobEntityWithAi, i, j, 0, vec3d2, true, 1.5707963705062866, mobEntityWithAi::getPathfindingFavor, false, 0, 0, true);
    }

    @Nullable
    public static Vec3d findTargetTowards(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d, double d) {
        Vec3d vec3d2 = vec3d.subtract(mobEntityWithAi.getX(), mobEntityWithAi.getY(), mobEntityWithAi.getZ());
        return TargetFinder.findTarget(mobEntityWithAi, i, j, 0, vec3d2, true, d, mobEntityWithAi::getPathfindingFavor, false, 0, 0, true);
    }

    @Nullable
    public static Vec3d findGroundTargetTowards(MobEntityWithAi mobEntityWithAi, int i, int j, int k, Vec3d vec3d, double d) {
        Vec3d vec3d2 = vec3d.subtract(mobEntityWithAi.getX(), mobEntityWithAi.getY(), mobEntityWithAi.getZ());
        return TargetFinder.findTarget(mobEntityWithAi, i, j, k, vec3d2, false, d, mobEntityWithAi::getPathfindingFavor, true, 0, 0, false);
    }

    @Nullable
    public static Vec3d findTargetAwayFrom(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d) {
        Vec3d vec3d2 = mobEntityWithAi.getPos().subtract(vec3d);
        return TargetFinder.findTarget(mobEntityWithAi, i, j, 0, vec3d2, true, 1.5707963705062866, mobEntityWithAi::getPathfindingFavor, false, 0, 0, true);
    }

    @Nullable
    public static Vec3d findGroundTargetAwayFrom(MobEntityWithAi mobEntityWithAi, int i, int j, Vec3d vec3d) {
        Vec3d vec3d2 = mobEntityWithAi.getPos().subtract(vec3d);
        return TargetFinder.findTarget(mobEntityWithAi, i, j, 0, vec3d2, false, 1.5707963705062866, mobEntityWithAi::getPathfindingFavor, true, 0, 0, true);
    }

    @Nullable
    private static Vec3d findTarget(MobEntityWithAi mobEntityWithAi, int i, int j, int k, @Nullable Vec3d vec3d, boolean bl, double d, ToDoubleFunction<BlockPos> toDoubleFunction, boolean bl2, int l, int m, boolean bl3) {
        EntityNavigation entityNavigation = mobEntityWithAi.getNavigation();
        Random random = mobEntityWithAi.getRandom();
        boolean bl4 = mobEntityWithAi.hasPositionTarget() ? mobEntityWithAi.getPositionTarget().isWithinDistance(mobEntityWithAi.getPos(), (double)(mobEntityWithAi.getPositionTargetRange() + (float)i) + 1.0) : false;
        boolean bl5 = false;
        double e = Double.NEGATIVE_INFINITY;
        BlockPos blockPos2 = new BlockPos(mobEntityWithAi);
        for (int n = 0; n < 10; ++n) {
            double f;
            PathNodeType pathNodeType;
            BlockPos blockPos3;
            BlockPos blockPos22 = TargetFinder.getRandomOffset(random, i, j, k, vec3d, d);
            if (blockPos22 == null) continue;
            int o = blockPos22.getX();
            int p = blockPos22.getY();
            int q = blockPos22.getZ();
            if (mobEntityWithAi.hasPositionTarget() && i > 1) {
                blockPos3 = mobEntityWithAi.getPositionTarget();
                o = mobEntityWithAi.getX() > (double)blockPos3.getX() ? (o -= random.nextInt(i / 2)) : (o += random.nextInt(i / 2));
                q = mobEntityWithAi.getZ() > (double)blockPos3.getZ() ? (q -= random.nextInt(i / 2)) : (q += random.nextInt(i / 2));
            }
            if ((blockPos3 = new BlockPos((double)o + mobEntityWithAi.getX(), (double)p + mobEntityWithAi.getY(), (double)q + mobEntityWithAi.getZ())).getY() < 0 || blockPos3.getY() > mobEntityWithAi.world.getHeight() || bl4 && !mobEntityWithAi.isInWalkTargetRange(blockPos3) || bl3 && !entityNavigation.isValidPosition(blockPos3)) continue;
            if (bl2) {
                blockPos3 = TargetFinder.findValidPositionAbove(blockPos3, random.nextInt(l + 1) + m, mobEntityWithAi.world.getHeight(), blockPos -> mobEntityWithAi.world.getBlockState((BlockPos)blockPos).getMaterial().isSolid());
            }
            if (!bl && mobEntityWithAi.world.getFluidState(blockPos3).matches(FluidTags.WATER) || mobEntityWithAi.getPathfindingPenalty(pathNodeType = LandPathNodeMaker.method_23476(mobEntityWithAi.world, blockPos3.getX(), blockPos3.getY(), blockPos3.getZ())) != 0.0f || !((f = toDoubleFunction.applyAsDouble(blockPos3)) > e)) continue;
            e = f;
            blockPos2 = blockPos3;
            bl5 = true;
        }
        if (bl5) {
            return new Vec3d(blockPos2);
        }
        return null;
    }

    @Nullable
    private static BlockPos getRandomOffset(Random random, int i, int j, int k, @Nullable Vec3d vec3d, double d) {
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

    static BlockPos findValidPositionAbove(BlockPos blockPos, int i, int j, Predicate<BlockPos> predicate) {
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
}

