package net.minecraft.entity.mob;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ElderGuardianEntity extends GuardianEntity {
	public static final float field_17492 = EntityType.ELDER_GUARDIAN.getWidth() / EntityType.GUARDIAN.getWidth();

	public ElderGuardianEntity(EntityType<? extends ElderGuardianEntity> entityType, World world) {
		super(entityType, world);
		this.setPersistent();
		if (this.field_7289 != null) {
			this.field_7289.setChance(400);
		}
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3F);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(8.0);
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(80.0);
	}

	@Override
	public int getWarmupTime() {
		return 60;
	}

	@Environment(EnvType.CLIENT)
	public void method_7010() {
		this.tailAngle = 1.0F;
		this.prevTailAngle = this.tailAngle;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isInsideWaterOrBubbleColumn() ? SoundEvents.field_15127 : SoundEvents.field_14569;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return this.isInsideWaterOrBubbleColumn() ? SoundEvents.field_14868 : SoundEvents.field_14652;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return this.isInsideWaterOrBubbleColumn() ? SoundEvents.field_15052 : SoundEvents.field_14973;
	}

	@Override
	protected SoundEvent method_7062() {
		return SoundEvents.field_14939;
	}

	@Override
	protected void mobTick() {
		super.mobTick();
		int i = 1200;
		if ((this.age + this.getEntityId()) % 1200 == 0) {
			StatusEffect statusEffect = StatusEffects.field_5901;
			List<ServerPlayerEntity> list = ((ServerWorld)this.world)
				.method_18766(serverPlayerEntityx -> this.squaredDistanceTo(serverPlayerEntityx) < 2500.0 && serverPlayerEntityx.interactionManager.isSurvivalLike());
			int j = 2;
			int k = 6000;
			int l = 1200;

			for (ServerPlayerEntity serverPlayerEntity : list) {
				if (!serverPlayerEntity.hasPotionEffect(statusEffect)
					|| serverPlayerEntity.getPotionEffect(statusEffect).getAmplifier() < 2
					|| serverPlayerEntity.getPotionEffect(statusEffect).getDuration() < 1200) {
					serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(10, 0.0F));
					serverPlayerEntity.addPotionEffect(new StatusEffectInstance(statusEffect, 6000, 2));
				}
			}
		}

		if (!this.hasWalkTargetRange()) {
			this.setWalkTarget(new BlockPos(this), 16);
		}
	}
}
