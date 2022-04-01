package net.minecraft.entity.ai;

import net.minecraft.entity.LivingEntity;

public interface RangedAttackMob {
	void attack(LivingEntity target, float pullProgress);

	default void method_42824(float f) {
	}
}
