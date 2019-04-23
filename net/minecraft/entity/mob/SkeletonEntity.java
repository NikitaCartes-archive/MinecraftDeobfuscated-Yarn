/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class SkeletonEntity
extends AbstractSkeletonEntity {
    public SkeletonEntity(EntityType<? extends SkeletonEntity> entityType, World world) {
        super((EntityType<? extends AbstractSkeletonEntity>)entityType, world);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SKELETON_DEATH;
    }

    @Override
    SoundEvent getStepSound() {
        return SoundEvents.ENTITY_SKELETON_STEP;
    }

    @Override
    protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
        CreeperEntity creeperEntity;
        super.dropEquipment(damageSource, i, bl);
        Entity entity = damageSource.getAttacker();
        if (entity instanceof CreeperEntity && (creeperEntity = (CreeperEntity)entity).shouldDropHead()) {
            creeperEntity.onHeadDropped();
            this.dropItem(Items.SKELETON_SKULL);
        }
    }
}

