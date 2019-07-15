/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Actor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ThreadExecutor<R extends Runnable>
implements Actor<R>,
Executor {
    private final String name;
    private static final Logger LOGGER = LogManager.getLogger();
    private final Queue<R> taskQueue = Queues.newConcurrentLinkedQueue();
    private int waitCount;

    protected ThreadExecutor(String string) {
        this.name = string;
    }

    protected abstract R prepareRunnable(Runnable var1);

    protected abstract boolean canRun(R var1);

    public boolean isOnThread() {
        return Thread.currentThread() == this.getThread();
    }

    protected abstract Thread getThread();

    protected boolean shouldRunAsync() {
        return !this.isOnThread();
    }

    public int method_21684() {
        return this.taskQueue.size();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Environment(value=EnvType.CLIENT)
    public <V> CompletableFuture<V> executeFuture(Supplier<V> supplier) {
        if (this.shouldRunAsync()) {
            return CompletableFuture.supplyAsync(supplier, this);
        }
        return CompletableFuture.completedFuture(supplier.get());
    }

    private CompletableFuture<Void> executeFuture(Runnable runnable) {
        return CompletableFuture.supplyAsync(() -> {
            runnable.run();
            return null;
        }, this);
    }

    public CompletableFuture<Void> method_20493(Runnable runnable) {
        if (this.shouldRunAsync()) {
            return this.executeFuture(runnable);
        }
        runnable.run();
        return CompletableFuture.completedFuture(null);
    }

    public void executeSync(Runnable runnable) {
        if (!this.isOnThread()) {
            this.executeFuture(runnable).join();
        } else {
            runnable.run();
        }
    }

    public void method_18858(R runnable) {
        this.taskQueue.add(runnable);
        LockSupport.unpark(this.getThread());
    }

    @Override
    public void execute(Runnable runnable) {
        if (this.shouldRunAsync()) {
            this.method_18858(this.prepareRunnable(runnable));
        } else {
            runnable.run();
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected void clear() {
        this.taskQueue.clear();
    }

    protected void executeTaskQueue() {
        while (this.executeQueuedTask()) {
        }
    }

    protected boolean executeQueuedTask() {
        Runnable runnable = (Runnable)this.taskQueue.peek();
        if (runnable == null) {
            return false;
        }
        if (this.waitCount == 0 && !this.canRun(runnable)) {
            return false;
        }
        this.runSafely((Runnable)this.taskQueue.remove());
        return true;
    }

    public void waitFor(BooleanSupplier booleanSupplier) {
        ++this.waitCount;
        try {
            while (!booleanSupplier.getAsBoolean()) {
                if (this.executeQueuedTask()) continue;
                this.method_20813();
            }
        } finally {
            --this.waitCount;
        }
    }

    protected void method_20813() {
        Thread.yield();
        LockSupport.parkNanos("waiting for tasks", 100000L);
    }

    protected void runSafely(R runnable) {
        try {
            runnable.run();
        } catch (Exception exception) {
            LOGGER.fatal("Error executing task on {}", (Object)this.getName(), (Object)exception);
        }
    }

    @Override
    public /* synthetic */ void send(Object object) {
        this.method_18858((Runnable)object);
    }
}

