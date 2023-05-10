package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiler.Profiler;

/**
 * A resource reloader performs actual reloading in its {@linkplain #reload
 * reload} when called by {@link SimpleResourceReload#start}.
 * 
 * @see SimpleResourceReload#start
 * @see SinglePreparationResourceReloader SinglePreparationResourceReloader
 * (completes preparation in one method)
 * @see SynchronousResourceReloader SynchronousResourceReloader
 * (performs all reloading in the apply executor)
 */
public interface ResourceReloader {
	/**
	 * Performs a reload. Returns a future that is completed when the reload
	 * is completed.
	 * 
	 * <p>In a reload, there is a prepare stage and an apply stage. For the
	 * prepare stage, you should create completable futures with {@linkplain
	 * CompletableFuture#supplyAsync(Supplier, Executor)
	 * CompletableFuture.supplyAsync(..., prepareExecutor)}
	 * to ensure the prepare actions are done with the prepare executor. Then,
	 * you should have a completable future for all the prepared actions, and
	 * call {@linkplain CompletableFuture#thenCompose(Function)
	 * combinedPrepare.thenCompose(synchronizer::waitFor)}
	 * to notify the {@code synchronizer}. Finally, you should run {@linkplain
	 * CompletableFuture#thenAcceptAsync(Consumer, Executor)
	 * CompletableFuture.thenAcceptAsync(..., applyExecutor)} for apply actions.
	 * In the end, returns the result of {@code thenAcceptAsync}.
	 * 
	 * @return a future for the reload
	 * @see ReloadableResourceManagerImpl#reload(Executor, Executor,
	 * CompletableFuture, List)
	 * 
	 * @param prepareProfiler the profiler for prepare stage
	 * @param applyProfiler the profiler for apply stage
	 * @param prepareExecutor the executor for prepare stage
	 * @param applyExecutor the executor for apply stage
	 * @param synchronizer the synchronizer
	 * @param manager the resource manager
	 */
	CompletableFuture<Void> reload(
		ResourceReloader.Synchronizer synchronizer,
		ResourceManager manager,
		Profiler prepareProfiler,
		Profiler applyProfiler,
		Executor prepareExecutor,
		Executor applyExecutor
	);

	/**
	 * Returns a user-friendly name for logging.
	 */
	default String getName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * A synchronizer to indicate completion of a reloader's prepare stage and
	 * to allow start of the apply stage only if all reloaders have finished
	 * the prepare stage.
	 */
	public interface Synchronizer {
		/**
		 * Indicates, to the ongoing reload, that this reloader has finished its
		 * preparation stage with the {@code preparedObject} as its result.
		 * 
		 * <p>Returns a completable future that the apply stage depends on. This
		 * returned future is completed when all the reloaders have completed their
		 * prepare stages in the reload.
		 * 
		 * <p>Example:
		 * {@code
		 * CompletableFuture<SomeObject> prepareStage = ...;
		 * prepareStage.thenCompose(synchronizer::whenPrepared)
		 *         .thenAcceptAsync(..., applyExecutor);
		 * }
		 * 
		 * @return a completable future as the precondition for the apply stage
		 * 
		 * @param preparedObject the result of the prepare stage
		 */
		<T> CompletableFuture<T> whenPrepared(T preparedObject);
	}
}
