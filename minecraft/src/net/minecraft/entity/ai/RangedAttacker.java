package net.minecraft.entity.ai;

import net.minecraft.entity.LivingEntity;

public interface RangedAttacker {
	void attack(LivingEntity livingEntity, float f);

	void setArmsRaised(boolean bl);
}
