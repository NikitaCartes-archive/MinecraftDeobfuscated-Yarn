/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.world.World;

public abstract class IllagerEntity
extends RaiderEntity {
    protected IllagerEntity(EntityType<? extends IllagerEntity> entityType, World world) {
        super((EntityType<? extends RaiderEntity>)entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.ILLAGER;
    }

    public State getState() {
        return State.CROSSED;
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        if (target instanceof MerchantEntity && target.isBaby()) {
            return false;
        }
        return super.canTarget(target);
    }

    public static enum State {
        CROSSED,
        ATTACKING,
        SPELLCASTING,
        BOW_AND_ARROW,
        CROSSBOW_HOLD,
        CROSSBOW_CHARGE,
        CELEBRATING,
        NEUTRAL;

    }

    protected class LongDoorInteractGoal
    extends net.minecraft.entity.ai.goal.LongDoorInteractGoal {
        public LongDoorInteractGoal(RaiderEntity raider) {
            super(raider, false);
        }

        @Override
        public boolean canStart() {
            return super.canStart() && IllagerEntity.this.hasActiveRaid();
        }
    }
}

