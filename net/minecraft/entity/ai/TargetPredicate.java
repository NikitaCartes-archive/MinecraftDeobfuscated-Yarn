/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;

public class TargetPredicate {
    public static final TargetPredicate DEFAULT = new TargetPredicate();
    private double baseMaxDistance = -1.0;
    private boolean includeInvulnerable;
    private boolean includeTeammates;
    private boolean includeHidden;
    private boolean ignoreEntityTargetRules;
    private boolean useDistanceScalingFactor = true;
    private Predicate<LivingEntity> predicate;

    public TargetPredicate setBaseMaxDistance(double baseMaxDistance) {
        this.baseMaxDistance = baseMaxDistance;
        return this;
    }

    public TargetPredicate includeInvulnerable() {
        this.includeInvulnerable = true;
        return this;
    }

    public TargetPredicate includeTeammates() {
        this.includeTeammates = true;
        return this;
    }

    public TargetPredicate includeHidden() {
        this.includeHidden = true;
        return this;
    }

    public TargetPredicate ignoreEntityTargetRules() {
        this.ignoreEntityTargetRules = true;
        return this;
    }

    public TargetPredicate ignoreDistanceScalingFactor() {
        this.useDistanceScalingFactor = false;
        return this;
    }

    public TargetPredicate setPredicate(@Nullable Predicate<LivingEntity> predicate) {
        this.predicate = predicate;
        return this;
    }

    public boolean test(@Nullable LivingEntity baseEntity, LivingEntity targetEntity) {
        if (baseEntity == targetEntity) {
            return false;
        }
        if (targetEntity.isSpectator()) {
            return false;
        }
        if (!targetEntity.isAlive()) {
            return false;
        }
        if (!this.includeInvulnerable && targetEntity.isInvulnerable()) {
            return false;
        }
        if (this.predicate != null && !this.predicate.test(targetEntity)) {
            return false;
        }
        if (baseEntity != null) {
            if (!this.ignoreEntityTargetRules) {
                if (!baseEntity.canTarget(targetEntity)) {
                    return false;
                }
                if (!baseEntity.canTarget(targetEntity.getType())) {
                    return false;
                }
            }
            if (!this.includeTeammates && baseEntity.isTeammate(targetEntity)) {
                return false;
            }
            if (this.baseMaxDistance > 0.0) {
                double d = this.useDistanceScalingFactor ? targetEntity.getAttackDistanceScalingFactor(baseEntity) : 1.0;
                double e = this.baseMaxDistance * d;
                double f = baseEntity.squaredDistanceTo(targetEntity.getX(), targetEntity.getY(), targetEntity.getZ());
                if (f > e * e) {
                    return false;
                }
            }
            if (!this.includeHidden && baseEntity instanceof MobEntity && !((MobEntity)baseEntity).getVisibilityCache().canSee(targetEntity)) {
                return false;
            }
        }
        return true;
    }
}

