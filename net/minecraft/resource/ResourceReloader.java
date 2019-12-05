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

    public static ResourceReloader<Void> create(ResourceManager resourceManager2, List<ResourceReloadListener> list, Executor executor, Executor executor22, CompletableFuture<Unit> completableFuture) {
        return new ResourceReloader<Void>(executor, executor22, resourceManager2, list, (synchronizer, resourceManager, resourceReloadListener, executor2, executor3) -> resourceReloadListener.reload(synchronizer, resourceManager, DummyProfiler.INSTANCE, DummyProfiler.INSTANCE, executor, executor3), completableFuture);
    }

    protected ResourceReloader(Executor executor, final Executor executor2, ResourceManager resourceManager, List<ResourceReloadListener> list, Factory<S> factory, CompletableFuture<Unit> completableFuture) {
        this.manager = resourceManager;
        this.listenerCount = list.size();
        this.preparingCount.incrementAndGet();
        completableFuture.thenRun(this.preparedCount::incrementAndGet);
        ArrayList<CompletableFuture<S>> list2 = Lists.newArrayList();
        CompletableFuture<Unit> completableFuture2 = completableFuture;
        this.waitingListeners = Sets.newHashSet(list);
        for (final ResourceReloadListener resourceReloadListener : list) {
            final CompletableFuture<Unit> completableFuture3 = completableFuture2;
            CompletableFuture<S> completableFuture4 = factory.create(new ResourceReloadListener.Synchronizer(){

                @Override
                public <T> CompletableFuture<T> whenPrepared(T object) {
                    executor2.execute(() -> {
                        ResourceReloader.this.waitingListeners.remove(resourceReloadListener);
                        if (ResourceReloader.this.waitingListeners.isEmpty()) {
                            ResourceReloader.this.prepareStageFuture.complete(Unit.INSTANCE);
                        }
                    });
                    return ResourceReloader.this.prepareStageFuture.thenCombine((CompletionStage)completableFuture3, (unit, object2) -> object);
                }
            }, resourceManager, resourceReloadListener, runnable -> {
                this.preparingCount.incrementAndGet();
                executor.execute(() -> {
                    runnable.run();
                    this.preparedCount.incrementAndGet();
                });
            }, runnable -> {
                ++this.applyingCount;
                executor2.execute(() -> {
                    runnable.run();
                    ++this.appliedCount;
                });
            });
            list2.add(completableFuture4);
            completableFuture2 = completableFuture4;
        }
        this.applyStageFuture = Util.combine(list2);
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

