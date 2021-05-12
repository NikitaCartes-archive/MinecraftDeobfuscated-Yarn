/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.thread;

import net.minecraft.util.thread.ThreadExecutor;

public abstract class ReentrantThreadExecutor<R extends Runnable>
extends ThreadExecutor<R> {
    private int runningTasks;

    public ReentrantThreadExecutor(String string) {
        super(string);
    }

    @Override
    public boolean shouldExecuteAsync() {
        return this.hasRunningTasks() || super.shouldExecuteAsync();
    }

    protected boolean hasRunningTasks() {
        return this.runningTasks != 0;
    }

    @Override
    public void executeTask(R task) {
        ++this.runningTasks;
        try {
            super.executeTask(task);
        } finally {
            --this.runningTasks;
        }
    }
}

