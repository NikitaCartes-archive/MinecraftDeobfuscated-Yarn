package net.minecraft.tag;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public interface TagManager {
	TagManager EMPTY = create(TagGroup.createEmpty(), TagGroup.createEmpty(), TagGroup.createEmpty(), TagGroup.createEmpty(), TagGroup.createEmpty());

	TagGroup<Block> getBlocks();

	TagGroup<Item> getItems();

	TagGroup<Fluid> getFluids();

	TagGroup<EntityType<?>> getEntityTypes();

	TagGroup<GameEvent> getGameEvents();

	default void apply() {
		RequiredTagListRegistry.updateTagManager(this);
		Blocks.refreshShapeCache();
	}

	default void toPacket(PacketByteBuf buf) {
		this.getBlocks().toPacket(buf, Registry.BLOCK);
		this.getItems().toPacket(buf, Registry.ITEM);
		this.getFluids().toPacket(buf, Registry.FLUID);
		this.getEntityTypes().toPacket(buf, Registry.ENTITY_TYPE);
		this.getGameEvents().toPacket(buf, Registry.GAME_EVENT);
	}

	static TagManager fromPacket(PacketByteBuf buf) {
		TagGroup<Block> tagGroup = TagGroup.fromPacket(buf, Registry.BLOCK);
		TagGroup<Item> tagGroup2 = TagGroup.fromPacket(buf, Registry.ITEM);
		TagGroup<Fluid> tagGroup3 = TagGroup.fromPacket(buf, Registry.FLUID);
		TagGroup<EntityType<?>> tagGroup4 = TagGroup.fromPacket(buf, Registry.ENTITY_TYPE);
		TagGroup<GameEvent> tagGroup5 = TagGroup.fromPacket(buf, Registry.GAME_EVENT);
		return create(tagGroup, tagGroup2, tagGroup3, tagGroup4, tagGroup5);
	}

	static TagManager create(
		TagGroup<Block> blocks, TagGroup<Item> items, TagGroup<Fluid> fluids, TagGroup<EntityType<?>> entityTypes, TagGroup<GameEvent> tagGroup
	) {
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

			@Override
			public TagGroup<GameEvent> getGameEvents() {
				return tagGroup;
			}
		};
	}
}
