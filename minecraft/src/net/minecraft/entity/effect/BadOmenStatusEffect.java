package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Difficulty;

class BadOmenStatusEffect extends StatusEffect {
	protected BadOmenStatusEffect(StatusEffectCategory statusEffectCategory, int i) {
		super(statusEffectCategory, i);
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
		super.applyUpdateEffect(entity, amplifier);
		if (entity instanceof ServerPlayerEntity serverPlayerEntity && !entity.isSpectator()) {
			ServerWorld serverWorld = serverPlayerEntity.getServerWorld();
			if (serverWorld.getDifficulty() == Difficulty.PEACEFUL) {
				return;
			}

			if (serverWorld.isNearOccupiedPointOfInterest(entity.getBlockPos())) {
				serverWorld.getRaidManager().startRaid(serverPlayerEntity);
			}
		}
	}
}
