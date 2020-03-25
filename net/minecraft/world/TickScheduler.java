/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;

public interface TickScheduler<T> {
    public boolean isScheduled(BlockPos var1, T var2);

    default public void schedule(BlockPos pos, T object, int delay) {
        this.schedule(pos, object, delay, TickPriority.NORMAL);
    }

    public void schedule(BlockPos var1, T var2, int var3, TickPriority var4);

    public boolean isTicking(BlockPos var1, T var2);
}

