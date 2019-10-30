/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class PufferfishEntity
extends FishEntity {
    private static final TrackedData<Integer> PUFF_STATE = DataTracker.registerData(PufferfishEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private int inflateTicks;
    private int deflateTicks;
    private static final Predicate<LivingEntity> BLOW_UP_FILTER = livingEntity -> {
        if (livingEntity == null) {
            return false;
        }
        if (livingEntity instanceof PlayerEntity && (livingEntity.isSpectator() || ((PlayerEntity)livingEntity).isCreative())) {
            return false;
        }
        return livingEntity.getGroup() != EntityGroup.AQUATIC;
    };

    public PufferfishEntity(EntityType<? extends PufferfishEntity> entityType, World world) {
        super((EntityType<? extends FishEntity>)entityType, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(PUFF_STATE, 0);
    }

    public int getPuffState() {
        return this.dataTracker.get(PUFF_STATE);
    }

    public void setPuffState(int i) {
        this.dataTracker.set(PUFF_STATE, i);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> trackedData) {
        if (PUFF_STATE.equals(trackedData)) {
            this.calculateDimensions();
        }
        super.onTrackedDataSet(trackedData);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        super.writeCustomDataToTag(compoundTag);
        compoundTag.putInt("PuffState", this.getPuffState());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        super.readCustomDataFromTag(compoundTag);
        this.setPuffState(compoundTag.getInt("PuffState"));
    }

    @Override
    protected ItemStack getFishBucketItem() {
        return new ItemStack(Items.PUFFERFISH_BUCKET);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new InflateGoal(this));
    }

    @Override
    public void tick() {
        if (!this.world.isClient && this.isAlive() && this.canMoveVoluntarily()) {
            if (this.inflateTicks > 0) {
                if (this.getPuffState() == 0) {
                    this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getSoundPitch());
                    this.setPuffState(1);
                } else if (this.inflateTicks > 40 && this.getPuffState() == 1) {
                    this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getSoundPitch());
                    this.setPuffState(2);
                }
                ++this.inflateTicks;
            } else if (this.getPuffState() != 0) {
                if (this.deflateTicks > 60 && this.getPuffState() == 2) {
                    this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getSoundPitch());
                    this.setPuffState(1);
                } else if (this.deflateTicks > 100 && this.getPuffState() == 1) {
                    this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getSoundPitch());
                    this.setPuffState(0);
                }
                ++this.deflateTicks;
            }
        }
        super.tick();
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.isAlive() && this.getPuffState() > 0) {
            List<LivingEntity> list = this.world.getEntities(MobEntity.class, this.getBoundingBox().expand(0.3), BLOW_UP_FILTER);
            for (MobEntity mobEntity : list) {
                if (!mobEntity.isAlive()) continue;
                this.sting(mobEntity);
            }
        }
    }

    private void sting(MobEntity mobEntity) {
        int i = this.getPuffState();
        if (mobEntity.damage(DamageSource.mob(this), 1 + i)) {
            mobEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60 * i, 0));
            this.playSound(SoundEvents.ENTITY_PUFFER_FISH_STING, 1.0f, 1.0f);
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity playerEntity) {
        int i = this.getPuffState();
        if (playerEntity instanceof ServerPlayerEntity && i > 0 && playerEntity.damage(DamageSource.mob(this), 1 + i)) {
            ((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(new GameStateChangeS2CPacket(9, 0.0f));
            playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60 * i, 0));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PUFFER_FISH_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PUFFER_FISH_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_PUFFER_FISH_HURT;
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.ENTITY_PUFFER_FISH_FLOP;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose entityPose) {
        return super.getDimensions(entityPose).scaled(PufferfishEntity.getScaleForPuffState(this.getPuffState()));
    }

    private static float getScaleForPuffState(int i) {
        switch (i) {
            case 1: {
                return 0.7f;
            }
            case 0: {
                return 0.5f;
            }
        }
        return 1.0f;
    }

    static class InflateGoal
    extends Goal {
        private final PufferfishEntity pufferfish;

        public InflateGoal(PufferfishEntity pufferfishEntity) {
            this.pufferfish = pufferfishEntity;
        }

        @Override
        public boolean canStart() {
            List<LivingEntity> list = this.pufferfish.world.getEntities(LivingEntity.class, this.pufferfish.getBoundingBox().expand(2.0), BLOW_UP_FILTER);
            return !list.isEmpty();
        }

        @Override
        public void start() {
            this.pufferfish.inflateTicks = 1;
            this.pufferfish.deflateTicks = 0;
        }

        @Override
        public void stop() {
            this.pufferfish.inflateTicks = 0;
        }

        @Override
        public boolean shouldContinue() {
            List<LivingEntity> list = this.pufferfish.world.getEntities(LivingEntity.class, this.pufferfish.getBoundingBox().expand(2.0), BLOW_UP_FILTER);
            return !list.isEmpty();
        }
    }
}

