/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class TagManagerLoader
implements ResourceReloadListener {
    private final TagGroupLoader<Block> blocks = new TagGroupLoader(Registry.BLOCK::getOrEmpty, "tags/blocks", "block");
    private final TagGroupLoader<Item> items = new TagGroupLoader(Registry.ITEM::getOrEmpty, "tags/items", "item");
    private final TagGroupLoader<Fluid> fluids = new TagGroupLoader(Registry.FLUID::getOrEmpty, "tags/fluids", "fluid");
    private final TagGroupLoader<EntityType<?>> entityTypes = new TagGroupLoader(Registry.ENTITY_TYPE::getOrEmpty, "tags/entity_types", "entity_type");
    private final TagGroupLoader<GameEvent> field_28094 = new TagGroupLoader(Registry.GAME_EVENT::getOrEmpty, "tags/game_events", "game_event");
    private TagManager tagManager = TagManager.EMPTY;

    public TagManager getTagManager() {
        return this.tagManager;
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture = this.blocks.prepareReload(manager, prepareExecutor);
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture2 = this.items.prepareReload(manager, prepareExecutor);
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture3 = this.fluids.prepareReload(manager, prepareExecutor);
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture4 = this.entityTypes.prepareReload(manager, prepareExecutor);
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture5 = this.field_28094.prepareReload(manager, prepareExecutor);
        return ((CompletableFuture)CompletableFuture.allOf(completableFuture, completableFuture2, completableFuture3, completableFuture4).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(void_ -> {
            TagGroup<GameEvent> tagGroup5;
            TagGroup<EntityType<?>> tagGroup4;
            TagGroup<Fluid> tagGroup3;
            TagGroup<Item> tagGroup2;
            TagGroup<Block> tagGroup = this.blocks.applyReload((Map)completableFuture.join());
            TagManager tagManager = TagManager.create(tagGroup, tagGroup2 = this.items.applyReload((Map)completableFuture2.join()), tagGroup3 = this.fluids.applyReload((Map)completableFuture3.join()), tagGroup4 = this.entityTypes.applyReload((Map)completableFuture4.join()), tagGroup5 = this.field_28094.applyReload((Map)completableFuture5.join()));
            Multimap<Identifier, Identifier> multimap = RequiredTagListRegistry.getMissingTags(tagManager);
            if (!multimap.isEmpty()) {
                throw new IllegalStateException("Missing required tags: " + multimap.entries().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).sorted().collect(Collectors.joining(",")));
            }
            ServerTagManagerHolder.setTagManager(tagManager);
            this.tagManager = tagManager;
        }, applyExecutor);
    }
}

