/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public abstract class TrackTargetGoal
extends Goal {
    protected final MobEntity mob;
    protected final boolean checkVisibility;
    private final boolean checkCanNavigate;
    private int canNavigateFlag;
    private int checkCanNavigateCooldown;
    private int timeWithoutVisibility;
    protected LivingEntity target;
    protected int maxTimeWithoutVisibility = 60;

    public TrackTargetGoal(MobEntity mobEntity, boolean bl) {
        this(mobEntity, bl, false);
    }

    public TrackTargetGoal(MobEntity mobEntity, boolean bl, boolean bl2) {
        this.mob = mobEntity;
        this.checkVisibility = bl;
        this.checkCanNavigate = bl2;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            livingEntity = this.target;
        }
        if (livingEntity == null) {
            return false;
        }
        if (!livingEntity.isAlive()) {
            return false;
        }
        AbstractTeam abstractTeam = this.mob.getScoreboardTeam();
        AbstractTeam abstractTeam2 = livingEntity.getScoreboardTeam();
        if (abstractTeam != null && abstractTeam2 == abstractTeam) {
            return false;
        }
        double d = this.getFollowRange();
        if (this.mob.squaredDistanceTo(livingEntity) > d * d) {
            return false;
        }
        if (this.checkVisibility) {
            if (this.mob.getVisibilityCache().canSee(livingEntity)) {
                this.timeWithoutVisibility = 0;
            } else if (++this.timeWithoutVisibility > this.maxTimeWithoutVisibility) {
                return false;
            }
        }
        if (livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.invulnerable) {
            return false;
        }
        this.mob.setTarget(livingEntity);
        return true;
    }

    protected double getFollowRange() {
        EntityAttributeInstance entityAttributeInstance = this.mob.getAttributeInstance(EntityAttributes.FOLLOW_RANGE);
        return entityAttributeInstance == null ? 16.0 : entityAttributeInstance.getValue();
    }

    @Override
    public void start() {
        this.canNavigateFlag = 0;
        this.checkCanNavigateCooldown = 0;
        this.timeWithoutVisibility = 0;
    }

    @Override
    public void stop() {
        this.mob.setTarget(null);
        this.target = null;
    }

    protected boolean canTrack(@Nullable LivingEntity livingEntity, TargetPredicate targetPredicate) {
        if (livingEntity == null) {
            return false;
        }
        if (!targetPredicate.test(this.mob, livingEntity)) {
            return false;
        }
        if (!this.mob.isInWalkTargetRange(new BlockPos(livingEntity))) {
            return false;
        }
        if (this.checkCanNavigate) {
            if (--this.checkCanNavigateCooldown <= 0) {
                this.canNavigateFlag = 0;
            }
            if (this.canNavigateFlag == 0) {
                int n = this.canNavigateFlag = this.canNavigateToEntity(livingEntity) ? 1 : 2;
            }
            if (this.canNavigateFlag == 2) {
                return false;
            }
        }
        return true;
    }

    private boolean canNavigateToEntity(LivingEntity livingEntity) {
        int j;
        this.checkCanNavigateCooldown = 10 + this.mob.getRandom().nextInt(5);
        Path path = this.mob.getNavigation().findPathTo(livingEntity, 0);
        if (path == null) {
            return false;
        }
        PathNode pathNode = path.getEnd();
        if (pathNode == null) {
            return false;
        }
        int i = pathNode.x - MathHelper.floor(livingEntity.getX());
        return (double)(i * i + (j = pathNode.z - MathHelper.floor(livingEntity.getZ())) * j) <= 2.25;
    }

    public TrackTargetGoal setMaxTimeWithoutVisibility(int i) {
        this.maxTimeWithoutVisibility = i;
        return this;
    }
}

