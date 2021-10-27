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
 * <p>
 * Methods in this class can be used to do pathing for an entity to a random position.
 * Positions are chosen to stay within range of the entity's chosen {@linkplain net.minecraft.entity.mob.MobEntity#getPositionTarget() position target}
 * if applicable, and will be suitably randomized within that constraint.
 */
public class FuzzyTargeting {
    /**
     * Paths to a random reachable position with positive path-finding favorability.
     * 
     * @return chosen position or null if none could be found
     * 
     * @param entity the entity doing the pathing
     * @param horizontalRange the horizontal pathing range (how far the point can be from the entity's starting position on the X or Z range)
     * @param verticalRange the vertical pathing range (how far the point can be from the entity's starting position on the Y range)
     */
    @Nullable
    public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange) {
        return FuzzyTargeting.find(entity, horizontalRange, verticalRange, entity::getPathfindingFavor);
    }

    /**
     * Paths to a random reachable position with positive path-finding favorability computed by a given function.
     * 
     * @return the chosen position or null if none could be found
     * 
     * @param scorer function to compute the path-finding favorability of a candidate position
     * @param verticalRange the vertical pathing range (how far the point can be from the entity's starting position on the Y range)
     * @param horizontalRange the horizontal pathing range (how far the point can be from the entity's starting position on the X or Z range)
     * @param entity the entity doing the pathing
     */
    @Nullable
    public static Vec3d find(PathAwareEntity entity, int horizontalRange, int verticalRange, ToDoubleFunction<BlockPos> scorer) {
        boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
        return FuzzyPositions.guessBest(() -> {
            BlockPos blockPos = FuzzyPositions.localFuzz(entity.getRandom(), horizontalRange, verticalRange);
            BlockPos blockPos2 = FuzzyTargeting.towardTarget(entity, horizontalRange, bl, blockPos);
            if (blockPos2 == null) {
                return null;
            }
            return FuzzyTargeting.validate(entity, blockPos2);
        }, scorer);
    }

    /**
     * Paths to a random reachable position leading towards a given end-point.
     * 
     * @return the chosen position or null if none could be found
     * 
     * @param end the position to path towards
     * @param verticalRange the vertical pathing range (how far the point can be from the entity's starting position on the Y range)
     * @param horizontalRange the horizontal pathing range (how far the point can be from the entity's starting position on the X or Z range)
     */
    @Nullable
    public static Vec3d findTo(PathAwareEntity entity, int horizontalRange, int verticalRange, Vec3d end) {
        Vec3d vec3d = end.subtract(entity.getX(), entity.getY(), entity.getZ());
        boolean bl = NavigationConditions.isPositionTargetInRange(entity, horizontalRange);
        return FuzzyTargeting.findValid(entity, horizontalRange, verticalRange, vec3d, bl);
    }

    /**
     * Paths to a random reachable position leading away from a given starting point.
     * 
     * @return the chosen position or null if none could be found
     * 
     * @param entity the entity doing the pathing
     * @param verticalRange the vertical pathing range (how far the point can be from the entity's starting position on the Y range)
     * @param horizontalRange the horizontal pathing range (how far the point can be from the entity's starting position on the X or Z range)
     * @param start the position to path away from
     */
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
            BlockPos blockPos2 = FuzzyTargeting.towardTarget(entity, horizontalRange, posTargetInRange, blockPos);
            if (blockPos2 == null) {
                return null;
            }
            return FuzzyTargeting.validate(entity, blockPos2);
        });
    }

    /**
     * Checks whether a given position is a valid pathable target.
     * 
     * @return the input position, or null if validation failed
     * 
     * @param pos the candidate position
     * @param entity the entity doing the pathing
     */
    @Nullable
    public static BlockPos validate(PathAwareEntity entity, BlockPos pos) {
        if (NavigationConditions.isWaterAt(entity, pos = FuzzyPositions.upWhile(pos, entity.world.getTopY(), currentPos -> NavigationConditions.isSolidAt(entity, currentPos))) || NavigationConditions.hasPathfindingPenalty(entity, pos)) {
            return null;
        }
        return pos;
    }

    /**
     * Paths to a random reachable position approaching an entity's chosen {@link net.minecraft.entity.mob.MobEntity#getPositionTarget() position target}.
     * 
     * @return the chosen position or null if none could be found
     * 
     * @param entity the entity doing the pathing
     * @param horizontalRange the horizontal pathing range (how far the point can be from the entity's starting position on the X or Z range)
     */
    @Nullable
    public static BlockPos towardTarget(PathAwareEntity entity, int horizontalRange, boolean posTargetInRange, BlockPos relativeInRangePos) {
        BlockPos blockPos = FuzzyPositions.towardTarget(entity, horizontalRange, entity.getRandom(), relativeInRangePos);
        if (NavigationConditions.isHeightInvalid(blockPos, entity) || NavigationConditions.isPositionTargetOutOfWalkRange(posTargetInRange, entity, blockPos) || NavigationConditions.isInvalidPosition(entity.getNavigation(), blockPos)) {
            return null;
        }
        return blockPos;
    }
}

