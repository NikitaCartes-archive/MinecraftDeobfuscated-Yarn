package net.minecraft.entity.ai.brain.sensor;

import java.util.Random;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public abstract class Sensor<E extends LivingEntity> {
	private static final Random RANDOM = new Random();
	private static final int DEFAULT_RUN_TIME = 20;
	protected static final int BASE_MAX_DISTANCE = 16;
	private static final TargetPredicate TARGET_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(16.0);
	private static final TargetPredicate TARGET_PREDICATE_IGNORE_DISTANCE_SCALING = TargetPredicate.createNonAttackable()
		.setBaseMaxDistance(16.0)
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

	public final void tick(ServerWorld world, E entity) {
		if (--this.lastSenseTime <= 0L) {
			this.lastSenseTime = (long)this.senseInterval;
			this.sense(world, entity);
		}
	}

	protected abstract void sense(ServerWorld world, E entity);

	public abstract Set<MemoryModuleType<?>> getOutputMemoryModules();

	protected static boolean testTargetPredicate(LivingEntity entity, LivingEntity target) {
		return entity.getBrain().hasMemoryModuleWithValue(MemoryModuleType.ATTACK_TARGET, target)
			? TARGET_PREDICATE_IGNORE_DISTANCE_SCALING.test(entity, target)
			: TARGET_PREDICATE.test(entity, target);
	}
}
