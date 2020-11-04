package net.minecraft.entity.ai.brain.sensor;

import java.util.Random;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public abstract class Sensor<E extends LivingEntity> {
	private static final Random RANDOM = new Random();
	private static final TargetPredicate field_26630 = new TargetPredicate().setBaseMaxDistance(16.0).includeTeammates().ignoreEntityTargetRules();
	private static final TargetPredicate field_26631 = new TargetPredicate()
		.setBaseMaxDistance(16.0)
		.includeTeammates()
		.ignoreEntityTargetRules()
		.ignoreDistanceScalingFactor();
	private final int senseInterval;
	private long lastSenseTime;

	public Sensor(int senseInterval) {
		this.senseInterval = senseInterval;
		this.lastSenseTime = (long)RANDOM.nextInt(senseInterval);
	}

	public Sensor() {
		this(20);
	}

	public final void tick(ServerWorld serverWorld, E entity) {
		if (--this.lastSenseTime <= 0L) {
			this.lastSenseTime = (long)this.senseInterval;
			this.sense(serverWorld, entity);
		}
	}

	protected abstract void sense(ServerWorld world, E entity);

	public abstract Set<MemoryModuleType<?>> getOutputMemoryModules();

	protected static boolean method_30954(LivingEntity livingEntity, LivingEntity livingEntity2) {
		return livingEntity.getBrain().method_29519(MemoryModuleType.ATTACK_TARGET, livingEntity2)
			? field_26631.test(livingEntity, livingEntity2)
			: field_26630.test(livingEntity, livingEntity2);
	}
}
