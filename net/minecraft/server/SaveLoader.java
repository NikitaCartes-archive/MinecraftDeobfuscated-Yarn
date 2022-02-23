/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
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
import net.minecraft.server.DataPackContents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;

public record SaveLoader(LifecycledResourceManager resourceManager, DataPackContents dataPackContents, DynamicRegistryManager.Immutable dynamicRegistryManager, SaveProperties saveProperties) implements AutoCloseable
{
    public static CompletableFuture<SaveLoader> ofLoaded(FunctionLoaderConfig functionLoaderConfig, DataPackSettingsSupplier dataPackSettingsSupplier, SavePropertiesSupplier savePropertiesSupplier, Executor prepareExecutor, Executor applyExecutor) {
        try {
            DataPackSettings dataPackSettings = (DataPackSettings)dataPackSettingsSupplier.get();
            DataPackSettings dataPackSettings2 = MinecraftServer.loadDataPacks(functionLoaderConfig.dataPackManager(), dataPackSettings, functionLoaderConfig.safeMode());
            List<ResourcePack> list = functionLoaderConfig.dataPackManager().createResourcePacks();
            LifecycledResourceManagerImpl lifecycledResourceManager = new LifecycledResourceManagerImpl(ResourceType.SERVER_DATA, list);
            Pair<SaveProperties, DynamicRegistryManager.Immutable> pair = savePropertiesSupplier.get(lifecycledResourceManager, dataPackSettings2);
            SaveProperties saveProperties = pair.getFirst();
            DynamicRegistryManager.Immutable immutable = pair.getSecond();
            return ((CompletableFuture)DataPackContents.reload(lifecycledResourceManager, immutable, functionLoaderConfig.commandEnvironment(), functionLoaderConfig.functionPermissionLevel(), prepareExecutor, applyExecutor).whenComplete((dataPackContents, throwable) -> {
                if (throwable != null) {
                    lifecycledResourceManager.close();
                }
            })).thenApply(dataPackContents -> new SaveLoader(lifecycledResourceManager, (DataPackContents)dataPackContents, immutable, saveProperties));
        } catch (Exception exception) {
            return CompletableFuture.failedFuture(exception);
        }
    }

    @Override
    public void close() {
        this.resourceManager.close();
    }

    public void refresh() {
        this.dataPackContents.refresh(this.dynamicRegistryManager);
    }

    @FunctionalInterface
    public static interface DataPackSettingsSupplier
    extends Supplier<DataPackSettings> {
        public static DataPackSettingsSupplier loadFromWorld(LevelStorage.Session session) {
            return () -> {
                DataPackSettings dataPackSettings = session.getDataPackSettings();
                if (dataPackSettings == null) {
                    throw new IllegalStateException("Failed to load data pack config");
                }
                return dataPackSettings;
            };
        }
    }

    public record FunctionLoaderConfig(ResourcePackManager dataPackManager, CommandManager.RegistrationEnvironment commandEnvironment, int functionPermissionLevel, boolean safeMode) {
    }

    @FunctionalInterface
    public static interface SavePropertiesSupplier {
        public Pair<SaveProperties, DynamicRegistryManager.Immutable> get(ResourceManager var1, DataPackSettings var2);

        public static SavePropertiesSupplier loadFromWorld(LevelStorage.Session session) {
            return (resourceManager, dataPackSettings) -> {
                DynamicRegistryManager.Mutable mutable = DynamicRegistryManager.createAndLoad();
                RegistryOps<NbtElement> dynamicOps = RegistryOps.ofLoaded(NbtOps.INSTANCE, mutable, resourceManager);
                SaveProperties saveProperties = session.readLevelProperties(dynamicOps, dataPackSettings, mutable.getRegistryLifecycle());
                if (saveProperties == null) {
                    throw new IllegalStateException("Failed to load world");
                }
                return Pair.of(saveProperties, mutable.toImmutable());
            };
        }
    }
}

