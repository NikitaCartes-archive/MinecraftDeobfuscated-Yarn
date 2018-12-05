package net.minecraft.tag;

import java.util.Collection;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

public class EntityTags {
	private static TagContainer<EntityType<?>> container = new TagContainer<>(identifier -> false, identifier -> null, "", false, "");
	private static int field_15509;
	public static final Tag<EntityType<?>> field_15507 = register("skeletons");

	public static void setContainer(TagContainer<EntityType<?>> tagContainer) {
		container = tagContainer;
		field_15509++;
	}

	public static TagContainer<EntityType<?>> getContainer() {
		return container;
	}

	private static Tag<EntityType<?>> register(String string) {
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
				this.field_15510 = EntityTags.container.getOrCreate(this.getId());
				this.field_15511 = EntityTags.field_15509;
			}

			return this.field_15510.contains(entityType);
		}

		@Override
		public Collection<EntityType<?>> values() {
			if (this.field_15511 != EntityTags.field_15509) {
				this.field_15510 = EntityTags.container.getOrCreate(this.getId());
				this.field_15511 = EntityTags.field_15509;
			}

			return this.field_15510.values();
		}

		@Override
		public Collection<Tag.Entry<EntityType<?>>> entries() {
			if (this.field_15511 != EntityTags.field_15509) {
				this.field_15510 = EntityTags.container.getOrCreate(this.getId());
				this.field_15511 = EntityTags.field_15509;
			}

			return this.field_15510.entries();
		}
	}
}
