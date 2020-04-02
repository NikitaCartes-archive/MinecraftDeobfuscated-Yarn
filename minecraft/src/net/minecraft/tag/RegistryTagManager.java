package net.minecraft.tag;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.block.Block;
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
			.thenAcceptAsync(void_ -> {
				this.blocks.applyReload((Map<Identifier, Tag.Builder>)completableFuture.join());
				this.items.applyReload((Map<Identifier, Tag.Builder>)completableFuture2.join());
				this.fluids.applyReload((Map<Identifier, Tag.Builder>)completableFuture3.join());
				this.entityTypes.applyReload((Map<Identifier, Tag.Builder>)completableFuture4.join());
				BlockTags.setContainer(this.blocks);
				ItemTags.setContainer(this.items);
				FluidTags.setContainer(this.fluids);
				EntityTypeTags.setContainer(this.entityTypes);
			}, applyExecutor);
	}
}
