/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.DummyProfiler;

public class ResourceReloader<S>
implements ResourceReloadMonitor {
    protected final ResourceManager manager;
    protected final CompletableFuture<Unit> prepareStageFuture = new CompletableFuture();
    protected final CompletableFuture<List<S>> applyStageFuture;
    private final Set<ResourceReloadListener> waitingListeners;
    private final int listenerCount;
    private int applyingCount;
    private int appliedCount;
    private final AtomicInteger preparingCount = new AtomicInteger();
    private final AtomicInteger preparedCount = new AtomicInteger();

    public static ResourceReloader<Void> create(ResourceManager manager, List<ResourceReloadListener> listeners, Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage) {
        return new ResourceReloader<Void>(prepareExecutor, applyExecutor, manager, listeners, (synchronizer, resourceManager, resourceReloadListener, executor2, executor3) -> resourceReloadListener.reload(synchronizer, resourceManager, DummyProfiler.INSTANCE, DummyProfiler.INSTANCE, prepareExecutor, executor3), initialStage);
    }

    protected ResourceReloader(Executor prepareExecutor, final Executor applyExecutor, ResourceManager manager, List<ResourceReloadListener> listeners, Factory<S> creator, CompletableFuture<Unit> initialStage) {
        this.manager = manager;
        this.listenerCount = listeners.size();
        this.preparingCount.incrementAndGet();
        initialStage.thenRun(this.preparedCount::incrementAndGet);
        ArrayList<CompletableFuture<S>> list = Lists.newArrayList();
        CompletableFuture<Unit> completableFuture = initialStage;
        this.waitingListeners = Sets.newHashSet(listeners);
        for (final ResourceReloadListener resourceReloadListener : listeners) {
            final CompletableFuture<Unit> completableFuture2 = completableFuture;
            CompletableFuture<S> completableFuture3 = creator.create(new ResourceReloadListener.Synchronizer(){

                @Override
                public <T> CompletableFuture<T> whenPrepared(T preparedObject) {
                    applyExecutor.execute(() -> {
                        ResourceReloader.this.waitingListeners.remove(resourceReloadListener);
                        if (ResourceReloader.this.waitingListeners.isEmpty()) {
                            ResourceReloader.this.prepareStageFuture.complete(Unit.INSTANCE);
                        }
                    });
                    return ResourceReloader.this.prepareStageFuture.thenCombine((CompletionStage)completableFuture2, (unit, object2) -> preparedObject);
                }
            }, manager, resourceReloadListener, runnable -> {
                this.preparingCount.incrementAndGet();
                prepareExecutor.execute(() -> {
                    runnable.run();
                    this.preparedCount.incrementAndGet();
                });
            }, runnable -> {
                ++this.applyingCount;
                applyExecutor.execute(() -> {
                    runnable.run();
                    ++this.appliedCount;
                });
            });
            list.add(completableFuture3);
            completableFuture = completableFuture3;
        }
        this.applyStageFuture = Util.combine(list);
    }

    @Override
    public CompletableFuture<Unit> whenComplete() {
        return this.applyStageFuture.thenApply(list -> Unit.INSTANCE);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public float getProgress() {
        int i = this.listenerCount - this.waitingListeners.size();
        float f = this.preparedCount.get() * 2 + this.appliedCount * 2 + i * 1;
        float g = this.preparingCount.get() * 2 + this.applyingCount * 2 + this.listenerCount * 1;
        return f / g;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isPrepareStageComplete() {
        return this.prepareStageFuture.isDone();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isApplyStageComplete() {
        return this.applyStageFuture.isDone();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void throwExceptions() {
        if (this.applyStageFuture.isCompletedExceptionally()) {
            this.applyStageFuture.join();
        }
    }

    public static interface Factory<S> {
        public CompletableFuture<S> create(ResourceReloadListener.Synchronizer var1, ResourceManager var2, ResourceReloadListener var3, Executor var4, Executor var5);
    }
}

