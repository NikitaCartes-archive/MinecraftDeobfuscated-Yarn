package net.minecraft.server;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;

public record SaveLoader(
	LifecycledResourceManager resourceManager,
	DataPackContents dataPackContents,
	DynamicRegistryManager.Immutable dynamicRegistryManager,
	SaveProperties saveProperties
) implements AutoCloseable {
	public static CompletableFuture<SaveLoader> load(
		SaveLoading.ServerConfig serverConfig,
		SaveLoading.LoadContextSupplier<SaveProperties> savePropertiesSupplier,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		return SaveLoading.load(serverConfig, savePropertiesSupplier, SaveLoader::new, prepareExecutor, applyExecutor);
	}

	public void close() {
		this.resourceManager.close();
	}
}
