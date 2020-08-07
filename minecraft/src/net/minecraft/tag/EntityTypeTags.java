package net.minecraft.tag;

import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

public final class EntityTypeTags {
	protected static final RequiredTagList<EntityType<?>> REQUIRED_TAGS = RequiredTagListRegistry.register(
		new Identifier("entity_type"), TagManager::getEntityTypes
	);
	public static final Tag.Identified<EntityType<?>> field_15507 = register("skeletons");
	public static final Tag.Identified<EntityType<?>> field_19168 = register("raiders");
	public static final Tag.Identified<EntityType<?>> field_20631 = register("beehive_inhabitors");
	public static final Tag.Identified<EntityType<?>> field_21508 = register("arrows");
	public static final Tag.Identified<EntityType<?>> field_22415 = register("impact_projectiles");

	private static Tag.Identified<EntityType<?>> register(String id) {
		return REQUIRED_TAGS.add(id);
	}

	public static TagGroup<EntityType<?>> getTagGroup() {
		return REQUIRED_TAGS.getGroup();
	}

	public static List<? extends Tag.Identified<EntityType<?>>> method_31073() {
		return REQUIRED_TAGS.getTags();
	}
}
