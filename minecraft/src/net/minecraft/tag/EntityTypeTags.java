package net.minecraft.tag;

import net.minecraft.entity.EntityType;

public class EntityTypeTags {
	private static final GlobalTagAccessor<EntityType<?>> ACCESSOR = new GlobalTagAccessor<>();
	public static final Tag.Identified<EntityType<?>> SKELETONS = register("skeletons");
	public static final Tag.Identified<EntityType<?>> RAIDERS = register("raiders");
	public static final Tag.Identified<EntityType<?>> BEEHIVE_INHABITORS = register("beehive_inhabitors");
	public static final Tag.Identified<EntityType<?>> ARROWS = register("arrows");
	public static final Tag.Identified<EntityType<?>> IMPACT_PROJECTILES = register("impact_projectiles");

	private static Tag.Identified<EntityType<?>> register(String id) {
		return ACCESSOR.get(id);
	}

	public static void setContainer(TagContainer<EntityType<?>> container) {
		ACCESSOR.setContainer(container);
	}

	public static TagContainer<EntityType<?>> getContainer() {
		return ACCESSOR.getContainer();
	}
}
