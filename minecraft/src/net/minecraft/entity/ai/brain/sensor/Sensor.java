package net.minecraft.entity.ai.brain.sensor;

import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public abstract class Sensor<E extends LivingEntity> {
	private final int senseInterval;
	protected long lastSenseTime;

	public Sensor(int i) {
		this.senseInterval = i;
	}

	public Sensor() {
		this(20);
	}

	public final void canSense(ServerWorld serverWorld, E livingEntity) {
		if (serverWorld.getTime() - this.lastSenseTime >= (long)this.senseInterval) {
			this.lastSenseTime = serverWorld.getTime();
			this.sense(serverWorld, livingEntity);
		}
	}

	protected abstract void sense(ServerWorld serverWorld, E livingEntity);

	public abstract Set<MemoryModuleType<?>> getOutputMemoryModules();
}
