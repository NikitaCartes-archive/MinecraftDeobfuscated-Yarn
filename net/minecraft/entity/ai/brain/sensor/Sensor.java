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

public abstract class Sensor<E extends LivingEntity> {
    private static final Random RANDOM = new Random();
    private static final TargetPredicate TARGET_PREDICATE = new TargetPredicate().setBaseMaxDistance(16.0).includeTeammates().ignoreEntityTargetRules();
    private static final TargetPredicate TARGET_PREDICATE_IGNORE_DISTANCE_SCALING = new TargetPredicate().setBaseMaxDistance(16.0).includeTeammates().ignoreEntityTargetRules().ignoreDistanceScalingFactor();
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

    protected static boolean method_30954(LivingEntity entity, LivingEntity target) {
        if (entity.getBrain().hasMemoryModuleWithValue(MemoryModuleType.ATTACK_TARGET, target)) {
            return TARGET_PREDICATE_IGNORE_DISTANCE_SCALING.test(entity, target);
        }
        return TARGET_PREDICATE.test(entity, target);
    }
}

