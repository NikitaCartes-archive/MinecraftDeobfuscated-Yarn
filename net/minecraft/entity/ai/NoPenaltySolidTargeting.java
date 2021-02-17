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
 * find never have pathfinding penalties and are always on solid blocks.
 */
public class NoPenaltySolidTargeting {
    @Nullable
    public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange, int startHeight, double xDirection, double zDirection, double rangeAngle) {
        boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
        return FuzzyPositions.guessBestPathTarget(entity, () -> NoPenaltySolidTargeting.tryMake(entity, horizontalRange, verticalRange, startHeight, xDirection, zDirection, rangeAngle, bl));
    }

    @Nullable
    public static BlockPos tryMake(PathAwareEntity entity, int horizontalRange, int verticalRange, int startHeight, double xDirection, double zDirection, double rangeAngle, boolean posTargetInRange) {
        BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange, startHeight, xDirection, zDirection, rangeAngle);
        if (blockPos == null) {
            return null;
        }
        BlockPos blockPos2 = FuzzyPositions.towardTarget(entity, horizontalRange, entity.getRandom(), blockPos);
        if (NavigationConditions.isHeightInvalid(blockPos2, entity) || NavigationConditions.isPositionTargetOutOfWalkRange(posTargetInRange, entity, blockPos2)) {
            return null;
        }
        if (NavigationConditions.hasPathfindingPenalty(entity, blockPos2 = FuzzyPositions.upWhile(blockPos2, entity.world.getTopY(), pos -> NavigationConditions.isSolidAt(entity, pos)))) {
            return null;
        }
        return blockPos2;
    }
}

