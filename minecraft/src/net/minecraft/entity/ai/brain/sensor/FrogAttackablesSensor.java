package net.minecraft.entity.ai.brain.sensor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.FrogEntity;

public class FrogAttackablesSensor extends NearestVisibleLivingEntitySensor {
	public static final float RANGE = 10.0F;

	@Override
	protected boolean matches(LivingEntity entity, LivingEntity target) {
		return !entity.getBrain().hasMemoryModule(MemoryModuleType.HAS_HUNTING_COOLDOWN)
				&& Sensor.testAttackableTargetPredicate(entity, target)
				&& FrogEntity.isValidFrogFood(target)
			? target.isInRange(entity, 10.0)
			: false;
	}

	@Override
	protected MemoryModuleType<LivingEntity> getOutputMemoryModule() {
		return MemoryModuleType.NEAREST_ATTACKABLE;
	}
}
