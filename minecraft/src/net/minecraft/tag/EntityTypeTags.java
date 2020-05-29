package net.minecraft.tag;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

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

	@Environment(EnvType.CLIENT)
	public static void markReady() {
		ACCESSOR.markReady();
	}

	public static TagContainer<EntityType<?>> getContainer() {
		return ACCESSOR.getContainer();
	}

	public static Set<Identifier> method_29215(TagContainer<EntityType<?>> tagContainer) {
		return ACCESSOR.method_29224(tagContainer);
	}
}
