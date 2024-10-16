package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.Unit;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;

/**
 * A base resource reloader that does all its work in the apply executor,
 * or the game engine's thread.
 * 
 * @apiNote This resource reloader is useful as a resource reload callback
 * that doesn't need resource manager access. If you access the resource
 * manager, consider writing resource reloaders that have a proper prepare
 * stage instead by moving resource manager access to the prepare stage.
 * That can speed up resource reloaders significantly.
 */
public interface SynchronousResourceReloader extends ResourceReloader {
	@Override
	default CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {
		return synchronizer.whenPrepared(Unit.INSTANCE).thenRunAsync(() -> {
			Profiler profiler = Profilers.get();
			profiler.push("listener");
			this.reload(manager);
			profiler.pop();
		}, applyExecutor);
	}

	/**
	 * Performs the reload in the apply executor, or the game engine.
	 * 
	 * @param manager the resource manager
	 */
	void reload(ResourceManager manager);
}
