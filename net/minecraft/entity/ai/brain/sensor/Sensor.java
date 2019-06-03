/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.sensor;

import java.util.Random;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public abstract class Sensor<E extends LivingEntity> {
    private static final Random RANDOM = new Random();
    private final int senseInterval;
    private long lastSenseTime;

    public Sensor(int i) {
        this.senseInterval = i;
        this.lastSenseTime = RANDOM.nextInt(i);
    }

    public Sensor() {
        this(20);
    }

    public final void canSense(ServerWorld serverWorld, E livingEntity) {
        if (--this.lastSenseTime <= 0L) {
            this.lastSenseTime = this.senseInterval;
            this.sense(serverWorld, livingEntity);
        }
    }

    protected abstract void sense(ServerWorld var1, E var2);

    public abstract Set<MemoryModuleType<?>> getOutputMemoryModules();
}

