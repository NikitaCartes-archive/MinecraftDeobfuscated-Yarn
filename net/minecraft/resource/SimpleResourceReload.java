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
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.DummyProfiler;

/**
 * A simple implementation of resource reload.
 * 
 * @param <S> the result type for each reloader in the reload
 */
public class SimpleResourceReload<S>
implements ResourceReload {
    protected final ResourceManager manager;
    protected final CompletableFuture<Unit> prepareStageFuture = new CompletableFuture();
    protected final CompletableFuture<List<S>> applyStageFuture;
    private final Set<ResourceReloader> waitingReloaders;
    private final int reloaderCount;
    private int toApplyCount;
    private int appliedCount;
    private final AtomicInteger toPrepareCount = new AtomicInteger();
    private final AtomicInteger preparedCount = new AtomicInteger();

    /**
     * Creates a simple resource reload without additional results.
     */
    public static SimpleResourceReload<Void> create(ResourceManager manager, List<ResourceReloader> reloaders, Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage) {
        return new SimpleResourceReload<Void>(prepareExecutor, applyExecutor, manager, reloaders, (synchronizer, resourceManager, resourceReloader, executor2, executor3) -> resourceReloader.reload(synchronizer, resourceManager, DummyProfiler.INSTANCE, DummyProfiler.INSTANCE, prepareExecutor, executor3), initialStage);
    }

    protected SimpleResourceReload(Executor prepareExecutor, final Executor applyExecutor, ResourceManager manager, List<ResourceReloader> reloaders, Factory<S> factory, CompletableFuture<Unit> initialStage) {
        this.manager = manager;
        this.reloaderCount = reloaders.size();
        this.toPrepareCount.incrementAndGet();
        initialStage.thenRun(this.preparedCount::incrementAndGet);
        ArrayList<CompletableFuture<S>> list = Lists.newArrayList();
        CompletableFuture<Unit> completableFuture = initialStage;
        this.waitingReloaders = Sets.newHashSet(reloaders);
        for (final ResourceReloader resourceReloader : reloaders) {
            final CompletableFuture<Unit> completableFuture2 = completableFuture;
            CompletableFuture<S> completableFuture3 = factory.create(new ResourceReloader.Synchronizer(){

                @Override
                public <T> CompletableFuture<T> whenPrepared(T preparedObject) {
                    applyExecutor.execute(() -> {
                        SimpleResourceReload.this.waitingReloaders.remove(resourceReloader);
                        if (SimpleResourceReload.this.waitingReloaders.isEmpty()) {
                            SimpleResourceReload.this.prepareStageFuture.complete(Unit.INSTANCE);
                        }
                    });
                    return SimpleResourceReload.this.prepareStageFuture.thenCombine((CompletionStage)completableFuture2, (unit, object2) -> preparedObject);
                }
            }, manager, resourceReloader, runnable -> {
                this.toPrepareCount.incrementAndGet();
                prepareExecutor.execute(() -> {
                    runnable.run();
                    this.preparedCount.incrementAndGet();
                });
            }, runnable -> {
                ++this.toApplyCount;
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
        int i = this.reloaderCount - this.waitingReloaders.size();
        float f = this.preparedCount.get() * 2 + this.appliedCount * 2 + i * 1;
        float g = this.toPrepareCount.get() * 2 + this.toApplyCount * 2 + this.reloaderCount * 1;
        return f / g;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isPrepareStageComplete() {
        return this.prepareStageFuture.isDone();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isComplete() {
        return this.applyStageFuture.isDone();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void throwException() {
        if (this.applyStageFuture.isCompletedExceptionally()) {
            this.applyStageFuture.join();
        }
    }

    public static interface Factory<S> {
        public CompletableFuture<S> create(ResourceReloader.Synchronizer var1, ResourceManager var2, ResourceReloader var3, Executor var4, Executor var5);
    }
}

