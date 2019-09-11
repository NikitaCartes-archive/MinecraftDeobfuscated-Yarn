/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import java.util.concurrent.locks.LockSupport;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.ThreadExecutor;

@Environment(value=EnvType.CLIENT)
public class SoundExecutor
extends ThreadExecutor<Runnable> {
    private Thread thread = this.createThread();
    private volatile boolean stopped;

    public SoundExecutor() {
        super("Sound executor");
    }

    private Thread createThread() {
        Thread thread = new Thread(this::waitForStop);
        thread.setDaemon(true);
        thread.setName("Sound engine");
        thread.start();
        return thread;
    }

    @Override
    protected Runnable createTask(Runnable runnable) {
        return runnable;
    }

    @Override
    protected boolean canExecute(Runnable runnable) {
        return !this.stopped;
    }

    @Override
    protected Thread getThread() {
        return this.thread;
    }

    private void waitForStop() {
        while (!this.stopped) {
            this.executeTasks(() -> this.stopped);
        }
    }

    @Override
    protected void waitForTasks() {
        LockSupport.park("waiting for tasks");
    }

    public void restart() {
        this.stopped = true;
        this.thread.interrupt();
        try {
            this.thread.join();
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        this.clearTasks();
        this.stopped = false;
        this.thread = this.createThread();
    }
}

