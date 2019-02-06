package net.minecraft.tag;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
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

public class TagManager implements ResourceReloadListener<TagManager.class_4015> {
	private final RegistryTagContainer<Block> blocks = new RegistryTagContainer<>(Registry.BLOCK, "tags/blocks", "block");
	private final RegistryTagContainer<Item> items = new RegistryTagContainer<>(Registry.ITEM, "tags/items", "item");
	private final RegistryTagContainer<Fluid> fluids = new RegistryTagContainer<>(Registry.FLUID, "tags/fluids", "fluid");
	private final RegistryTagContainer<EntityType<?>> entities = new RegistryTagContainer<>(Registry.ENTITY_TYPE, "tags/entity_types", "entity_type");

	public RegistryTagContainer<Block> blocks() {
		return this.blocks;
	}

	public RegistryTagContainer<Item> items() {
		return this.items;
	}

	public RegistryTagContainer<Fluid> fluids() {
		return this.fluids;
	}

	public RegistryTagContainer<EntityType<?>> entities() {
		return this.entities;
	}

	public void clear() {
		this.blocks.clear();
		this.items.clear();
		this.fluids.clear();
		this.entities.clear();
	}

	public void toPacket(PacketByteBuf packetByteBuf) {
		this.blocks.toPacket(packetByteBuf);
		this.items.toPacket(packetByteBuf);
		this.fluids.toPacket(packetByteBuf);
		this.entities.toPacket(packetByteBuf);
	}

	public static TagManager fromPacket(PacketByteBuf packetByteBuf) {
		TagManager tagManager = new TagManager();
		tagManager.blocks().fromPacket(packetByteBuf);
		tagManager.items().fromPacket(packetByteBuf);
		tagManager.fluids().fromPacket(packetByteBuf);
		tagManager.entities().fromPacket(packetByteBuf);
		return tagManager;
	}

	@Override
	public CompletableFuture<TagManager.class_4015> prepare(ResourceManager resourceManager, Profiler profiler) {
		CompletableFuture<Map<Identifier, Tag.Builder<Block>>> completableFuture = this.blocks.prepareReload(resourceManager);
		CompletableFuture<Map<Identifier, Tag.Builder<Item>>> completableFuture2 = this.items.prepareReload(resourceManager);
		CompletableFuture<Map<Identifier, Tag.Builder<Fluid>>> completableFuture3 = this.fluids.prepareReload(resourceManager);
		CompletableFuture<Map<Identifier, Tag.Builder<EntityType<?>>>> completableFuture4 = this.entities.prepareReload(resourceManager);
		return CompletableFuture.allOf(completableFuture, completableFuture2, completableFuture3, completableFuture4)
			.thenApply(
				void_ -> new TagManager.class_4015(
						(Map<Identifier, Tag.Builder<Block>>)completableFuture.join(),
						(Map<Identifier, Tag.Builder<Item>>)completableFuture2.join(),
						(Map<Identifier, Tag.Builder<Fluid>>)completableFuture3.join(),
						(Map<Identifier, Tag.Builder<EntityType<?>>>)completableFuture4.join()
					)
			);
	}

	public void method_18245(ResourceManager resourceManager, TagManager.class_4015 arg, Profiler profiler) {
		this.clear();
		this.blocks.applyReload(arg.field_17938);
		this.items.applyReload(arg.field_17939);
		this.fluids.applyReload(arg.field_17940);
		this.entities.applyReload(arg.field_17941);
		BlockTags.setContainer(this.blocks);
		ItemTags.setContainer(this.items);
		FluidTags.setContainer(this.fluids);
		EntityTags.setContainer(this.entities);
	}

	public static class class_4015 {
		final Map<Identifier, Tag.Builder<Block>> field_17938;
		final Map<Identifier, Tag.Builder<Item>> field_17939;
		final Map<Identifier, Tag.Builder<Fluid>> field_17940;
		final Map<Identifier, Tag.Builder<EntityType<?>>> field_17941;

		public class_4015(
			Map<Identifier, Tag.Builder<Block>> map,
			Map<Identifier, Tag.Builder<Item>> map2,
			Map<Identifier, Tag.Builder<Fluid>> map3,
			Map<Identifier, Tag.Builder<EntityType<?>>> map4
		) {
			this.field_17938 = map;
			this.field_17939 = map2;
			this.field_17940 = map3;
			this.field_17941 = map4;
		}
	}
}
