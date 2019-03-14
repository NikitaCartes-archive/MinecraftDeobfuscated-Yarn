package net.minecraft.entity.ai.brain.sensor;

import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public abstract class Sensor<E extends LivingEntity> {
	private final int senseInterval = 10;
	protected long field_18463 = 0L;

	public boolean canSense(ServerWorld serverWorld, E livingEntity) {
		return serverWorld.getTime() - this.field_18463 >= 10L && this.getOutputMemoryModules().stream().anyMatch(livingEntity.getBrain()::hasMemoryModule);
	}

	public abstract void sense(ServerWorld serverWorld, E livingEntity);

	protected abstract Set<MemoryModuleType<?>> getOutputMemoryModules();
}
