package net.minecraft.tag;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;

/**
 * Allows access to tags from either client or server threads and propagates
 * tag changes through reloads/server tag sends.
 */
public class GlobalTagAccessor<T> {
	private TagContainer<T> currentContainer = new TagContainer<>(identifier -> Optional.empty(), "", "");
	private final List<GlobalTagAccessor.CachedTag<T>> tags = Lists.<GlobalTagAccessor.CachedTag<T>>newArrayList();

	public Tag.Identified<T> get(String id) {
		GlobalTagAccessor.CachedTag<T> cachedTag = new GlobalTagAccessor.CachedTag<>(new Identifier(id));
		this.tags.add(cachedTag);
		return cachedTag;
	}

	public void setContainer(TagContainer<T> container) {
		this.currentContainer = container;
		this.tags.forEach(cachedTag -> cachedTag.updateContainer(container));
	}

	public TagContainer<T> getContainer() {
		return this.currentContainer;
	}

	static class CachedTag<T> implements Tag.Identified<T> {
		@Nullable
		private Tag<T> currentTag;
		protected final Identifier id;

		private CachedTag(Identifier id) {
			this.id = id;
		}

		@Override
		public Identifier getId() {
			return this.id;
		}

		private Tag<T> get() {
			if (this.currentTag == null) {
				throw new IllegalStateException("Tag " + this.id + " used before it was bound");
			} else {
				return this.currentTag;
			}
		}

		void updateContainer(TagContainer<T> container) {
			this.currentTag = container.get(this.id);
		}

		@Override
		public boolean contains(T entry) {
			return this.get().contains(entry);
		}

		@Override
		public List<T> values() {
			return this.get().values();
		}
	}
}
