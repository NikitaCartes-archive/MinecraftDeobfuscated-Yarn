package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;

public class IsInWaterSensor extends Sensor<LivingEntity> {
	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.IS_IN_WATER);
	}

	@Override
	protected void sense(ServerWorld world, LivingEntity entity) {
		if (entity.isTouchingWater()) {
			entity.getBrain().remember(MemoryModuleType.IS_IN_WATER, Unit.INSTANCE);
		} else {
			entity.getBrain().forget(MemoryModuleType.IS_IN_WATER);
		}
	}
}
