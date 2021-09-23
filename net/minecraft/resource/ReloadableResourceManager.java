/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Unit;

/**
 * A resource manager that has a reload mechanism. Reloading allows
 * reloaders to update when resources change. Accessing resources in
 * reloads can reduce impact on game performance as well.
 * 
 * <p>In each reload, all reloaders in this resource manager will have
 * their {@linkplain ResourceReloader#reload reload} called.
 * 
 * @see ResourceReloader
 */
public interface ReloadableResourceManager
extends ResourceManager,
AutoCloseable {
    /**
     * Performs a reload. This returns a future that is completed when the
     * reload is completed.
     * 
     * @return the future of the reload
     * @see #reload(Executor, Executor, CompletableFuture, List)
     * 
     * @param initialStage a completable future to be completed before this reload
     * @param applyExecutor an executor for the apply stage
     * @param packs a list of resource packs providing resources
     * @param prepareExecutor an executor for the prepare stage
     */
    default public CompletableFuture<Unit> reload(Executor prepareExecutor, Executor applyExecutor, List<ResourcePack> packs, CompletableFuture<Unit> initialStage) {
        return this.reload(prepareExecutor, applyExecutor, initialStage, packs).whenComplete();
    }

    /**
     * Performs a reload. Returns an object that yields some insights to the
     * reload.
     * 
     * <p>{@code prepareExecutor} may be asynchronous. {@code applyExecutor} must
     * synchronize with the game engine so changes are properly made to it.
     * The reload will only begin after {@code initialStage} has completed.
     * Earlier elements in {@code packs} have lower priorities.
     * 
     * @return the reload
     * @see ResourceReloader#reload
     * 
     * @param initialStage a completable future to be completed before this reload
     * @param applyExecutor an executor for the apply stage
     * @param packs a list of resource packs providing resources
     * @param prepareExecutor an executor for the prepare stage
     */
    public ResourceReload reload(Executor var1, Executor var2, CompletableFuture<Unit> var3, List<ResourcePack> var4);

    /**
     * Registers a resource reloader to this manager.
     * 
     * @param reloader the reloader
     */
    public void registerReloader(ResourceReloader var1);

    @Override
    public void close();
}

