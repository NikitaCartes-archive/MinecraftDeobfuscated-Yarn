/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class TagManagerLoader
implements ResourceReloader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final DynamicRegistryManager registryManager;
    private TagManager tagManager = TagManager.EMPTY;

    public TagManagerLoader(DynamicRegistryManager registryManager) {
        this.registryManager = registryManager;
    }

    public TagManager getTagManager() {
        return this.tagManager;
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        ArrayList list = Lists.newArrayList();
        RequiredTagListRegistry.forEach(requiredTagList -> {
            RequiredGroup requiredGroup = this.buildRequiredGroup(manager, prepareExecutor, (RequiredTagList)requiredTagList);
            if (requiredGroup != null) {
                list.add(requiredGroup);
            }
        });
        return ((CompletableFuture)CompletableFuture.allOf((CompletableFuture[])list.stream().map(requiredGroup -> requiredGroup.groupLoadFuture).toArray(CompletableFuture[]::new)).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(void_ -> {
            TagManager.Builder builder = new TagManager.Builder();
            list.forEach(requiredGroup -> requiredGroup.addTo(builder));
            TagManager tagManager = builder.build();
            Multimap<RegistryKey<Registry<?>>, Identifier> multimap = RequiredTagListRegistry.getMissingTags(tagManager);
            if (!multimap.isEmpty()) {
                throw new IllegalStateException("Missing required tags: " + multimap.entries().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).sorted().collect(Collectors.joining(",")));
            }
            ServerTagManagerHolder.setTagManager(tagManager);
            this.tagManager = tagManager;
        }, applyExecutor);
    }

    @Nullable
    private <T> RequiredGroup<T> buildRequiredGroup(ResourceManager resourceManager, Executor prepareExecutor, RequiredTagList<T> requirement) {
        Optional<Registry<T>> optional = this.registryManager.getOptional(requirement.getRegistryKey());
        if (optional.isPresent()) {
            Registry<T> registry = optional.get();
            TagGroupLoader tagGroupLoader = new TagGroupLoader(registry::getOrEmpty, requirement.getDataType());
            CompletableFuture<TagGroup> completableFuture = CompletableFuture.supplyAsync(() -> tagGroupLoader.load(resourceManager), prepareExecutor);
            return new RequiredGroup<T>(requirement, completableFuture);
        }
        LOGGER.warn("Can't find registry for {}", (Object)requirement.getRegistryKey());
        return null;
    }

    static class RequiredGroup<T> {
        private final RequiredTagList<T> requirement;
        final CompletableFuture<? extends TagGroup<T>> groupLoadFuture;

        RequiredGroup(RequiredTagList<T> requirement, CompletableFuture<? extends TagGroup<T>> groupLoadFuture) {
            this.requirement = requirement;
            this.groupLoadFuture = groupLoadFuture;
        }

        public void addTo(TagManager.Builder builder) {
            builder.add(this.requirement.getRegistryKey(), this.groupLoadFuture.join());
        }
    }
}

