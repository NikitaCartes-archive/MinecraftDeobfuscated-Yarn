package net.minecraft.entity.ai;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;

public interface RangedAttacker {
	void attack(LivingEntity livingEntity, float f);

	@Environment(EnvType.CLIENT)
	boolean hasArmsRaised();

	void setArmsRaised(boolean bl);
}
