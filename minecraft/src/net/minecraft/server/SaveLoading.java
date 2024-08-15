package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.command.CommandManager;
import org.slf4j.Logger;

public class SaveLoading {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static <D, R> CompletableFuture<R> load(
		SaveLoading.ServerConfig serverConfig,
		SaveLoading.LoadContextSupplier<D> loadContextSupplier,
		SaveLoading.SaveApplierFactory<D, R> saveApplierFactory,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		try {
			Pair<DataConfiguration, LifecycledResourceManager> pair = serverConfig.dataPacks.load();
			LifecycledResourceManager lifecycledResourceManager = pair.getSecond();
			CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries = ServerDynamicRegistryType.createCombinedDynamicRegistries();
			List<Registry.PendingTagLoad<?>> list = TagGroupLoader.startReload(
				lifecycledResourceManager, combinedDynamicRegistries.get(ServerDynamicRegistryType.STATIC)
			);
			DynamicRegistryManager.Immutable immutable = combinedDynamicRegistries.getPrecedingRegistryManagers(ServerDynamicRegistryType.WORLDGEN);
			List<RegistryWrapper.Impl<?>> list2 = TagGroupLoader.collectRegistries(immutable, list);
			DynamicRegistryManager.Immutable immutable2 = RegistryLoader.loadFromResource(lifecycledResourceManager, list2, RegistryLoader.DYNAMIC_REGISTRIES);
			List<RegistryWrapper.Impl<?>> list3 = Stream.concat(list2.stream(), immutable2.stream()).toList();
			DynamicRegistryManager.Immutable immutable3 = RegistryLoader.loadFromResource(lifecycledResourceManager, list3, RegistryLoader.DIMENSION_REGISTRIES);
			DataConfiguration dataConfiguration = pair.getFirst();
			RegistryWrapper.WrapperLookup wrapperLookup = RegistryWrapper.WrapperLookup.of(list3.stream());
			SaveLoading.LoadContext<D> loadContext = loadContextSupplier.get(
				new SaveLoading.LoadContextSupplierContext(lifecycledResourceManager, dataConfiguration, wrapperLookup, immutable3)
			);
			CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries2 = combinedDynamicRegistries.with(
				ServerDynamicRegistryType.WORLDGEN, immutable2, loadContext.dimensionsRegistryManager
			);
			return DataPackContents.reload(
					lifecycledResourceManager,
					combinedDynamicRegistries2,
					list,
					dataConfiguration.enabledFeatures(),
					serverConfig.commandEnvironment(),
					serverConfig.functionPermissionLevel(),
					prepareExecutor,
					applyExecutor
				)
				.whenComplete((dataPackContents, throwable) -> {
					if (throwable != null) {
						lifecycledResourceManager.close();
					}
				})
				.thenApplyAsync(dataPackContents -> {
					dataPackContents.applyPendingTagLoads();
					return saveApplierFactory.create(lifecycledResourceManager, dataPackContents, combinedDynamicRegistries2, loadContext.extraData);
				}, applyExecutor);
		} catch (Exception var18) {
			return CompletableFuture.failedFuture(var18);
		}
	}

	public static record DataPacks(ResourcePackManager manager, DataConfiguration initialDataConfig, boolean safeMode, boolean initMode) {
		public Pair<DataConfiguration, LifecycledResourceManager> load() {
			DataConfiguration dataConfiguration = MinecraftServer.loadDataPacks(this.manager, this.initialDataConfig, this.initMode, this.safeMode);
			List<ResourcePack> list = this.manager.createResourcePacks();
			LifecycledResourceManager lifecycledResourceManager = new LifecycledResourceManagerImpl(ResourceType.SERVER_DATA, list);
			return Pair.of(dataConfiguration, lifecycledResourceManager);
		}
	}

	public static record LoadContext<D>(D extraData, DynamicRegistryManager.Immutable dimensionsRegistryManager) {
	}

	@FunctionalInterface
	public interface LoadContextSupplier<D> {
		SaveLoading.LoadContext<D> get(SaveLoading.LoadContextSupplierContext context);
	}

	public static record LoadContextSupplierContext(
		ResourceManager resourceManager,
		DataConfiguration dataConfiguration,
		RegistryWrapper.WrapperLookup worldGenRegistryManager,
		DynamicRegistryManager.Immutable dimensionsRegistryManager
	) {
	}

	@FunctionalInterface
	public interface SaveApplierFactory<D, R> {
		R create(
			LifecycledResourceManager resourceManager,
			DataPackContents dataPackContents,
			CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries,
			D loadContext
		);
	}

	public static record ServerConfig(SaveLoading.DataPacks dataPacks, CommandManager.RegistrationEnvironment commandEnvironment, int functionPermissionLevel) {
	}
}
