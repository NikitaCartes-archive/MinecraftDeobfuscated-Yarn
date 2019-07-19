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
		CompletableFuture<Map<Identifier, Tag.Builder<Block>>> completableFuture = this.blocks.prepareReload(manager, prepareExecutor);
		CompletableFuture<Map<Identifier, Tag.Builder<Item>>> completableFuture2 = this.items.prepareReload(manager, prepareExecutor);
		CompletableFuture<Map<Identifier, Tag.Builder<Fluid>>> completableFuture3 = this.fluids.prepareReload(manager, prepareExecutor);
		CompletableFuture<Map<Identifier, Tag.Builder<EntityType<?>>>> completableFuture4 = this.entityTypes.prepareReload(manager, prepareExecutor);
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
			}, applyExecutor);
	}

	public static class BuilderHolder {
		final Map<Identifier, Tag.Builder<Block>> blocks;
		final Map<Identifier, Tag.Builder<Item>> items;
		final Map<Identifier, Tag.Builder<Fluid>> fluids;
		final Map<Identifier, Tag.Builder<EntityType<?>>> entityTypes;

		public BuilderHolder(
			Map<Identifier, Tag.Builder<Block>> blocks,
			Map<Identifier, Tag.Builder<Item>> items,
			Map<Identifier, Tag.Builder<Fluid>> fluids,
			Map<Identifier, Tag.Builder<EntityType<?>>> entityTypes
		) {
			this.blocks = blocks;
			this.items = items;
			this.fluids = fluids;
			this.entityTypes = entityTypes;
		}
	}
}
