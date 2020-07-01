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
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

public class TagManagerLoader implements ResourceReloadListener {
	private final TagGroupLoader<Block> blocks = new TagGroupLoader<>(Registry.BLOCK::getOrEmpty, "tags/blocks", "block");
	private final TagGroupLoader<Item> items = new TagGroupLoader<>(Registry.ITEM::getOrEmpty, "tags/items", "item");
	private final TagGroupLoader<Fluid> fluids = new TagGroupLoader<>(Registry.FLUID::getOrEmpty, "tags/fluids", "fluid");
	private final TagGroupLoader<EntityType<?>> entityTypes = new TagGroupLoader<>(Registry.ENTITY_TYPE::getOrEmpty, "tags/entity_types", "entity_type");
	private TagManager tagManager = TagManager.EMPTY;

	public TagManager getTagManager() {
		return this.tagManager;
	}

	@Override
	public CompletableFuture<Void> reload(
		ResourceReloadListener.Synchronizer synchronizer,
		ResourceManager manager,
		Profiler prepareProfiler,
		Profiler applyProfiler,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture = this.blocks.prepareReload(manager, prepareExecutor);
		CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture2 = this.items.prepareReload(manager, prepareExecutor);
		CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture3 = this.fluids.prepareReload(manager, prepareExecutor);
		CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture4 = this.entityTypes.prepareReload(manager, prepareExecutor);
		return CompletableFuture.allOf(completableFuture, completableFuture2, completableFuture3, completableFuture4)
			.thenCompose(synchronizer::whenPrepared)
			.thenAcceptAsync(
				void_ -> {
					TagGroup<Block> tagGroup = this.blocks.applyReload((Map<Identifier, Tag.Builder>)completableFuture.join());
					TagGroup<Item> tagGroup2 = this.items.applyReload((Map<Identifier, Tag.Builder>)completableFuture2.join());
					TagGroup<Fluid> tagGroup3 = this.fluids.applyReload((Map<Identifier, Tag.Builder>)completableFuture3.join());
					TagGroup<EntityType<?>> tagGroup4 = this.entityTypes.applyReload((Map<Identifier, Tag.Builder>)completableFuture4.join());
					TagManager tagManager = TagManager.create(tagGroup, tagGroup2, tagGroup3, tagGroup4);
					Multimap<Identifier, Identifier> multimap = RequiredTagListRegistry.getMissingTags(tagManager);
					if (!multimap.isEmpty()) {
						throw new IllegalStateException(
							"Missing required tags: "
								+ (String)multimap.entries().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).sorted().collect(Collectors.joining(","))
						);
					} else {
						ServerTagManagerHolder.setTagManager(tagManager);
						this.tagManager = tagManager;
					}
				},
				applyExecutor
			);
	}
}
