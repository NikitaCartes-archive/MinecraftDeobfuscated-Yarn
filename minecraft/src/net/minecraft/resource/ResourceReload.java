package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import net.minecraft.util.Unit;

/**
 * Represents a resource reload.
 * 
 * @see ReloadableResourceManager#reload(java.util.concurrent.Executor,
 * java.util.concurrent.Executor, CompletableFuture, java.util.List)
 */
public interface ResourceReload {
	/**
	 * Returns a future for the reload. The returned future is completed when
	 * the reload completes.
	 */
	CompletableFuture<Unit> whenComplete();

	/**
	 * Returns a fraction between 0 and 1 indicating the progress of this
	 * reload.
	 */
	float getProgress();

	/**
	 * Returns if this reload has completed, either normally or abnormally.
	 */
	boolean isComplete();

	/**
	 * Throws an unchecked exception from this reload, if there is any. Does
	 * nothing if the reload has not completed or terminated.
	 */
	void throwException();
}
