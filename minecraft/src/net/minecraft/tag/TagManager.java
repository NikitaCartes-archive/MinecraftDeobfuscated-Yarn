package net.minecraft.tag;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class TagManager implements ResourceReloadListener {
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

	@Override
	public void onResourceReload(ResourceManager resourceManager) {
		this.clear();
		this.blocks.load(resourceManager);
		this.items.load(resourceManager);
		this.fluids.load(resourceManager);
		this.entities.load(resourceManager);
		BlockTags.setContainer(this.blocks);
		ItemTags.setContainer(this.items);
		FluidTags.setContainer(this.fluids);
		EntityTags.setContainer(this.entities);
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
}
