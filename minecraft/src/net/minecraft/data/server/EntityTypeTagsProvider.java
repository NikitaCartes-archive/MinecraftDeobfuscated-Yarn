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
		this.method_10512(EntityTypeTags.field_15507).add(EntityType.field_6137, EntityType.field_6098, EntityType.field_6076);
		this.method_10512(EntityTypeTags.field_19168)
			.add(EntityType.field_6090, EntityType.field_6105, EntityType.field_6134, EntityType.field_6117, EntityType.field_6065, EntityType.field_6145);
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
	protected void method_10511(TagContainer<EntityType<?>> tagContainer) {
		EntityTypeTags.setContainer(tagContainer);
	}
}
