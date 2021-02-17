/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai;

import java.util.function.ToDoubleFunction;
import net.minecraft.entity.ai.FuzzyPositions;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

/**
 * Path targeting utilities using fuzzy, or approximated, positions from
 * {@link FuzzyPositions}.
 */
public class FuzzyTargeting {
    @Nullable
    public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange) {
        return FuzzyTargeting.find(entity, horizontalRange, verticalRange, entity::getPathfindingFavor);
    }

    @Nullable
    public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange, ToDoubleFunction<BlockPos> scorer) {
        boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
        return FuzzyPositions.guessBest(() -> {
            BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange);
            BlockPos blockPos2 = FuzzyTargeting.tryMake(entity, horizontalRange, bl, blockPos);
            if (blockPos2 == null) {
                return null;
            }
            return FuzzyTargeting.validate(entity, blockPos2);
        }, scorer);
    }

    @Nullable
    public static Vec3d findTo(PathAwareEntity entity, int horizontalRange, int verticalRange, Vec3d end) {
        Vec3d vec3d = end.subtract(entity.getX(), entity.getY(), entity.getZ());
        boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
        return FuzzyTargeting.findValid(entity, horizontalRange, verticalRange, vec3d, bl);
    }

    @Nullable
    public static Vec3d findFrom(PathAwareEntity entity, int horizontalRange, int verticalRange, Vec3d start) {
        Vec3d vec3d = entity.getPos().subtract(start);
        boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
        return FuzzyTargeting.findValid(entity, horizontalRange, verticalRange, vec3d, bl);
    }

    @Nullable
    private static Vec3d findValid(PathAwareEntity entity, int horizontalRange, int verticalRange, Vec3d direction, boolean posTargetInRange) {
        return FuzzyPositions.guessBestPathTarget(entity, () -> {
            BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange, 0, vec3d.x, vec3d.z, 1.5707963705062866);
            if (blockPos == null) {
                return null;
            }
            BlockPos blockPos2 = FuzzyTargeting.tryMake(entity, horizontalRange, posTargetInRange, blockPos);
            if (blockPos2 == null) {
                return null;
            }
            return FuzzyTargeting.validate(entity, blockPos2);
        });
    }

    @Nullable
    public static BlockPos validate(PathAwareEntity entity, BlockPos pos) {
        if (NavigationConditions.isWaterAt(entity, pos = FuzzyPositions.upWhile(pos, entity.world.getTopY(), currentPos -> NavigationConditions.isSolidAt(entity, currentPos))) || NavigationConditions.hasPathfindingPenalty(entity, pos)) {
            return null;
        }
        return pos;
    }

    @Nullable
    public static BlockPos tryMake(PathAwareEntity entity, int horizontalRange, boolean posTargetInRange, BlockPos relativeInRangePos) {
        BlockPos blockPos = FuzzyPositions.towardTarget(entity, horizontalRange, entity.getRandom(), relativeInRangePos);
        if (NavigationConditions.isHeightInvalid(blockPos, entity) || NavigationConditions.isPositionTargetOutOfWalkRange(posTargetInRange, entity, blockPos) || NavigationConditions.isInvalidPosition(entity.getNavigation(), blockPos)) {
            return null;
        }
        return blockPos;
    }
}

