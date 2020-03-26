/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.SkeletonHorseTrapTriggerGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SkeletonHorseEntity
extends HorseBaseEntity {
    private final SkeletonHorseTrapTriggerGoal trapTriggerGoal = new SkeletonHorseTrapTriggerGoal(this);
    private boolean trapped;
    private int trapTime;

    public SkeletonHorseEntity(EntityType<? extends SkeletonHorseEntity> entityType, World world) {
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
    protected void initCustomGoals() {
    }

    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        if (this.isSubmergedIn(FluidTags.WATER)) {
            return SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT_WATER;
        }
        return SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.ENTITY_SKELETON_HORSE_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        super.getHurtSound(source);
        return SoundEvents.ENTITY_SKELETON_HORSE_HURT;
    }

    @Override
    protected SoundEvent getSwimSound() {
        if (this.onGround) {
            if (this.hasPassengers()) {
                ++this.soundTicks;
                if (this.soundTicks > 5 && this.soundTicks % 3 == 0) {
                    return SoundEvents.ENTITY_SKELETON_HORSE_GALLOP_WATER;
                }
                if (this.soundTicks <= 5) {
                    return SoundEvents.ENTITY_SKELETON_HORSE_STEP_WATER;
                }
            } else {
                return SoundEvents.ENTITY_SKELETON_HORSE_STEP_WATER;
            }
        }
        return SoundEvents.ENTITY_SKELETON_HORSE_SWIM;
    }

    @Override
    protected void playSwimSound(float volume) {
        if (this.onGround) {
            super.playSwimSound(0.3f);
        } else {
            super.playSwimSound(Math.min(0.1f, volume * 25.0f));
        }
    }

    @Override
    protected void playJumpSound() {
        if (this.isTouchingWater()) {
            this.playSound(SoundEvents.ENTITY_SKELETON_HORSE_JUMP_WATER, 0.4f, 1.0f);
        } else {
            super.playJumpSound();
        }
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    public double getMountedHeightOffset() {
        return super.getMountedHeightOffset() - 0.1875;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.isTrapped() && this.trapTime++ >= 18000) {
            this.remove();
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("SkeletonTrap", this.isTrapped());
        tag.putInt("SkeletonTrapTime", this.trapTime);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setTrapped(tag.getBoolean("SkeletonTrap"));
        this.trapTime = tag.getInt("SkeletonTrapTime");
    }

    @Override
    public boolean canBeRiddenInWater() {
        return true;
    }

    @Override
    protected float getBaseMovementSpeedMultiplier() {
        return 0.96f;
    }

    public boolean isTrapped() {
        return this.trapped;
    }

    public void setTrapped(boolean trapped) {
        if (trapped == this.trapped) {
            return;
        }
        this.trapped = trapped;
        if (trapped) {
            this.goalSelector.add(1, this.trapTriggerGoal);
        } else {
            this.goalSelector.remove(this.trapTriggerGoal);
        }
    }

    @Override
    @Nullable
    public PassiveEntity createChild(PassiveEntity mate) {
        return EntityType.SKELETON_HORSE.create(this.world);
    }

    @Override
    public boolean interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.getItem() instanceof SpawnEggItem) {
            return super.interactMob(player, hand);
        }
        if (!this.isTame()) {
            return false;
        }
        if (this.isBaby()) {
            return super.interactMob(player, hand);
        }
        if (player.shouldCancelInteraction()) {
            this.openInventory(player);
            return true;
        }
        if (this.hasPassengers()) {
            return super.interactMob(player, hand);
        }
        if (!itemStack.isEmpty()) {
            if (itemStack.getItem() == Items.SADDLE && !this.isSaddled()) {
                this.openInventory(player);
                return true;
            }
            if (itemStack.useOnEntity(player, this, hand)) {
                return true;
            }
        }
        this.putPlayerOnBack(player);
        return true;
    }
}

