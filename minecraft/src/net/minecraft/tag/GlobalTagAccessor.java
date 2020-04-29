package net.minecraft.tag;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

/**
 * Allows access to tags from either client or server threads and propagates
 * tag changes through reloads/server tag sends.
 */
public class GlobalTagAccessor<T> {
	private final TagContainer<T> emptyContainer = new TagContainer<>(identifier -> Optional.empty(), "", "");
	private TagContainer<T> currentContainer = this.emptyContainer;
	private final List<GlobalTagAccessor.CachedTag<T>> tags = Lists.<GlobalTagAccessor.CachedTag<T>>newArrayList();

	public Tag.Identified<T> get(String id) {
		GlobalTagAccessor.CachedTag<T> cachedTag = new GlobalTagAccessor.CachedTag<>(new Identifier(id));
		this.tags.add(cachedTag);
		return cachedTag;
	}

	@Environment(EnvType.CLIENT)
	public void markReady() {
		this.currentContainer = this.emptyContainer;
		Tag<T> tag = this.emptyContainer.getEmpty();
		this.tags.forEach(cachedTag -> cachedTag.updateContainer(identifier -> tag));
	}

	public void setContainer(TagContainer<T> container) {
		this.currentContainer = container;
		this.tags.forEach(cachedTag -> cachedTag.updateContainer(container::get));
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

		void updateContainer(Function<Identifier, Tag<T>> tagFactory) {
			this.currentTag = (Tag<T>)tagFactory.apply(this.id);
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
