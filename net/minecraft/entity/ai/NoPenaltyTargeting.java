/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai;

import net.minecraft.entity.ai.FuzzyPositions;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

/**
 * Similar to {@link FuzzyTargeting}, but the positions this class' utility methods
 * find never have pathfinding penalties.
 */
public class NoPenaltyTargeting {
    @Nullable
    public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange) {
        boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
        return FuzzyPositions.guessBestPathTarget(entity, () -> {
            BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange);
            return NoPenaltyTargeting.tryMake(entity, horizontalRange, bl, blockPos);
        });
    }

    @Nullable
    public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange, Vec3d end, double angleRange) {
        Vec3d vec3d = end.subtract(entity.getX(), entity.getY(), entity.getZ());
        boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
        return FuzzyPositions.guessBestPathTarget(entity, () -> {
            BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange, 0, vec3d.x, vec3d.z, angleRange);
            if (blockPos == null) {
                return null;
            }
            return NoPenaltyTargeting.tryMake(entity, horizontalRange, bl, blockPos);
        });
    }

    @Nullable
    public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange, Vec3d direction) {
        Vec3d vec3d = entity.getPos().subtract(direction);
        boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
        return FuzzyPositions.guessBestPathTarget(entity, () -> {
            BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange, 0, vec3d.x, vec3d.z, 1.5707963705062866);
            if (blockPos == null) {
                return null;
            }
            return NoPenaltyTargeting.tryMake(entity, horizontalRange, bl, blockPos);
        });
    }

    @Nullable
    private static BlockPos tryMake(PathAwareEntity entity, int horizontalRange, boolean posTargetInRange, BlockPos fuzz) {
        BlockPos blockPos = FuzzyPositions.towardTarget(entity, horizontalRange, entity.getRandom(), fuzz);
        if (NavigationConditions.isHeightInvalid(blockPos, entity) || NavigationConditions.isPositionTargetOutOfWalkRange(posTargetInRange, entity, blockPos) || NavigationConditions.isInvalidPosition(entity.getNavigation(), blockPos) || NavigationConditions.hasPathfindingPenalty(entity, blockPos)) {
            return null;
        }
        return blockPos;
    }
}

