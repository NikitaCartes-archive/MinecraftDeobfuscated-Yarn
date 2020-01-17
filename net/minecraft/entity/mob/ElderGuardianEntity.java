/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ElderGuardianEntity
extends GuardianEntity {
    public static final float field_17492 = EntityType.ELDER_GUARDIAN.getWidth() / EntityType.GUARDIAN.getWidth();

    public ElderGuardianEntity(EntityType<? extends ElderGuardianEntity> entityType, World world) {
        super((EntityType<? extends GuardianEntity>)entityType, world);
        this.setPersistent();
        if (this.wanderGoal != null) {
            this.wanderGoal.setChance(400);
        }
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3f);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(8.0);
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(80.0);
    }

    @Override
    public int getWarmupTime() {
        return 60;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isInsideWaterOrBubbleColumn() ? SoundEvents.ENTITY_ELDER_GUARDIAN_AMBIENT : SoundEvents.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.isInsideWaterOrBubbleColumn() ? SoundEvents.ENTITY_ELDER_GUARDIAN_HURT : SoundEvents.ENTITY_ELDER_GUARDIAN_HURT_LAND;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isInsideWaterOrBubbleColumn() ? SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH : SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH_LAND;
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.ENTITY_ELDER_GUARDIAN_FLOP;
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        int i = 1200;
        if ((this.age + this.getEntityId()) % 1200 == 0) {
            StatusEffect statusEffect = StatusEffects.MINING_FATIGUE;
            List<ServerPlayerEntity> list = ((ServerWorld)this.world).getPlayers(serverPlayerEntity -> this.squaredDistanceTo((Entity)serverPlayerEntity) < 2500.0 && serverPlayerEntity.interactionManager.isSurvivalLike());
            int j = 2;
            int k = 6000;
            int l = 1200;
            for (ServerPlayerEntity serverPlayerEntity2 : list) {
                if (serverPlayerEntity2.hasStatusEffect(statusEffect) && serverPlayerEntity2.getStatusEffect(statusEffect).getAmplifier() >= 2 && serverPlayerEntity2.getStatusEffect(statusEffect).getDuration() >= 1200) continue;
                serverPlayerEntity2.networkHandler.sendPacket(new GameStateChangeS2CPacket(10, 0.0f));
                serverPlayerEntity2.addStatusEffect(new StatusEffectInstance(statusEffect, 6000, 2));
            }
        }
        if (!this.hasPositionTarget()) {
            this.setPositionTarget(new BlockPos(this), 16);
        }
    }
}

