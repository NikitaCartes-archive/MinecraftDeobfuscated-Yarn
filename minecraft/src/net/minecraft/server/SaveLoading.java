package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.registry.DynamicRegistryManager;

public class SaveLoading {
	public static <D, R> CompletableFuture<R> load(
		SaveLoading.ServerConfig serverConfig,
		SaveLoading.LoadContextSupplier<D> loadContextSupplier,
		SaveLoading.SaveApplierFactory<D, R> saveApplierFactory,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		try {
			Pair<DataPackSettings, LifecycledResourceManager> pair = serverConfig.dataPacks.load();
			LifecycledResourceManager lifecycledResourceManager = pair.getSecond();
			Pair<D, DynamicRegistryManager.Immutable> pair2 = loadContextSupplier.get(lifecycledResourceManager, pair.getFirst());
			D object = pair2.getFirst();
			DynamicRegistryManager.Immutable immutable = pair2.getSecond();
			return DataPackContents.reload(
					lifecycledResourceManager, immutable, serverConfig.commandEnvironment(), serverConfig.functionPermissionLevel(), prepareExecutor, applyExecutor
				)
				.whenComplete((dataPackContents, throwable) -> {
					if (throwable != null) {
						lifecycledResourceManager.close();
					}
				})
				.thenApplyAsync(dataPackContents -> {
					dataPackContents.refresh(immutable);
					return saveApplierFactory.create(lifecycledResourceManager, dataPackContents, immutable, object);
				}, applyExecutor);
		} catch (Exception var10) {
			return CompletableFuture.failedFuture(var10);
		}
	}

	public static record DataPacks(ResourcePackManager manager, DataPackSettings settings, boolean safeMode) {
		public Pair<DataPackSettings, LifecycledResourceManager> load() {
			DataPackSettings dataPackSettings = MinecraftServer.loadDataPacks(this.manager, this.settings, this.safeMode);
			List<ResourcePack> list = this.manager.createResourcePacks();
			LifecycledResourceManager lifecycledResourceManager = new LifecycledResourceManagerImpl(ResourceType.SERVER_DATA, list);
			return Pair.of(dataPackSettings, lifecycledResourceManager);
		}
	}

	@FunctionalInterface
	public interface LoadContextSupplier<D> {
		Pair<D, DynamicRegistryManager.Immutable> get(ResourceManager resourceManager, DataPackSettings dataPackSettings);
	}

	@FunctionalInterface
	public interface SaveApplierFactory<D, R> {
		R create(LifecycledResourceManager resourceManager, DataPackContents dataPackContents, DynamicRegistryManager.Immutable dynamicRegistryManager, D loadContext);
	}

	public static record ServerConfig(SaveLoading.DataPacks dataPacks, CommandManager.RegistrationEnvironment commandEnvironment, int functionPermissionLevel) {
	}
}
