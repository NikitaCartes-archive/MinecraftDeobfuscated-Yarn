package net.minecraft.registry.tag;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedSet;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.DependencyTracker;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class TagGroupLoader<T> {
	private static final Logger LOGGER = LogUtils.getLogger();
	final TagGroupLoader.EntrySupplier<T> entrySupplier;
	private final String dataType;

	public TagGroupLoader(TagGroupLoader.EntrySupplier<T> entrySupplier, String dataType) {
		this.entrySupplier = entrySupplier;
		this.dataType = dataType;
	}

	public Map<Identifier, List<TagGroupLoader.TrackedEntry>> loadTags(ResourceManager resourceManager) {
		Map<Identifier, List<TagGroupLoader.TrackedEntry>> map = new HashMap();
		ResourceFinder resourceFinder = ResourceFinder.json(this.dataType);

		for (Entry<Identifier, List<Resource>> entry : resourceFinder.findAllResources(resourceManager).entrySet()) {
			Identifier identifier = (Identifier)entry.getKey();
			Identifier identifier2 = resourceFinder.toResourceId(identifier);

			for (Resource resource : (List)entry.getValue()) {
				try {
					Reader reader = resource.getReader();

					try {
						JsonElement jsonElement = JsonParser.parseReader(reader);
						List<TagGroupLoader.TrackedEntry> list = (List<TagGroupLoader.TrackedEntry>)map.computeIfAbsent(identifier2, id -> new ArrayList());
						TagFile tagFile = TagFile.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, jsonElement)).getOrThrow();
						if (tagFile.replace()) {
							list.clear();
						}

						String string = resource.getPackId();
						tagFile.entries().forEach(entryx -> list.add(new TagGroupLoader.TrackedEntry(entryx, string)));
					} catch (Throwable var16) {
						if (reader != null) {
							try {
								reader.close();
							} catch (Throwable var15) {
								var16.addSuppressed(var15);
							}
						}

						throw var16;
					}

					if (reader != null) {
						reader.close();
					}
				} catch (Exception var17) {
					LOGGER.error("Couldn't read tag list {} from {} in data pack {}", identifier2, identifier, resource.getPackId(), var17);
				}
			}
		}

		return map;
	}

	private Either<List<TagGroupLoader.TrackedEntry>, List<T>> resolveAll(TagEntry.ValueGetter<T> valueGetter, List<TagGroupLoader.TrackedEntry> entries) {
		SequencedSet<T> sequencedSet = new LinkedHashSet();
		List<TagGroupLoader.TrackedEntry> list = new ArrayList();

		for (TagGroupLoader.TrackedEntry trackedEntry : entries) {
			if (!trackedEntry.entry().resolve(valueGetter, sequencedSet::add)) {
				list.add(trackedEntry);
			}
		}

		return list.isEmpty() ? Either.right(List.copyOf(sequencedSet)) : Either.left(list);
	}

	public Map<Identifier, List<T>> buildGroup(Map<Identifier, List<TagGroupLoader.TrackedEntry>> tags) {
		final Map<Identifier, List<T>> map = new HashMap();
		TagEntry.ValueGetter<T> valueGetter = new TagEntry.ValueGetter<T>() {
			@Nullable
			@Override
			public T direct(Identifier id, boolean required) {
				return (T)TagGroupLoader.this.entrySupplier.get(id, required).orElse(null);
			}

			@Nullable
			@Override
			public Collection<T> tag(Identifier id) {
				return (Collection<T>)map.get(id);
			}
		};
		DependencyTracker<Identifier, TagGroupLoader.TagDependencies> dependencyTracker = new DependencyTracker<>();
		tags.forEach((id, entries) -> dependencyTracker.add(id, new TagGroupLoader.TagDependencies(entries)));
		dependencyTracker.traverse(
			(id, dependencies) -> this.resolveAll(valueGetter, dependencies.entries)
					.ifLeft(
						missingReferences -> LOGGER.error(
								"Couldn't load tag {} as it is missing following references: {}",
								id,
								missingReferences.stream().map(Objects::toString).collect(Collectors.joining(", "))
							)
					)
					.ifRight(values -> map.put(id, values))
		);
		return map;
	}

	public static <T> void loadFromNetwork(TagPacketSerializer.Serialized tags, MutableRegistry<T> registry) {
		tags.toRegistryTags(registry).tags.forEach(registry::setEntries);
	}

	public static List<Registry.PendingTagLoad<?>> startReload(ResourceManager resourceManager, DynamicRegistryManager registryManager) {
		return (List<Registry.PendingTagLoad<?>>)registryManager.streamAllRegistries()
			.map(registry -> startReload(resourceManager, registry.value()))
			.flatMap(Optional::stream)
			.collect(Collectors.toUnmodifiableList());
	}

	public static <T> void loadInitial(ResourceManager resourceManager, MutableRegistry<T> registry) {
		RegistryKey<? extends Registry<T>> registryKey = registry.getKey();
		TagGroupLoader<RegistryEntry<T>> tagGroupLoader = new TagGroupLoader<>(
			TagGroupLoader.EntrySupplier.forInitial(registry), RegistryKeys.getTagPath(registryKey)
		);
		tagGroupLoader.buildGroup(tagGroupLoader.loadTags(resourceManager)).forEach((id, entries) -> registry.setEntries(TagKey.of(registryKey, id), entries));
	}

	private static <T> Map<TagKey<T>, List<RegistryEntry<T>>> toTagKeyedMap(
		RegistryKey<? extends Registry<T>> registryRef, Map<Identifier, List<RegistryEntry<T>>> tags
	) {
		return (Map<TagKey<T>, List<RegistryEntry<T>>>)tags.entrySet()
			.stream()
			.collect(Collectors.toUnmodifiableMap(entry -> TagKey.of(registryRef, (Identifier)entry.getKey()), Entry::getValue));
	}

	private static <T> Optional<Registry.PendingTagLoad<T>> startReload(ResourceManager resourceManager, Registry<T> registry) {
		RegistryKey<? extends Registry<T>> registryKey = registry.getKey();
		TagGroupLoader<RegistryEntry<T>> tagGroupLoader = new TagGroupLoader<>(
			(TagGroupLoader.EntrySupplier<RegistryEntry<T>>)TagGroupLoader.EntrySupplier.forReload(registry), RegistryKeys.getTagPath(registryKey)
		);
		TagGroupLoader.RegistryTags<T> registryTags = new TagGroupLoader.RegistryTags<>(
			registryKey, toTagKeyedMap(registry.getKey(), tagGroupLoader.buildGroup(tagGroupLoader.loadTags(resourceManager)))
		);
		return registryTags.tags().isEmpty() ? Optional.empty() : Optional.of(registry.startTagReload(registryTags));
	}

	public static List<RegistryWrapper.Impl<?>> collectRegistries(DynamicRegistryManager.Immutable registryManager, List<Registry.PendingTagLoad<?>> tagLoads) {
		List<RegistryWrapper.Impl<?>> list = new ArrayList();
		registryManager.streamAllRegistries().forEach(registry -> {
			Registry.PendingTagLoad<?> pendingTagLoad = find(tagLoads, registry.key());
			list.add(pendingTagLoad != null ? pendingTagLoad.getLookup() : registry.value());
		});
		return list;
	}

	@Nullable
	private static Registry.PendingTagLoad<?> find(List<Registry.PendingTagLoad<?>> pendingTags, RegistryKey<? extends Registry<?>> registryRef) {
		for (Registry.PendingTagLoad<?> pendingTagLoad : pendingTags) {
			if (pendingTagLoad.getKey() == registryRef) {
				return pendingTagLoad;
			}
		}

		return null;
	}

	public interface EntrySupplier<T> {
		Optional<? extends T> get(Identifier id, boolean required);

		static <T> TagGroupLoader.EntrySupplier<? extends RegistryEntry<T>> forReload(Registry<T> registry) {
			return (id, required) -> registry.getEntry(id);
		}

		static <T> TagGroupLoader.EntrySupplier<RegistryEntry<T>> forInitial(MutableRegistry<T> registry) {
			RegistryEntryLookup<T> registryEntryLookup = registry.createMutableRegistryLookup();
			return (id, required) -> ((RegistryEntryLookup<T>)(required ? registryEntryLookup : registry)).getOptional(RegistryKey.of(registry.getKey(), id));
		}
	}

	public static record RegistryTags<T>(RegistryKey<? extends Registry<T>> key, Map<TagKey<T>, List<RegistryEntry<T>>> tags) {
	}

	static record TagDependencies(List<TagGroupLoader.TrackedEntry> entries) implements DependencyTracker.Dependencies<Identifier> {

		@Override
		public void forDependencies(Consumer<Identifier> callback) {
			this.entries.forEach(entry -> entry.entry.forEachRequiredTagId(callback));
		}

		@Override
		public void forOptionalDependencies(Consumer<Identifier> callback) {
			this.entries.forEach(entry -> entry.entry.forEachOptionalTagId(callback));
		}
	}

	public static record TrackedEntry(TagEntry entry, String source) {

		public String toString() {
			return this.entry + " (from " + this.source + ")";
		}
	}
}
