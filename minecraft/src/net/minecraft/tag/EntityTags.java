package net.minecraft.tag;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

public class EntityTags {
	private static TagContainer<EntityType<?>> field_15508 = new TagContainer<>(identifier -> Optional.empty(), "", false, "");
	private static int field_15509;
	public static final Tag<EntityType<?>> field_15507 = method_15077("skeletons");

	public static void method_15078(TagContainer<EntityType<?>> tagContainer) {
		field_15508 = tagContainer;
		field_15509++;
	}

	public static TagContainer<EntityType<?>> method_15082() {
		return field_15508;
	}

	private static Tag<EntityType<?>> method_15077(String string) {
		return new EntityTags.class_3484(new Identifier(string));
	}

	public static class class_3484 extends Tag<EntityType<?>> {
		private int field_15511 = -1;
		private Tag<EntityType<?>> field_15510;

		public class_3484(Identifier identifier) {
			super(identifier);
		}

		public boolean method_15084(EntityType<?> entityType) {
			if (this.field_15511 != EntityTags.field_15509) {
				this.field_15510 = EntityTags.field_15508.getOrCreate(this.getId());
				this.field_15511 = EntityTags.field_15509;
			}

			return this.field_15510.contains(entityType);
		}

		@Override
		public Collection<EntityType<?>> values() {
			if (this.field_15511 != EntityTags.field_15509) {
				this.field_15510 = EntityTags.field_15508.getOrCreate(this.getId());
				this.field_15511 = EntityTags.field_15509;
			}

			return this.field_15510.values();
		}

		@Override
		public Collection<Tag.Entry<EntityType<?>>> entries() {
			if (this.field_15511 != EntityTags.field_15509) {
				this.field_15510 = EntityTags.field_15508.getOrCreate(this.getId());
				this.field_15511 = EntityTags.field_15509;
			}

			return this.field_15510.entries();
		}
	}
}
