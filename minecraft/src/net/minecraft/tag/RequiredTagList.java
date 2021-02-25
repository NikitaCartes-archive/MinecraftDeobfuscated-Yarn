package net.minecraft.tag;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

/**
 * Stores all required tags of a given type, so they can be updated to point to the new tag instances on datapack reload
 */
public class RequiredTagList<T> {
	private final RegistryKey<? extends Registry<T>> registryKey;
	private final String dataType;
	private TagGroup<T> group = TagGroup.createEmpty();
	private final List<RequiredTagList.TagWrapper<T>> tags = Lists.<RequiredTagList.TagWrapper<T>>newArrayList();

	public RequiredTagList(RegistryKey<? extends Registry<T>> registryKey, String dataType) {
		this.registryKey = registryKey;
		this.dataType = dataType;
	}

	public Tag.Identified<T> add(String id) {
		RequiredTagList.TagWrapper<T> tagWrapper = new RequiredTagList.TagWrapper<>(new Identifier(id));
		this.tags.add(tagWrapper);
		return tagWrapper;
	}

	@Environment(EnvType.CLIENT)
	public void clearAllTags() {
		this.group = TagGroup.createEmpty();
		Tag<T> tag = SetTag.empty();
		this.tags.forEach(tagx -> tagx.updateDelegate(id -> tag));
	}

	public void updateTagManager(TagManager tagManager) {
		TagGroup<T> tagGroup = tagManager.getOrCreateTagGroup(this.registryKey);
		this.group = tagGroup;
		this.tags.forEach(tag -> tag.updateDelegate(tagGroup::getTag));
	}

	public TagGroup<T> getGroup() {
		return this.group;
	}

	/**
	 * Gets the required tags which are not supplied by the current datapacks.
	 */
	public Set<Identifier> getMissingTags(TagManager tagManager) {
		TagGroup<T> tagGroup = tagManager.getOrCreateTagGroup(this.registryKey);
		Set<Identifier> set = (Set<Identifier>)this.tags.stream().map(RequiredTagList.TagWrapper::getId).collect(Collectors.toSet());
		ImmutableSet<Identifier> immutableSet = ImmutableSet.copyOf(tagGroup.getTagIds());
		return Sets.<Identifier>difference(set, immutableSet);
	}

	public RegistryKey<? extends Registry<T>> getRegistryKey() {
		return this.registryKey;
	}

	public String getDataType() {
		return this.dataType;
	}

	protected void method_33147(TagManager.Builder builder) {
		builder.add(
			this.registryKey, TagGroup.create((Map<Identifier, Tag<T>>)this.tags.stream().collect(Collectors.toMap(Tag.Identified::getId, tagWrapper -> tagWrapper)))
		);
	}

	static class TagWrapper<T> implements Tag.Identified<T> {
		@Nullable
		private Tag<T> delegate;
		protected final Identifier id;

		private TagWrapper(Identifier id) {
			this.id = id;
		}

		@Override
		public Identifier getId() {
			return this.id;
		}

		private Tag<T> get() {
			if (this.delegate == null) {
				throw new IllegalStateException("Tag " + this.id + " used before it was bound");
			} else {
				return this.delegate;
			}
		}

		void updateDelegate(Function<Identifier, Tag<T>> tagFactory) {
			this.delegate = (Tag<T>)tagFactory.apply(this.id);
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
