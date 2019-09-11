/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ZombieHorseEntity
extends HorseBaseEntity {
    public ZombieHorseEntity(EntityType<? extends ZombieHorseEntity> entityType, World world) {
        super((EntityType<? extends HorseBaseEntity>)entityType, world);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(15.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.2f);
        this.getAttributeInstance(JUMP_STRENGTH).setBaseValue(this.getChildJumpStrengthBonus());
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.ENTITY_ZOMBIE_HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.ENTITY_ZOMBIE_HORSE_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        super.getHurtSound(damageSource);
        return SoundEvents.ENTITY_ZOMBIE_HORSE_HURT;
    }

    @Override
    @Nullable
    public PassiveEntity createChild(PassiveEntity passiveEntity) {
        return EntityType.ZOMBIE_HORSE.create(this.world);
    }

    @Override
    public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if (itemStack.getItem() instanceof SpawnEggItem) {
            return super.interactMob(playerEntity, hand);
        }
        if (!this.isTame()) {
            return false;
        }
        if (this.isBaby()) {
            return super.interactMob(playerEntity, hand);
        }
        if (playerEntity.shouldCancelInteraction()) {
            this.openInventory(playerEntity);
            return true;
        }
        if (this.hasPassengers()) {
            return super.interactMob(playerEntity, hand);
        }
        if (!itemStack.isEmpty()) {
            if (!this.isSaddled() && itemStack.getItem() == Items.SADDLE) {
                this.openInventory(playerEntity);
                return true;
            }
            if (itemStack.useOnEntity(playerEntity, this, hand)) {
                return true;
            }
        }
        this.putPlayerOnBack(playerEntity);
        return true;
    }

    @Override
    protected void initCustomGoals() {
    }
}

