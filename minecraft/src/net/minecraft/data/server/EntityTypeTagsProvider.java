package net.minecraft.data.server;

import java.nio.file.Path;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityTypeTagsProvider extends AbstractTagProvider<EntityType<?>> {
	public EntityTypeTagsProvider(DataGenerator dataGenerator) {
		super(dataGenerator, Registry.ENTITY_TYPE);
	}

	@Override
	protected void configure() {
		this.getOrCreateTagBuilder(EntityTypeTags.field_15507).add(EntityType.field_6137, EntityType.field_6098, EntityType.field_6076);
		this.getOrCreateTagBuilder(EntityTypeTags.field_19168)
			.add(EntityType.field_6090, EntityType.field_6105, EntityType.field_6134, EntityType.field_6117, EntityType.field_6065, EntityType.field_6145);
		this.getOrCreateTagBuilder(EntityTypeTags.field_20631).add(EntityType.field_20346);
		this.getOrCreateTagBuilder(EntityTypeTags.field_21508).add(EntityType.field_6122, EntityType.field_6135);
		this.getOrCreateTagBuilder(EntityTypeTags.field_22415)
			.addTag(EntityTypeTags.field_21508)
			.add(
				EntityType.field_6068,
				EntityType.field_6066,
				EntityType.field_6049,
				EntityType.field_6144,
				EntityType.field_6127,
				EntityType.field_6129,
				EntityType.field_6130
			);
	}

	@Override
	protected Path getOutput(Identifier identifier) {
		return this.root.getOutput().resolve("data/" + identifier.getNamespace() + "/tags/entity_types/" + identifier.getPath() + ".json");
	}

	@Override
	public String getName() {
		return "Entity Type Tags";
	}
}
