/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.server.DataPackContents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.registry.DynamicRegistryManager;

public class SaveLoading {
    public static <D, R> CompletableFuture<R> load(ServerConfig serverConfig, LoadContextSupplier<D> loadContextSupplier, SaveApplierFactory<D, R> saveApplierFactory, Executor prepareExecutor, Executor applyExecutor) {
        try {
            Pair<DataPackSettings, LifecycledResourceManager> pair = serverConfig.dataPacks.load();
            LifecycledResourceManager lifecycledResourceManager = pair.getSecond();
            Pair<D, DynamicRegistryManager.Immutable> pair2 = loadContextSupplier.get(lifecycledResourceManager, pair.getFirst());
            Object object = pair2.getFirst();
            DynamicRegistryManager.Immutable immutable = pair2.getSecond();
            return ((CompletableFuture)DataPackContents.reload(lifecycledResourceManager, immutable, serverConfig.commandEnvironment(), serverConfig.functionPermissionLevel(), prepareExecutor, applyExecutor).whenComplete((dataPackContents, throwable) -> {
                if (throwable != null) {
                    lifecycledResourceManager.close();
                }
            })).thenApplyAsync(dataPackContents -> {
                dataPackContents.refresh(immutable);
                return saveApplierFactory.create(lifecycledResourceManager, (DataPackContents)dataPackContents, immutable, object);
            }, applyExecutor);
        } catch (Exception exception) {
            return CompletableFuture.failedFuture(exception);
        }
    }

    public record ServerConfig(DataPacks dataPacks, CommandManager.RegistrationEnvironment commandEnvironment, int functionPermissionLevel) {
    }

    public record DataPacks(ResourcePackManager manager, DataPackSettings settings, boolean safeMode) {
        public Pair<DataPackSettings, LifecycledResourceManager> load() {
            DataPackSettings dataPackSettings = MinecraftServer.loadDataPacks(this.manager, this.settings, this.safeMode);
            List<ResourcePack> list = this.manager.createResourcePacks();
            LifecycledResourceManagerImpl lifecycledResourceManager = new LifecycledResourceManagerImpl(ResourceType.SERVER_DATA, list);
            return Pair.of(dataPackSettings, lifecycledResourceManager);
        }
    }

    @FunctionalInterface
    public static interface LoadContextSupplier<D> {
        public Pair<D, DynamicRegistryManager.Immutable> get(ResourceManager var1, DataPackSettings var2);
    }

    @FunctionalInterface
    public static interface SaveApplierFactory<D, R> {
        public R create(LifecycledResourceManager var1, DataPackContents var2, DynamicRegistryManager.Immutable var3, D var4);
    }
}

