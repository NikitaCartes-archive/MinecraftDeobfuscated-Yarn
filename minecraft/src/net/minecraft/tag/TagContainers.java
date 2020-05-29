package net.minecraft.tag;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;

public class TagContainers {
	private static volatile TagContainers instance = new TagContainers(
		BlockTags.getContainer(), ItemTags.getContainer(), FluidTags.getContainer(), EntityTypeTags.getContainer()
	);
	private final TagContainer<Block> blocks;
	private final TagContainer<Item> items;
	private final TagContainer<Fluid> fluids;
	private final TagContainer<EntityType<?>> entityTypes;

	private TagContainers(TagContainer<Block> blocks, TagContainer<Item> items, TagContainer<Fluid> fluids, TagContainer<EntityType<?>> entityTypes) {
		this.blocks = blocks;
		this.items = items;
		this.fluids = fluids;
		this.entityTypes = entityTypes;
	}

	public TagContainer<Block> blocks() {
		return this.blocks;
	}

	public TagContainer<Item> items() {
		return this.items;
	}

	public TagContainer<Fluid> fluids() {
		return this.fluids;
	}

	public TagContainer<EntityType<?>> entityTypes() {
		return this.entityTypes;
	}

	public static TagContainers instance() {
		return instance;
	}

	public static void method_29219(
		TagContainer<Block> tagContainer, TagContainer<Item> tagContainer2, TagContainer<Fluid> tagContainer3, TagContainer<EntityType<?>> tagContainer4
	) {
		instance = new TagContainers(tagContainer, tagContainer2, tagContainer3, tagContainer4);
	}
}
