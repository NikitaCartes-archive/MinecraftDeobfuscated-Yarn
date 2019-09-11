/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import net.minecraft.util.ThreadExecutor;

public abstract class NonBlockingThreadExecutor<R extends Runnable>
extends ThreadExecutor<R> {
    private int runningTasks;

    public NonBlockingThreadExecutor(String string) {
        super(string);
    }

    @Override
    protected boolean shouldExecuteAsync() {
        return this.hasRunningTasks() || super.shouldExecuteAsync();
    }

    protected boolean hasRunningTasks() {
        return this.runningTasks != 0;
    }

    @Override
    protected void executeTask(R runnable) {
        ++this.runningTasks;
        try {
            super.executeTask(runnable);
        } finally {
            --this.runningTasks;
        }
    }
}

