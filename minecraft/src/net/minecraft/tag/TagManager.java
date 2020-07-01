package net.minecraft.tag;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public interface TagManager {
	TagManager EMPTY = create(TagGroup.createEmpty(), TagGroup.createEmpty(), TagGroup.createEmpty(), TagGroup.createEmpty());

	TagGroup<Block> getBlocks();

	TagGroup<Item> getItems();

	TagGroup<Fluid> getFluids();

	TagGroup<EntityType<?>> getEntityTypes();

	default void apply() {
		RequiredTagListRegistry.updateTagManager(this);
		Blocks.refreshShapeCache();
	}

	default void toPacket(PacketByteBuf buf) {
		this.getBlocks().toPacket(buf, Registry.BLOCK);
		this.getItems().toPacket(buf, Registry.ITEM);
		this.getFluids().toPacket(buf, Registry.FLUID);
		this.getEntityTypes().toPacket(buf, Registry.ENTITY_TYPE);
	}

	static TagManager fromPacket(PacketByteBuf buf) {
		TagGroup<Block> tagGroup = TagGroup.fromPacket(buf, Registry.BLOCK);
		TagGroup<Item> tagGroup2 = TagGroup.fromPacket(buf, Registry.ITEM);
		TagGroup<Fluid> tagGroup3 = TagGroup.fromPacket(buf, Registry.FLUID);
		TagGroup<EntityType<?>> tagGroup4 = TagGroup.fromPacket(buf, Registry.ENTITY_TYPE);
		return create(tagGroup, tagGroup2, tagGroup3, tagGroup4);
	}

	static TagManager create(TagGroup<Block> blocks, TagGroup<Item> items, TagGroup<Fluid> fluids, TagGroup<EntityType<?>> entityTypes) {
		return new TagManager() {
			@Override
			public TagGroup<Block> getBlocks() {
				return blocks;
			}

			@Override
			public TagGroup<Item> getItems() {
				return items;
			}

			@Override
			public TagGroup<Fluid> getFluids() {
				return fluids;
			}

			@Override
			public TagGroup<EntityType<?>> getEntityTypes() {
				return entityTypes;
			}
		};
	}
}
