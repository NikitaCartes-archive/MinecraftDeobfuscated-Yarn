package net.minecraft.data.server;

import java.nio.file.Path;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.EntityTags;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityTypeTagsProvider extends AbstractTagProvider<EntityType<?>> {
	public EntityTypeTagsProvider(DataGenerator dataGenerator) {
		super(dataGenerator, Registry.ENTITY_TYPE);
	}

	@Override
	protected void configure() {
		this.method_10512(EntityTags.field_15507).add(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON);
		this.method_10512(EntityTags.field_19168)
			.add(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH);
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
		EntityTags.setContainer(tagContainer);
	}
}
