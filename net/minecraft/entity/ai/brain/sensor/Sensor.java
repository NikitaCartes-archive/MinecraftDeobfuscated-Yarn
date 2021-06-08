/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.sensor;

import java.util.Random;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

/**
 * A sensor can update memories over time in a brain. The sensor's computation
 * replaces that of individual tasks, so that it is more efficient than the goal
 * system.
 * 
 * @see Brain#sensors
 */
public abstract class Sensor<E extends LivingEntity> {
    private static final Random RANDOM = new Random();
    private static final int DEFAULT_RUN_TIME = 20;
    protected static final int BASE_MAX_DISTANCE = 16;
    private static final TargetPredicate TARGET_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(16.0);
    private static final TargetPredicate TARGET_PREDICATE_IGNORE_DISTANCE_SCALING = TargetPredicate.createNonAttackable().setBaseMaxDistance(16.0).ignoreDistanceScalingFactor();
    private static final TargetPredicate field_33762 = TargetPredicate.createAttackable().setBaseMaxDistance(16.0);
    private static final TargetPredicate field_33763 = TargetPredicate.createAttackable().setBaseMaxDistance(16.0).ignoreDistanceScalingFactor();
    private final int senseInterval;
    private long lastSenseTime;

    public Sensor(int senseInterval) {
        this.senseInterval = senseInterval;
        this.lastSenseTime = RANDOM.nextInt(senseInterval);
    }

    public Sensor() {
        this(20);
    }

    public final void tick(ServerWorld world, E entity) {
        if (--this.lastSenseTime <= 0L) {
            this.lastSenseTime = this.senseInterval;
            this.sense(world, entity);
        }
    }

    protected abstract void sense(ServerWorld var1, E var2);

    public abstract Set<MemoryModuleType<?>> getOutputMemoryModules();

    protected static boolean testTargetPredicate(LivingEntity entity, LivingEntity target) {
        if (entity.getBrain().hasMemoryModuleWithValue(MemoryModuleType.ATTACK_TARGET, target)) {
            return TARGET_PREDICATE_IGNORE_DISTANCE_SCALING.test(entity, target);
        }
        return TARGET_PREDICATE.test(entity, target);
    }

    public static boolean method_36982(LivingEntity livingEntity, LivingEntity livingEntity2) {
        if (livingEntity.getBrain().hasMemoryModuleWithValue(MemoryModuleType.ATTACK_TARGET, livingEntity2)) {
            return field_33763.test(livingEntity, livingEntity2);
        }
        return field_33762.test(livingEntity, livingEntity2);
    }
}

