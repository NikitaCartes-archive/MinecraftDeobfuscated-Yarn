package net.minecraft.tag;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagManager {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final TagManager EMPTY = new TagManager(ImmutableMap.of());
	private final Map<RegistryKey<? extends Registry<?>>, TagGroup<?>> tagGroups;

	private TagManager(Map<RegistryKey<? extends Registry<?>>, TagGroup<?>> tagGroups) {
		this.tagGroups = tagGroups;
	}

	@Nullable
	private <T> TagGroup<T> getTagGroup(RegistryKey<? extends Registry<T>> registryKey) {
		return (TagGroup<T>)this.tagGroups.get(registryKey);
	}

	public <T> TagGroup<T> getOrCreateTagGroup(RegistryKey<? extends Registry<T>> registryKey) {
		return (TagGroup<T>)this.tagGroups.getOrDefault(registryKey, TagGroup.createEmpty());
	}

	public <T, E extends Exception> Tag<T> getTag(RegistryKey<? extends Registry<T>> registryKey, Identifier id, Function<Identifier, E> exceptionFactory) throws E {
		TagGroup<T> tagGroup = this.getTagGroup(registryKey);
		if (tagGroup == null) {
			throw (Exception)exceptionFactory.apply(id);
		} else {
			Tag<T> tag = tagGroup.getTag(id);
			if (tag == null) {
				throw (Exception)exceptionFactory.apply(id);
			} else {
				return tag;
			}
		}
	}

	public <T, E extends Exception> Identifier getTagId(RegistryKey<? extends Registry<T>> registryKey, Tag<T> tag, Supplier<E> exceptionSupplier) throws E {
		TagGroup<T> tagGroup = this.getTagGroup(registryKey);
		if (tagGroup == null) {
			throw (Exception)exceptionSupplier.get();
		} else {
			Identifier identifier = tagGroup.getUncheckedTagId(tag);
			if (identifier == null) {
				throw (Exception)exceptionSupplier.get();
			} else {
				return identifier;
			}
		}
	}

	public void accept(TagManager.Visitor visitor) {
		this.tagGroups.forEach((type, group) -> offerTo(visitor, type, group));
	}

	private static <T> void offerTo(TagManager.Visitor visitor, RegistryKey<? extends Registry<?>> type, TagGroup<?> group) {
		visitor.visit(type, group);
	}

	public void apply() {
		RequiredTagListRegistry.updateTagManager(this);
		Blocks.refreshShapeCache();
	}

	public Map<RegistryKey<? extends Registry<?>>, TagGroup.Serialized> toPacket(DynamicRegistryManager registryManager) {
		final Map<RegistryKey<? extends Registry<?>>, TagGroup.Serialized> map = Maps.<RegistryKey<? extends Registry<?>>, TagGroup.Serialized>newHashMap();
		this.accept(new TagManager.Visitor() {
			@Override
			public <T> void visit(RegistryKey<? extends Registry<T>> type, TagGroup<T> group) {
				Optional<? extends Registry<T>> optional = registryManager.getOptional(type);
				if (optional.isPresent()) {
					map.put(type, group.serialize((Registry<T>)optional.get()));
				} else {
					TagManager.LOGGER.error("Unknown registry {}", type);
				}
			}
		});
		return map;
	}

	public static TagManager fromPacket(DynamicRegistryManager registryManager, Map<RegistryKey<? extends Registry<?>>, TagGroup.Serialized> groups) {
		TagManager.Builder builder = new TagManager.Builder();
		groups.forEach((type, group) -> tryAdd(registryManager, builder, type, group));
		return builder.build();
	}

	private static <T> void tryAdd(
		DynamicRegistryManager registryManager, TagManager.Builder builder, RegistryKey<? extends Registry<? extends T>> type, TagGroup.Serialized group
	) {
		Optional<? extends Registry<? extends T>> optional = registryManager.getOptional(type);
		if (optional.isPresent()) {
			builder.add(type, TagGroup.deserialize(group, (Registry<? extends T>)optional.get()));
		} else {
			LOGGER.error("Unknown registry {}", type);
		}
	}

	public static class Builder {
		private final ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, TagGroup<?>> groups = ImmutableMap.builder();

		public <T> TagManager.Builder add(RegistryKey<? extends Registry<? extends T>> type, TagGroup<T> tagGroup) {
			this.groups.put(type, tagGroup);
			return this;
		}

		public TagManager build() {
			return new TagManager(this.groups.build());
		}
	}

	@FunctionalInterface
	interface Visitor {
		<T> void visit(RegistryKey<? extends Registry<T>> type, TagGroup<T> group);
	}
}
