/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

/**
 * A functional interface that represents a task.
 */
public interface TaskRunnable<E extends LivingEntity> {
    /**
     * Runs the task.
     * 
     * @return whether the task successfully ran
     */
    public boolean trigger(ServerWorld var1, E var2, long var3);
}

