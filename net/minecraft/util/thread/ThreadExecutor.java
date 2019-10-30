/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.thread;

import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.thread.MessageListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ThreadExecutor<R extends Runnable>
implements MessageListener<R>,
Executor {
    private final String name;
    private static final Logger LOGGER = LogManager.getLogger();
    private final Queue<R> tasks = Queues.newConcurrentLinkedQueue();
    private int executionsInProgress;

    protected ThreadExecutor(String string) {
        this.name = string;
    }

    protected abstract R createTask(Runnable var1);

    protected abstract boolean canExecute(R var1);

    public boolean isOnThread() {
        return Thread.currentThread() == this.getThread();
    }

    protected abstract Thread getThread();

    protected boolean shouldExecuteAsync() {
        return !this.isOnThread();
    }

    public int getTaskCount() {
        return this.tasks.size();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Environment(value=EnvType.CLIENT)
    public <V> CompletableFuture<V> submit(Supplier<V> supplier) {
        if (this.shouldExecuteAsync()) {
            return CompletableFuture.supplyAsync(supplier, this);
        }
        return CompletableFuture.completedFuture(supplier.get());
    }

    private CompletableFuture<Void> submitAsync(Runnable runnable) {
        return CompletableFuture.supplyAsync(() -> {
            runnable.run();
            return null;
        }, this);
    }

    public CompletableFuture<Void> submit(Runnable runnable) {
        if (this.shouldExecuteAsync()) {
            return this.submitAsync(runnable);
        }
        runnable.run();
        return CompletableFuture.completedFuture(null);
    }

    public void submitAndJoin(Runnable runnable) {
        if (!this.isOnThread()) {
            this.submitAsync(runnable).join();
        } else {
            runnable.run();
        }
    }

    public void method_18858(R runnable) {
        this.tasks.add(runnable);
        LockSupport.unpark(this.getThread());
    }

    @Override
    public void execute(Runnable runnable) {
        if (this.shouldExecuteAsync()) {
            this.method_18858(this.createTask(runnable));
        } else {
            runnable.run();
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected void cancelTasks() {
        this.tasks.clear();
    }

    protected void runTasks() {
        while (this.runTask()) {
        }
    }

    protected boolean runTask() {
        Runnable runnable = (Runnable)this.tasks.peek();
        if (runnable == null) {
            return false;
        }
        if (this.executionsInProgress == 0 && !this.canExecute(runnable)) {
            return false;
        }
        this.executeTask((Runnable)this.tasks.remove());
        return true;
    }

    public void runTasks(BooleanSupplier booleanSupplier) {
        ++this.executionsInProgress;
        try {
            while (!booleanSupplier.getAsBoolean()) {
                if (this.runTask()) continue;
                this.waitForTasks();
            }
        } finally {
            --this.executionsInProgress;
        }
    }

    protected void waitForTasks() {
        Thread.yield();
        LockSupport.parkNanos("waiting for tasks", 100000L);
    }

    protected void executeTask(R runnable) {
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

