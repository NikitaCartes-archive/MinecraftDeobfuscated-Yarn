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
    private static final TargetPredicate field_26630 = new TargetPredicate().setBaseMaxDistance(16.0).includeTeammates().ignoreEntityTargetRules();
    private static final TargetPredicate field_26631 = new TargetPredicate().setBaseMaxDistance(16.0).includeTeammates().ignoreEntityTargetRules().ignoreDistanceScalingFactor();
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

    protected static boolean method_30954(LivingEntity livingEntity, LivingEntity livingEntity2) {
        if (livingEntity.getBrain().method_29519(MemoryModuleType.ATTACK_TARGET, livingEntity2)) {
            return field_26631.test(livingEntity, livingEntity2);
        }
        return field_26630.test(livingEntity, livingEntity2);
    }
}

