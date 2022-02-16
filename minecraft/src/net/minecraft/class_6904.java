package net.minecraft;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;

public record class_6904(
	LifecycledResourceManager resourceManager, ServerResourceManager dataPackResources, DynamicRegistryManager.Immutable registryAccess, SaveProperties worldData
) implements AutoCloseable {
	public static CompletableFuture<class_6904> method_40431(
		class_6904.class_6906 arg, class_6904.class_6905 arg2, class_6904.class_6907 arg3, Executor executor, Executor executor2
	) {
		try {
			DataPackSettings dataPackSettings = (DataPackSettings)arg2.get();
			DataPackSettings dataPackSettings2 = MinecraftServer.loadDataPacks(arg.packRepository(), dataPackSettings, arg.safeMode());
			List<ResourcePack> list = arg.packRepository().createResourcePacks();
			LifecycledResourceManager lifecycledResourceManager = new LifecycledResourceManagerImpl(ResourceType.SERVER_DATA, list);
			Pair<SaveProperties, DynamicRegistryManager.Immutable> pair = arg3.get(lifecycledResourceManager, dataPackSettings2);
			SaveProperties saveProperties = pair.getFirst();
			DynamicRegistryManager.Immutable immutable = pair.getSecond();
			return ServerResourceManager.reload(lifecycledResourceManager, immutable, arg.commandSelection(), arg.functionCompilationLevel(), executor, executor2)
				.whenComplete((serverResourceManager, throwable) -> {
					if (throwable != null) {
						lifecycledResourceManager.close();
					}
				})
				.thenApply(serverResourceManager -> new class_6904(lifecycledResourceManager, serverResourceManager, immutable, saveProperties));
		} catch (Exception var12) {
			return CompletableFuture.failedFuture(var12);
		}
	}

	public void close() {
		this.resourceManager.close();
	}

	public void method_40428() {
		this.dataPackResources.method_40421(this.registryAccess);
	}

	@FunctionalInterface
	public interface class_6905 extends Supplier<DataPackSettings> {
		static class_6904.class_6905 loadFromWorld(LevelStorage.Session session) {
			return () -> {
				DataPackSettings dataPackSettings = session.getDataPackSettings();
				if (dataPackSettings == null) {
					throw new IllegalStateException("Failed to load data pack config");
				} else {
					return dataPackSettings;
				}
			};
		}
	}

	public static record class_6906(
		ResourcePackManager packRepository, CommandManager.RegistrationEnvironment commandSelection, int functionCompilationLevel, boolean safeMode
	) {
	}

	@FunctionalInterface
	public interface class_6907 {
		Pair<SaveProperties, DynamicRegistryManager.Immutable> get(ResourceManager resourceManager, DataPackSettings dataPackSettings);

		static class_6904.class_6907 loadFromWorld(LevelStorage.Session session) {
			return (resourceManager, dataPackSettings) -> {
				DynamicRegistryManager.Mutable mutable = DynamicRegistryManager.createAndLoad();
				DynamicOps<NbtElement> dynamicOps = RegistryOps.ofLoaded(NbtOps.INSTANCE, mutable, resourceManager);
				SaveProperties saveProperties = session.readLevelProperties(dynamicOps, dataPackSettings);
				if (saveProperties == null) {
					throw new IllegalStateException("Failed to load world");
				} else {
					return Pair.of(saveProperties, mutable.toImmutable());
				}
			};
		}
	}
}
