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
    public static Vec3d findTarget(MobEntityWithAi mob, int maxHorizontalDistance, int maxVerticalDistance) {
        return TargetFinder.findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, null, true, 1.5707963705062866, mob::getPathfindingFavor, false, 0, 0, true);
    }

    @Nullable
    public static Vec3d findGroundTarget(MobEntityWithAi mob, int maxHorizontalDistance, int maxVerticalDistance, int preferredYDifference, @Nullable Vec3d preferredAngle, double maxAngleDifference) {
        return TargetFinder.findTarget(mob, maxHorizontalDistance, maxVerticalDistance, preferredYDifference, preferredAngle, true, maxAngleDifference, mob::getPathfindingFavor, true, 0, 0, false);
    }

    @Nullable
    public static Vec3d findGroundTarget(MobEntityWithAi mob, int maxHorizontalDistance, int maxVerticalDistance) {
        return TargetFinder.findGroundTarget(mob, maxHorizontalDistance, maxVerticalDistance, mob::getPathfindingFavor);
    }

    @Nullable
    public static Vec3d findGroundTarget(MobEntityWithAi mob, int maxHorizontalDistance, int maxVerticalDistance, ToDoubleFunction<BlockPos> pathfindingFavor) {
        return TargetFinder.findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, null, false, 0.0, pathfindingFavor, true, 0, 0, true);
    }

    @Nullable
    public static Vec3d findAirTarget(MobEntityWithAi mob, int maxHorizontalDistance, int maxVerticalDistance, Vec3d preferredAngle, float maxAngleDifference, int distanceAboveGroundRange, int minDistanceAboveGround) {
        return TargetFinder.findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, preferredAngle, false, maxAngleDifference, mob::getPathfindingFavor, true, distanceAboveGroundRange, minDistanceAboveGround, true);
    }

    @Nullable
    public static Vec3d findTargetTowards(MobEntityWithAi mob, int maxHorizontalDistance, int maxVerticalDistance, Vec3d pos) {
        Vec3d vec3d = pos.subtract(mob.getX(), mob.getY(), mob.getZ());
        return TargetFinder.findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, vec3d, true, 1.5707963705062866, mob::getPathfindingFavor, false, 0, 0, true);
    }

    @Nullable
    public static Vec3d findTargetTowards(MobEntityWithAi mob, int maxHorizontalDistance, int maxVerticalDistance, Vec3d pos, double maxAngleDifference) {
        Vec3d vec3d = pos.subtract(mob.getX(), mob.getY(), mob.getZ());
        return TargetFinder.findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, vec3d, true, maxAngleDifference, mob::getPathfindingFavor, false, 0, 0, true);
    }

    @Nullable
    public static Vec3d findGroundTargetTowards(MobEntityWithAi mob, int maxHorizontalDistance, int maxVerticalDistance, int preferredYDifference, Vec3d pos, double maxAngleDifference) {
        Vec3d vec3d = pos.subtract(mob.getX(), mob.getY(), mob.getZ());
        return TargetFinder.findTarget(mob, maxHorizontalDistance, maxVerticalDistance, preferredYDifference, vec3d, false, maxAngleDifference, mob::getPathfindingFavor, true, 0, 0, false);
    }

    @Nullable
    public static Vec3d findTargetAwayFrom(MobEntityWithAi mob, int maxHorizontalDistance, int maxVerticalDistance, Vec3d pos) {
        Vec3d vec3d = mob.getPos().subtract(pos);
        return TargetFinder.findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, vec3d, true, 1.5707963705062866, mob::getPathfindingFavor, false, 0, 0, true);
    }

    @Nullable
    public static Vec3d findGroundTargetAwayFrom(MobEntityWithAi mob, int maxHorizontalDistance, int maxVerticalDistance, Vec3d pos) {
        Vec3d vec3d = mob.getPos().subtract(pos);
        return TargetFinder.findTarget(mob, maxHorizontalDistance, maxVerticalDistance, 0, vec3d, false, 1.5707963705062866, mob::getPathfindingFavor, true, 0, 0, true);
    }

    @Nullable
    private static Vec3d findTarget(MobEntityWithAi mob, int maxHorizontalDistance, int maxVerticalDistance, int preferredYDifference, @Nullable Vec3d preferredAngle, boolean notInWater, double maxAngleDifference, ToDoubleFunction<BlockPos> favorProvider, boolean aboveGround, int distanceAboveGroundRange, int minDistanceAboveGround, boolean validPositionsOnly) {
        EntityNavigation entityNavigation = mob.getNavigation();
        Random random = mob.getRandom();
        boolean bl = mob.hasPositionTarget() ? mob.getPositionTarget().isWithinDistance(mob.getPos(), (double)(mob.getPositionTargetRange() + (float)maxHorizontalDistance) + 1.0) : false;
        boolean bl2 = false;
        double d = Double.NEGATIVE_INFINITY;
        BlockPos blockPos2 = mob.getSenseCenterPos();
        for (int i = 0; i < 10; ++i) {
            double e;
            PathNodeType pathNodeType;
            BlockPos blockPos3;
            BlockPos blockPos22 = TargetFinder.getRandomOffset(random, maxHorizontalDistance, maxVerticalDistance, preferredYDifference, preferredAngle, maxAngleDifference);
            if (blockPos22 == null) continue;
            int j = blockPos22.getX();
            int k = blockPos22.getY();
            int l = blockPos22.getZ();
            if (mob.hasPositionTarget() && maxHorizontalDistance > 1) {
                blockPos3 = mob.getPositionTarget();
                j = mob.getX() > (double)blockPos3.getX() ? (j -= random.nextInt(maxHorizontalDistance / 2)) : (j += random.nextInt(maxHorizontalDistance / 2));
                l = mob.getZ() > (double)blockPos3.getZ() ? (l -= random.nextInt(maxHorizontalDistance / 2)) : (l += random.nextInt(maxHorizontalDistance / 2));
            }
            if ((blockPos3 = new BlockPos((double)j + mob.getX(), (double)k + mob.getY(), (double)l + mob.getZ())).getY() < 0 || blockPos3.getY() > mob.world.getHeight() || bl && !mob.isInWalkTargetRange(blockPos3) || validPositionsOnly && !entityNavigation.isValidPosition(blockPos3)) continue;
            if (aboveGround) {
                blockPos3 = TargetFinder.findValidPositionAbove(blockPos3, random.nextInt(distanceAboveGroundRange + 1) + minDistanceAboveGround, mob.world.getHeight(), blockPos -> mobEntityWithAi.world.getBlockState((BlockPos)blockPos).getMaterial().isSolid());
            }
            if (!notInWater && mob.world.getFluidState(blockPos3).matches(FluidTags.WATER) || mob.getPathfindingPenalty(pathNodeType = LandPathNodeMaker.getLandNodeType(mob.world, blockPos3.getX(), blockPos3.getY(), blockPos3.getZ())) != 0.0f || !((e = favorProvider.applyAsDouble(blockPos3)) > d)) continue;
            d = e;
            blockPos2 = blockPos3;
            bl2 = true;
        }
        if (bl2) {
            return Vec3d.method_24955(blockPos2);
        }
        return null;
    }

    @Nullable
    private static BlockPos getRandomOffset(Random random, int maxHorizontalDistance, int maxVerticalDistance, int preferredYDifference, @Nullable Vec3d preferredAngle, double maxAngleDifference) {
        if (preferredAngle == null || maxAngleDifference >= Math.PI) {
            int i = random.nextInt(2 * maxHorizontalDistance + 1) - maxHorizontalDistance;
            int j = random.nextInt(2 * maxVerticalDistance + 1) - maxVerticalDistance + preferredYDifference;
            int k = random.nextInt(2 * maxHorizontalDistance + 1) - maxHorizontalDistance;
            return new BlockPos(i, j, k);
        }
        double d = MathHelper.atan2(preferredAngle.z, preferredAngle.x) - 1.5707963705062866;
        double e = d + (double)(2.0f * random.nextFloat() - 1.0f) * maxAngleDifference;
        double f = Math.sqrt(random.nextDouble()) * (double)MathHelper.SQUARE_ROOT_OF_TWO * (double)maxHorizontalDistance;
        double g = -f * Math.sin(e);
        double h = f * Math.cos(e);
        if (Math.abs(g) > (double)maxHorizontalDistance || Math.abs(h) > (double)maxHorizontalDistance) {
            return null;
        }
        int l = random.nextInt(2 * maxVerticalDistance + 1) - maxVerticalDistance + preferredYDifference;
        return new BlockPos(g, (double)l, h);
    }

    static BlockPos findValidPositionAbove(BlockPos pos, int minDistanceAboveIllegal, int maxOffset, Predicate<BlockPos> isIllegalPredicate) {
        if (minDistanceAboveIllegal < 0) {
            throw new IllegalArgumentException("aboveSolidAmount was " + minDistanceAboveIllegal + ", expected >= 0");
        }
        if (isIllegalPredicate.test(pos)) {
            BlockPos blockPos3;
            BlockPos blockPos = pos.up();
            while (blockPos.getY() < maxOffset && isIllegalPredicate.test(blockPos)) {
                blockPos = blockPos.up();
            }
            BlockPos blockPos2 = blockPos;
            while (blockPos2.getY() < maxOffset && blockPos2.getY() - blockPos.getY() < minDistanceAboveIllegal && !isIllegalPredicate.test(blockPos3 = blockPos2.up())) {
                blockPos2 = blockPos3;
            }
            return blockPos2;
        }
        return pos;
    }
}

