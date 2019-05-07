/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.passive.TameableEntity;

public class AttackWithOwnerGoal
extends TrackTargetGoal {
    private final TameableEntity tameable;
    private LivingEntity attacking;
    private int lastAttackTime;

    public AttackWithOwnerGoal(TameableEntity tameableEntity) {
        super(tameableEntity, false);
        this.tameable = tameableEntity;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        if (!this.tameable.isTamed() || this.tameable.isSitting()) {
            return false;
        }
        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity == null) {
            return false;
        }
        this.attacking = livingEntity.getAttacking();
        int i = livingEntity.getLastAttackTime();
        return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT) && this.tameable.canAttackWithOwner(this.attacking, livingEntity);
    }

    @Override
    public void start() {
        this.mob.setTarget(this.attacking);
        LivingEntity livingEntity = this.tameable.getOwner();
        if (livingEntity != null) {
            this.lastAttackTime = livingEntity.getLastAttackTime();
        }
        super.start();
    }
}

