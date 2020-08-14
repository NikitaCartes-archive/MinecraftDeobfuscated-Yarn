package net.minecraft.tag;

import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

/**
 * A class containing the single static instance of {@link TagManager} on the server.
 */
public class ServerTagManagerHolder {
	private static volatile TagManager tagManager = TagManager.create(
		TagGroup.create((Map<Identifier, Tag<Block>>)BlockTags.getRequiredTags().stream().collect(Collectors.toMap(Tag.Identified::getId, identified -> identified))),
		TagGroup.create((Map<Identifier, Tag<Item>>)ItemTags.getRequiredTags().stream().collect(Collectors.toMap(Tag.Identified::getId, identified -> identified))),
		TagGroup.create((Map<Identifier, Tag<Fluid>>)FluidTags.getRequiredTags().stream().collect(Collectors.toMap(Tag.Identified::getId, identified -> identified))),
		TagGroup.create(
			(Map<Identifier, Tag<EntityType<?>>>)EntityTypeTags.getRequiredTags().stream().collect(Collectors.toMap(Tag.Identified::getId, identified -> identified))
		)
	);

	public static TagManager getTagManager() {
		return tagManager;
	}

	public static void setTagManager(TagManager tagManager) {
		ServerTagManagerHolder.tagManager = tagManager;
	}
}
