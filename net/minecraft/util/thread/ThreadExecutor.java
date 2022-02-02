/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.util.profiler.SampleType;
import net.minecraft.util.profiler.Sampler;
import net.minecraft.util.thread.ExecutorSampling;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.util.thread.SampleableExecutor;
import org.slf4j.Logger;

public abstract class ThreadExecutor<R extends Runnable>
implements SampleableExecutor,
MessageListener<R>,
Executor {
    private final String name;
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Queue<R> tasks = Queues.newConcurrentLinkedQueue();
    private int executionsInProgress;

    protected ThreadExecutor(String name) {
        this.name = name;
        ExecutorSampling.INSTANCE.add(this);
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

    public <V> CompletableFuture<V> submit(Supplier<V> task) {
        if (this.shouldExecuteAsync()) {
            return CompletableFuture.supplyAsync(task, this);
        }
        return CompletableFuture.completedFuture(task.get());
    }

    private CompletableFuture<Void> submitAsync(Runnable runnable) {
        return CompletableFuture.supplyAsync(() -> {
            runnable.run();
            return null;
        }, this);
    }

    public CompletableFuture<Void> submit(Runnable task) {
        if (this.shouldExecuteAsync()) {
            return this.submitAsync(task);
        }
        task.run();
        return CompletableFuture.completedFuture(null);
    }

    public void submitAndJoin(Runnable runnable) {
        if (!this.isOnThread()) {
            this.submitAsync(runnable).join();
        } else {
            runnable.run();
        }
    }

    @Override
    public void send(R runnable) {
        this.tasks.add(runnable);
        LockSupport.unpark(this.getThread());
    }

    @Override
    public void execute(Runnable runnable) {
        if (this.shouldExecuteAsync()) {
            this.send(this.createTask(runnable));
        } else {
            runnable.run();
        }
    }

    public void executeSync(Runnable runnable) {
        this.execute(runnable);
    }

    protected void cancelTasks() {
        this.tasks.clear();
    }

    protected void runTasks() {
        while (this.runTask()) {
        }
    }

    public boolean runTask() {
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

    public void runTasks(BooleanSupplier stopCondition) {
        ++this.executionsInProgress;
        try {
            while (!stopCondition.getAsBoolean()) {
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

    protected void executeTask(R task) {
        try {
            task.run();
        } catch (Exception exception) {
            LOGGER.error(LogUtils.FATAL_MARKER, "Error executing task on {}", (Object)this.getName(), (Object)exception);
            throw exception;
        }
    }

    @Override
    public List<Sampler> createSamplers() {
        return ImmutableList.of(Sampler.create(this.name + "-pending-tasks", SampleType.EVENT_LOOPS, this::getTaskCount));
    }

    @Override
    public /* synthetic */ void send(Object message) {
        this.send((R)((Runnable)message));
    }
}

