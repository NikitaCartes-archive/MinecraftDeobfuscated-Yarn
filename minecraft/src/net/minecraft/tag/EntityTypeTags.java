package net.minecraft.tag;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

public class EntityTypeTags {
	private static TagContainer<EntityType<?>> container = new TagContainer<>(identifier -> Optional.empty(), "", false, "");
	private static int latestVersion;
	public static final Tag<EntityType<?>> field_15507 = register("skeletons");
	public static final Tag<EntityType<?>> field_19168 = register("raiders");

	public static void setContainer(TagContainer<EntityType<?>> tagContainer) {
		container = tagContainer;
		latestVersion++;
	}

	public static TagContainer<EntityType<?>> getContainer() {
		return container;
	}

	private static Tag<EntityType<?>> register(String string) {
		return new EntityTypeTags.CachingTag(new Identifier(string));
	}

	public static class CachingTag extends Tag<EntityType<?>> {
		private int version = -1;
		private Tag<EntityType<?>> delegate;

		public CachingTag(Identifier identifier) {
			super(identifier);
		}

		public boolean method_15084(EntityType<?> entityType) {
			if (this.version != EntityTypeTags.latestVersion) {
				this.delegate = EntityTypeTags.container.getOrCreate(this.getId());
				this.version = EntityTypeTags.latestVersion;
			}

			return this.delegate.contains(entityType);
		}

		@Override
		public Collection<EntityType<?>> values() {
			if (this.version != EntityTypeTags.latestVersion) {
				this.delegate = EntityTypeTags.container.getOrCreate(this.getId());
				this.version = EntityTypeTags.latestVersion;
			}

			return this.delegate.values();
		}

		@Override
		public Collection<Tag.Entry<EntityType<?>>> entries() {
			if (this.version != EntityTypeTags.latestVersion) {
				this.delegate = EntityTypeTags.container.getOrCreate(this.getId());
				this.version = EntityTypeTags.latestVersion;
			}

			return this.delegate.entries();
		}
	}
}
