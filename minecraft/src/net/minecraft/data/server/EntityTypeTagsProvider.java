package net.minecraft.data.server;

import java.nio.file.Path;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityTypeTagsProvider extends AbstractTagProvider<EntityType<?>> {
	public EntityTypeTagsProvider(DataGenerator dataGenerator) {
		super(dataGenerator, Registry.ENTITY_TYPE);
	}

	@Override
	protected void configure() {
		this.getOrCreateTagBuilder(EntityTypeTags.SKELETONS).add(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON);
		this.getOrCreateTagBuilder(EntityTypeTags.RAIDERS)
			.add(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH);
		this.getOrCreateTagBuilder(EntityTypeTags.BEEHIVE_INHABITORS).add(EntityType.BEE);
		this.getOrCreateTagBuilder(EntityTypeTags.ARROWS).add(EntityType.ARROW, EntityType.SPECTRAL_ARROW);
	}

	@Override
	protected Path getOutput(Identifier identifier) {
		return this.root.getOutput().resolve("data/" + identifier.getNamespace() + "/tags/entity_types/" + identifier.getPath() + ".json");
	}

	@Override
	public String getName() {
		return "Entity Type Tags";
	}

	@Override
	protected void setContainer(TagContainer<EntityType<?>> tagContainer) {
		EntityTypeTags.setContainer(tagContainer);
	}
}
