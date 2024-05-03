package net.minecraft.entity.effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractWindChargeEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

class WindChargedStatusEffect extends StatusEffect {
	protected WindChargedStatusEffect(StatusEffectCategory statusEffectCategory, int i) {
		super(statusEffectCategory, i, ParticleTypes.SMALL_GUST);
	}

	@Override
	public void onEntityRemoval(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
		if (reason == Entity.RemovalReason.KILLED && entity.getWorld() instanceof ServerWorld serverWorld) {
			double d = entity.getX();
			double e = entity.getY() + (double)(entity.getHeight() / 2.0F);
			double f = entity.getZ();
			float g = 3.0F + entity.getRandom().nextFloat() * 2.0F;
			serverWorld.createExplosion(
				entity,
				null,
				AbstractWindChargeEntity.EXPLOSION_BEHAVIOR,
				d,
				e,
				f,
				g,
				false,
				World.ExplosionSourceType.TRIGGER,
				ParticleTypes.GUST_EMITTER_SMALL,
				ParticleTypes.GUST_EMITTER_LARGE,
				SoundEvents.ENTITY_BREEZE_WIND_BURST
			);
		}
	}
}
