package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
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
			CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries2 = withRegistriesLoaded(
				lifecycledResourceManager, combinedDynamicRegistries, ServerDynamicRegistryType.WORLDGEN, RegistryLoader.DYNAMIC_REGISTRIES
			);
			DynamicRegistryManager.Immutable immutable = combinedDynamicRegistries2.getPrecedingRegistryManagers(ServerDynamicRegistryType.DIMENSIONS);
			DynamicRegistryManager.Immutable immutable2 = RegistryLoader.load(lifecycledResourceManager, immutable, RegistryLoader.DIMENSION_REGISTRIES);
			DataConfiguration dataConfiguration = pair.getFirst();
			SaveLoading.LoadContext<D> loadContext = loadContextSupplier.get(
				new SaveLoading.LoadContextSupplierContext(lifecycledResourceManager, dataConfiguration, immutable, immutable2)
			);
			CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries3 = combinedDynamicRegistries2.with(
				ServerDynamicRegistryType.DIMENSIONS, loadContext.dimensionsRegistryManager
			);
			DynamicRegistryManager.Immutable immutable3 = combinedDynamicRegistries3.getPrecedingRegistryManagers(ServerDynamicRegistryType.RELOADABLE);
			return DataPackContents.reload(
					lifecycledResourceManager,
					immutable3,
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
					dataPackContents.refresh(immutable3);
					return saveApplierFactory.create(lifecycledResourceManager, dataPackContents, combinedDynamicRegistries3, loadContext.extraData);
				}, applyExecutor);
		} catch (Exception var15) {
			return CompletableFuture.failedFuture(var15);
		}
	}

	private static DynamicRegistryManager.Immutable loadDynamicRegistryManager(
		ResourceManager resourceManager,
		CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries,
		ServerDynamicRegistryType type,
		List<RegistryLoader.Entry<?>> entries
	) {
		DynamicRegistryManager.Immutable immutable = combinedDynamicRegistries.getPrecedingRegistryManagers(type);
		return RegistryLoader.load(resourceManager, immutable, entries);
	}

	private static CombinedDynamicRegistries<ServerDynamicRegistryType> withRegistriesLoaded(
		ResourceManager resourceManager,
		CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries,
		ServerDynamicRegistryType type,
		List<RegistryLoader.Entry<?>> entries
	) {
		DynamicRegistryManager.Immutable immutable = loadDynamicRegistryManager(resourceManager, combinedDynamicRegistries, type, entries);
		return combinedDynamicRegistries.with(type, immutable);
	}

	public static record DataPacks(ResourcePackManager manager, DataConfiguration initialDataConfig, boolean safeMode, boolean initMode) {
		public Pair<DataConfiguration, LifecycledResourceManager> load() {
			FeatureSet featureSet = this.initMode ? FeatureFlags.FEATURE_MANAGER.getFeatureSet() : this.initialDataConfig.enabledFeatures();
			DataConfiguration dataConfiguration = MinecraftServer.loadDataPacks(this.manager, this.initialDataConfig.dataPacks(), this.safeMode, featureSet);
			if (!this.initMode) {
				dataConfiguration = dataConfiguration.withFeaturesAdded(this.initialDataConfig.enabledFeatures());
			}

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
		DynamicRegistryManager.Immutable worldGenRegistryManager,
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
