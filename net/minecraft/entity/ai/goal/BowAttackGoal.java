/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;

public class BowAttackGoal<T extends HostileEntity>
extends Goal {
    private final T actor;
    private final double speed;
    private int attackInterval;
    private final float squaredRange;
    private int cooldown = -1;
    private int field_6572;
    private boolean field_6573;
    private boolean field_6571;
    private int field_6568 = -1;

    public BowAttackGoal(T hostileEntity, double d, int i, float f) {
        this.actor = hostileEntity;
        this.speed = d;
        this.attackInterval = i;
        this.squaredRange = f * f;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    public void setAttackInterval(int i) {
        this.attackInterval = i;
    }

    @Override
    public boolean canStart() {
        if (((MobEntity)this.actor).getTarget() == null) {
            return false;
        }
        return this.isHoldingBow();
    }

    protected boolean isHoldingBow() {
        return ((MobEntity)this.actor).isHolding(Items.BOW);
    }

    @Override
    public boolean shouldContinue() {
        return (this.canStart() || !((MobEntity)this.actor).getNavigation().isIdle()) && this.isHoldingBow();
    }

    @Override
    public void start() {
        super.start();
        ((MobEntity)this.actor).setAttacking(true);
    }

    @Override
    public void stop() {
        super.stop();
        ((MobEntity)this.actor).setAttacking(false);
        this.field_6572 = 0;
        this.cooldown = -1;
        ((LivingEntity)this.actor).clearActiveItem();
    }

    @Override
    public void tick() {
        boolean bl2;
        LivingEntity livingEntity = ((MobEntity)this.actor).getTarget();
        if (livingEntity == null) {
            return;
        }
        double d = ((Entity)this.actor).squaredDistanceTo(livingEntity.x, livingEntity.getBoundingBox().y1, livingEntity.z);
        boolean bl = ((MobEntity)this.actor).getVisibilityCache().canSee(livingEntity);
        boolean bl3 = bl2 = this.field_6572 > 0;
        if (bl != bl2) {
            this.field_6572 = 0;
        }
        this.field_6572 = bl ? ++this.field_6572 : --this.field_6572;
        if (d > (double)this.squaredRange || this.field_6572 < 20) {
            ((MobEntity)this.actor).getNavigation().startMovingTo(livingEntity, this.speed);
            this.field_6568 = -1;
        } else {
            ((MobEntity)this.actor).getNavigation().stop();
            ++this.field_6568;
        }
        if (this.field_6568 >= 20) {
            if ((double)((LivingEntity)this.actor).getRandom().nextFloat() < 0.3) {
                boolean bl4 = this.field_6573 = !this.field_6573;
            }
            if ((double)((LivingEntity)this.actor).getRandom().nextFloat() < 0.3) {
                this.field_6571 = !this.field_6571;
            }
            this.field_6568 = 0;
        }
        if (this.field_6568 > -1) {
            if (d > (double)(this.squaredRange * 0.75f)) {
                this.field_6571 = false;
            } else if (d < (double)(this.squaredRange * 0.25f)) {
                this.field_6571 = true;
            }
            ((MobEntity)this.actor).getMoveControl().strafeTo(this.field_6571 ? -0.5f : 0.5f, this.field_6573 ? 0.5f : -0.5f);
            ((MobEntity)this.actor).lookAtEntity(livingEntity, 30.0f, 30.0f);
        } else {
            ((MobEntity)this.actor).getLookControl().lookAt(livingEntity, 30.0f, 30.0f);
        }
        if (((LivingEntity)this.actor).isUsingItem()) {
            int i;
            if (!bl && this.field_6572 < -60) {
                ((LivingEntity)this.actor).clearActiveItem();
            } else if (bl && (i = ((LivingEntity)this.actor).getItemUseTime()) >= 20) {
                ((LivingEntity)this.actor).clearActiveItem();
                ((RangedAttackMob)this.actor).attack(livingEntity, BowItem.getPullProgress(i));
                this.cooldown = this.attackInterval;
            }
        } else if (--this.cooldown <= 0 && this.field_6572 >= -60) {
            ((LivingEntity)this.actor).setCurrentHand(ProjectileUtil.getHandPossiblyHolding(this.actor, Items.BOW));
        }
    }
}

