package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class DummySensor extends Sensor<LivingEntity> {
	@Override
	public boolean canSense(ServerWorld serverWorld, LivingEntity livingEntity) {
		return false;
	}

	@Override
	public void sense(ServerWorld serverWorld, LivingEntity livingEntity) {
	}

	@Override
	protected Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of();
	}
}
