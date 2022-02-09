/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

public class TagManagerLoader
implements ResourceReloader {
    private static final Map<RegistryKey<? extends Registry<?>>, String> DIRECTORIES = Map.of(Registry.BLOCK_KEY, "tags/blocks", Registry.ENTITY_TYPE_KEY, "tags/entity_types", Registry.FLUID_KEY, "tags/fluids", Registry.GAME_EVENT_KEY, "tags/game_events", Registry.ITEM_KEY, "tags/items");
    private final DynamicRegistryManager registryManager;
    private List<RegistryTags<?>> registryTags = List.of();

    public TagManagerLoader(DynamicRegistryManager registryManager) {
        this.registryManager = registryManager;
    }

    public List<RegistryTags<?>> getRegistryTags() {
        return this.registryTags;
    }

    public static String getPath(RegistryKey<? extends Registry<?>> registry) {
        String string = DIRECTORIES.get(registry);
        if (string != null) {
            return string;
        }
        return "tags/" + registry.getValue().getPath();
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        List<CompletableFuture> list = this.registryManager.streamAllRegistries().map(entry -> this.buildRequiredGroup(manager, prepareExecutor, (DynamicRegistryManager.Entry)entry)).toList();
        return ((CompletableFuture)CompletableFuture.allOf((CompletableFuture[])list.toArray(CompletableFuture[]::new)).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(void_ -> {
            this.registryTags = list.stream().map(CompletableFuture::join).collect(Collectors.toUnmodifiableList());
        }, applyExecutor);
    }

    private <T> CompletableFuture<RegistryTags<T>> buildRequiredGroup(ResourceManager resourceManager, Executor prepareExecutor, DynamicRegistryManager.Entry<T> requirement) {
        RegistryKey registryKey = requirement.key();
        Registry registry = requirement.value();
        TagGroupLoader tagGroupLoader = new TagGroupLoader(identifier -> registry.getEntry(RegistryKey.of(registryKey, identifier)), TagManagerLoader.getPath(registryKey));
        return CompletableFuture.supplyAsync(() -> new RegistryTags(registryKey, tagGroupLoader.load(resourceManager)), prepareExecutor);
    }

    public record RegistryTags<T>(RegistryKey<? extends Registry<T>> key, Map<Identifier, Tag<RegistryEntry<T>>> tags) {
    }
}

