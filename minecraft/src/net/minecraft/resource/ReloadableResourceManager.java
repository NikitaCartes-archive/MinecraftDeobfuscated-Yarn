package net.minecraft.resource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
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
public interface ReloadableResourceManager extends ResourceManager, AutoCloseable {
	/**
	 * Performs a reload. This returns a future that is completed when the
	 * reload is completed.
	 * 
	 * @return the future of the reload
	 * @see #reload(Executor, Executor, CompletableFuture, List)
	 * 
	 * @param prepareExecutor an executor for the prepare stage
	 * @param applyExecutor an executor for the apply stage
	 * @param packs a list of resource packs providing resources
	 * @param initialStage a completable future to be completed before this reload
	 */
	default CompletableFuture<Unit> reload(Executor prepareExecutor, Executor applyExecutor, List<ResourcePack> packs, CompletableFuture<Unit> initialStage) {
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
	 * @param prepareExecutor an executor for the prepare stage
	 * @param applyExecutor an executor for the apply stage
	 * @param initialStage a completable future to be completed before this reload
	 * @param packs a list of resource packs providing resources
	 */
	ResourceReload reload(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs);

	/**
	 * Registers a resource reloader to this manager.
	 * 
	 * @param reloader the reloader
	 */
	void registerReloader(ResourceReloader reloader);

	void close();
}
