package net.minecraft.entity.ai.brain.sensor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.registry.tag.EntityTypeTags;

public class AxolotlAttackablesSensor extends NearestVisibleLivingEntitySensor {
	public static final float TARGET_RANGE = 8.0F;

	@Override
	protected boolean matches(LivingEntity entity, LivingEntity target) {
		return this.isInRange(entity, target)
			&& target.isInsideWaterOrBubbleColumn()
			&& (this.isAlwaysHostileTo(target) || this.canHunt(entity, target))
			&& Sensor.testAttackableTargetPredicate(entity, target);
	}

	private boolean canHunt(LivingEntity axolotl, LivingEntity target) {
		return !axolotl.getBrain().hasMemoryModule(MemoryModuleType.HAS_HUNTING_COOLDOWN) && target.getType().isIn(EntityTypeTags.AXOLOTL_HUNT_TARGETS);
	}

	private boolean isAlwaysHostileTo(LivingEntity axolotl) {
		return axolotl.getType().isIn(EntityTypeTags.AXOLOTL_ALWAYS_HOSTILES);
	}

	private boolean isInRange(LivingEntity axolotl, LivingEntity target) {
		return target.squaredDistanceTo(axolotl) <= 64.0;
	}

	@Override
	protected MemoryModuleType<LivingEntity> getOutputMemoryModule() {
		return MemoryModuleType.NEAREST_ATTACKABLE;
	}
}
