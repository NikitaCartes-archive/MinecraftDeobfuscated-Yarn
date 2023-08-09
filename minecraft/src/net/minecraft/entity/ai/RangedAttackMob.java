package net.minecraft.entity.ai;

import net.minecraft.entity.LivingEntity;

public interface RangedAttackMob {
	void shootAt(LivingEntity target, float pullProgress);
}
