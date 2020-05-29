package net.minecraft.tag;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

public class RegistryTagManager implements ResourceReloadListener {
	private final RegistryTagContainer<Block> blocks = new RegistryTagContainer<>(Registry.BLOCK, "tags/blocks", "block");
	private final RegistryTagContainer<Item> items = new RegistryTagContainer<>(Registry.ITEM, "tags/items", "item");
	private final RegistryTagContainer<Fluid> fluids = new RegistryTagContainer<>(Registry.FLUID, "tags/fluids", "fluid");
	private final RegistryTagContainer<EntityType<?>> entityTypes = new RegistryTagContainer<>(Registry.ENTITY_TYPE, "tags/entity_types", "entity_type");

	public RegistryTagContainer<Block> blocks() {
		return this.blocks;
	}

	public RegistryTagContainer<Item> items() {
		return this.items;
	}

	public RegistryTagContainer<Fluid> fluids() {
		return this.fluids;
	}

	public RegistryTagContainer<EntityType<?>> entityTypes() {
		return this.entityTypes;
	}

	public void toPacket(PacketByteBuf buf) {
		this.blocks.toPacket(buf);
		this.items.toPacket(buf);
		this.fluids.toPacket(buf);
		this.entityTypes.toPacket(buf);
	}

	public static RegistryTagManager fromPacket(PacketByteBuf buf) {
		RegistryTagManager registryTagManager = new RegistryTagManager();
		registryTagManager.blocks().fromPacket(buf);
		registryTagManager.items().fromPacket(buf);
		registryTagManager.fluids().fromPacket(buf);
		registryTagManager.entityTypes().fromPacket(buf);
		return registryTagManager;
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
					this.blocks.applyReload((Map<Identifier, Tag.Builder>)completableFuture.join());
					this.items.applyReload((Map<Identifier, Tag.Builder>)completableFuture2.join());
					this.fluids.applyReload((Map<Identifier, Tag.Builder>)completableFuture3.join());
					this.entityTypes.applyReload((Map<Identifier, Tag.Builder>)completableFuture4.join());
					TagContainers.method_29219(this.blocks, this.items, this.fluids, this.entityTypes);
					Multimap<String, Identifier> multimap = HashMultimap.create();
					multimap.putAll("blocks", BlockTags.method_29214(this.blocks));
					multimap.putAll("items", ItemTags.method_29217(this.items));
					multimap.putAll("fluids", FluidTags.method_29216(this.fluids));
					multimap.putAll("entity_types", EntityTypeTags.method_29215(this.entityTypes));
					if (!multimap.isEmpty()) {
						throw new IllegalStateException(
							"Missing required tags: "
								+ (String)multimap.entries().stream().map(entry -> (String)entry.getKey() + ":" + entry.getValue()).sorted().collect(Collectors.joining(","))
						);
					}
				},
				applyExecutor
			);
	}

	public void method_29226() {
		BlockTags.setContainer(this.blocks);
		ItemTags.setContainer(this.items);
		FluidTags.setContainer(this.fluids);
		EntityTypeTags.setContainer(this.entityTypes);
		Blocks.refreshShapeCache();
	}
}
