package net.minecraft.tag;

import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
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

	public void toPacket(PacketByteBuf packetByteBuf) {
		this.blocks.toPacket(packetByteBuf);
		this.items.toPacket(packetByteBuf);
		this.fluids.toPacket(packetByteBuf);
		this.entityTypes.toPacket(packetByteBuf);
	}

	public static RegistryTagManager fromPacket(PacketByteBuf packetByteBuf) {
		RegistryTagManager registryTagManager = new RegistryTagManager();
		registryTagManager.blocks().fromPacket(packetByteBuf);
		registryTagManager.items().fromPacket(packetByteBuf);
		registryTagManager.fluids().fromPacket(packetByteBuf);
		registryTagManager.entityTypes().fromPacket(packetByteBuf);
		return registryTagManager;
	}

	@Override
	public CompletableFuture<Void> reload(
		ResourceReloadListener.Synchronizer synchronizer,
		ResourceManager resourceManager,
		Profiler profiler,
		Profiler profiler2,
		Executor executor,
		Executor executor2
	) {
		CompletableFuture<Map<Identifier, Tag.Builder<Block>>> completableFuture = this.blocks.prepareReload(resourceManager, executor);
		CompletableFuture<Map<Identifier, Tag.Builder<Item>>> completableFuture2 = this.items.prepareReload(resourceManager, executor);
		CompletableFuture<Map<Identifier, Tag.Builder<Fluid>>> completableFuture3 = this.fluids.prepareReload(resourceManager, executor);
		CompletableFuture<Map<Identifier, Tag.Builder<EntityType<?>>>> completableFuture4 = this.entityTypes.prepareReload(resourceManager, executor);
		return completableFuture.thenCombine(completableFuture2, Pair::of)
			.thenCombine(
				completableFuture3.thenCombine(completableFuture4, Pair::of),
				(pair, pair2) -> new RegistryTagManager.BuilderHolder(
						(Map<Identifier, Tag.Builder<Block>>)pair.getFirst(),
						(Map<Identifier, Tag.Builder<Item>>)pair.getSecond(),
						(Map<Identifier, Tag.Builder<Fluid>>)pair2.getFirst(),
						(Map<Identifier, Tag.Builder<EntityType<?>>>)pair2.getSecond()
					)
			)
			.thenCompose(synchronizer::whenPrepared)
			.thenAcceptAsync(builderHolder -> {
				this.blocks.applyReload(builderHolder.blocks);
				this.items.applyReload(builderHolder.items);
				this.fluids.applyReload(builderHolder.fluids);
				this.entityTypes.applyReload(builderHolder.entityTypes);
				BlockTags.setContainer(this.blocks);
				ItemTags.setContainer(this.items);
				FluidTags.setContainer(this.fluids);
				EntityTypeTags.setContainer(this.entityTypes);
			}, executor2);
	}

	public static class BuilderHolder {
		final Map<Identifier, Tag.Builder<Block>> blocks;
		final Map<Identifier, Tag.Builder<Item>> items;
		final Map<Identifier, Tag.Builder<Fluid>> fluids;
		final Map<Identifier, Tag.Builder<EntityType<?>>> entityTypes;

		public BuilderHolder(
			Map<Identifier, Tag.Builder<Block>> map,
			Map<Identifier, Tag.Builder<Item>> map2,
			Map<Identifier, Tag.Builder<Fluid>> map3,
			Map<Identifier, Tag.Builder<EntityType<?>>> map4
		) {
			this.blocks = map;
			this.items = map2;
			this.fluids = map3;
			this.entityTypes = map4;
		}
	}
}
