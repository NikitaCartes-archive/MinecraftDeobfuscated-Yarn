package net.minecraft.entity.ai.brain.sensor;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;

/**
 * A sensor can update memories over time in a brain. The sensor's computation
 * replaces that of individual tasks, so that it is more efficient than the goal
 * system.
 * 
 * @see net.minecraft.entity.ai.brain.Brain#sensors
 */
public abstract class Sensor<E extends LivingEntity> {
	private static final Random RANDOM = Random.createThreadSafe();
	private static final int DEFAULT_RUN_TIME = 20;
	private static final int BASE_MAX_DISTANCE = 16;
	private static final TargetPredicate TARGET_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(16.0);
	private static final TargetPredicate TARGET_PREDICATE_IGNORE_DISTANCE_SCALING = TargetPredicate.createNonAttackable()
		.setBaseMaxDistance(16.0)
		.ignoreDistanceScalingFactor();
	private static final TargetPredicate ATTACKABLE_TARGET_PREDICATE = TargetPredicate.createAttackable().setBaseMaxDistance(16.0);
	private static final TargetPredicate ATTACKABLE_TARGET_PREDICATE_IGNORE_DISTANCE_SCALING = TargetPredicate.createAttackable()
		.setBaseMaxDistance(16.0)
		.ignoreDistanceScalingFactor();
	private static final TargetPredicate ATTACKABLE_TARGET_PREDICATE_IGNORE_VISIBILITY = TargetPredicate.createAttackable()
		.setBaseMaxDistance(16.0)
		.ignoreVisibility();
	private static final TargetPredicate ATTACKABLE_TARGET_PREDICATE_IGNORE_VISIBILITY_OR_DISTANCE_SCALING = TargetPredicate.createAttackable()
		.setBaseMaxDistance(16.0)
		.ignoreVisibility()
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
			this.updateRange(entity);
			this.sense(world, entity);
		}
	}

	private void updateRange(E entity) {
		double d = entity.getAttributeValue(EntityAttributes.FOLLOW_RANGE);
		TARGET_PREDICATE.setBaseMaxDistance(d);
		TARGET_PREDICATE_IGNORE_DISTANCE_SCALING.setBaseMaxDistance(d);
		ATTACKABLE_TARGET_PREDICATE.setBaseMaxDistance(d);
		ATTACKABLE_TARGET_PREDICATE_IGNORE_DISTANCE_SCALING.setBaseMaxDistance(d);
		ATTACKABLE_TARGET_PREDICATE_IGNORE_VISIBILITY.setBaseMaxDistance(d);
		ATTACKABLE_TARGET_PREDICATE_IGNORE_VISIBILITY_OR_DISTANCE_SCALING.setBaseMaxDistance(d);
	}

	protected abstract void sense(ServerWorld world, E entity);

	public abstract Set<MemoryModuleType<?>> getOutputMemoryModules();

	public static boolean testTargetPredicate(LivingEntity entity, LivingEntity target) {
		return entity.getBrain().hasMemoryModuleWithValue(MemoryModuleType.ATTACK_TARGET, target)
			? TARGET_PREDICATE_IGNORE_DISTANCE_SCALING.test(entity, target)
			: TARGET_PREDICATE.test(entity, target);
	}

	public static boolean testAttackableTargetPredicate(LivingEntity entity, LivingEntity target) {
		return entity.getBrain().hasMemoryModuleWithValue(MemoryModuleType.ATTACK_TARGET, target)
			? ATTACKABLE_TARGET_PREDICATE_IGNORE_DISTANCE_SCALING.test(entity, target)
			: ATTACKABLE_TARGET_PREDICATE.test(entity, target);
	}

	public static Predicate<LivingEntity> hasTargetBeenAttackableRecently(LivingEntity entity, int ticks) {
		return hasPredicatePassedRecently(ticks, target -> testAttackableTargetPredicate(entity, target));
	}

	public static boolean testAttackableTargetPredicateIgnoreVisibility(LivingEntity entity, LivingEntity target) {
		return entity.getBrain().hasMemoryModuleWithValue(MemoryModuleType.ATTACK_TARGET, target)
			? ATTACKABLE_TARGET_PREDICATE_IGNORE_VISIBILITY_OR_DISTANCE_SCALING.test(entity, target)
			: ATTACKABLE_TARGET_PREDICATE_IGNORE_VISIBILITY.test(entity, target);
	}

	static <T> Predicate<T> hasPredicatePassedRecently(int times, Predicate<T> predicate) {
		AtomicInteger atomicInteger = new AtomicInteger(0);
		return target -> {
			if (predicate.test(target)) {
				atomicInteger.set(times);
				return true;
			} else {
				return atomicInteger.decrementAndGet() >= 0;
			}
		};
	}
}
