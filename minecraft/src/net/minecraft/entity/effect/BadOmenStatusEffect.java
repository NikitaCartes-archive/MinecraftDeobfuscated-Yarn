package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.raid.Raid;
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
	public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
		if (entity instanceof ServerPlayerEntity serverPlayerEntity && !serverPlayerEntity.isSpectator()) {
			ServerWorld serverWorld = serverPlayerEntity.getServerWorld();
			if (serverWorld.getDifficulty() != Difficulty.PEACEFUL && serverWorld.isNearOccupiedPointOfInterest(serverPlayerEntity.getBlockPos())) {
				Raid raid = serverWorld.getRaidAt(serverPlayerEntity.getBlockPos());
				if (raid == null || raid.getBadOmenLevel() < raid.getMaxAcceptableBadOmenLevel()) {
					serverPlayerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RAID_OMEN, 600, amplifier));
					serverPlayerEntity.setStartRaidPos(serverPlayerEntity.getBlockPos());
					return false;
				}
			}
		}

		return true;
	}
}
