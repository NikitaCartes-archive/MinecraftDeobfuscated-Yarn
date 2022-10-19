package net.minecraft.tag;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class TagGroupLoader<T> {
	private static final Logger LOGGER = LogUtils.getLogger();
	final Function<Identifier, Optional<? extends T>> registryGetter;
	private final String dataType;

	public TagGroupLoader(Function<Identifier, Optional<? extends T>> registryGetter, String dataType) {
		this.registryGetter = registryGetter;
		this.dataType = dataType;
	}

	public Map<Identifier, List<TagGroupLoader.TrackedEntry>> loadTags(ResourceManager resourceManager) {
		Map<Identifier, List<TagGroupLoader.TrackedEntry>> map = Maps.<Identifier, List<TagGroupLoader.TrackedEntry>>newHashMap();
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
						TagFile tagFile = TagFile.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, jsonElement)).getOrThrow(false, LOGGER::error);
						if (tagFile.replace()) {
							list.clear();
						}

						String string = resource.getResourcePackName();
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
					LOGGER.error("Couldn't read tag list {} from {} in data pack {}", identifier2, identifier, resource.getResourcePackName(), var17);
				}
			}
		}

		return map;
	}

	private static void resolveAll(
		Map<Identifier, List<TagGroupLoader.TrackedEntry>> tags,
		Multimap<Identifier, Identifier> referencedTagIdsByTagId,
		Set<Identifier> alreadyResolved,
		Identifier tagId,
		BiConsumer<Identifier, List<TagGroupLoader.TrackedEntry>> resolver
	) {
		if (alreadyResolved.add(tagId)) {
			referencedTagIdsByTagId.get(tagId).forEach(resolvedTagId -> resolveAll(tags, referencedTagIdsByTagId, alreadyResolved, resolvedTagId, resolver));
			List<TagGroupLoader.TrackedEntry> list = (List<TagGroupLoader.TrackedEntry>)tags.get(tagId);
			if (list != null) {
				resolver.accept(tagId, list);
			}
		}
	}

	private static boolean hasCircularDependency(Multimap<Identifier, Identifier> referencedTagIdsByTagId, Identifier tagId, Identifier referencedTagId) {
		Collection<Identifier> collection = referencedTagIdsByTagId.get(referencedTagId);
		return collection.contains(tagId) ? true : collection.stream().anyMatch(id -> hasCircularDependency(referencedTagIdsByTagId, tagId, id));
	}

	private static void addReference(Multimap<Identifier, Identifier> referencedTagIdsByTagId, Identifier tagId, Identifier referencedTagId) {
		if (!hasCircularDependency(referencedTagIdsByTagId, tagId, referencedTagId)) {
			referencedTagIdsByTagId.put(tagId, referencedTagId);
		}
	}

	private Either<Collection<TagGroupLoader.TrackedEntry>, Collection<T>> resolveAll(
		TagEntry.ValueGetter<T> valueGetter, List<TagGroupLoader.TrackedEntry> entries
	) {
		Builder<T> builder = ImmutableSet.builder();
		List<TagGroupLoader.TrackedEntry> list = new ArrayList();

		for (TagGroupLoader.TrackedEntry trackedEntry : entries) {
			if (!trackedEntry.entry().resolve(valueGetter, builder::add)) {
				list.add(trackedEntry);
			}
		}

		return list.isEmpty() ? Either.right(builder.build()) : Either.left(list);
	}

	public Map<Identifier, Collection<T>> buildGroup(Map<Identifier, List<TagGroupLoader.TrackedEntry>> tags) {
		final Map<Identifier, Collection<T>> map = Maps.<Identifier, Collection<T>>newHashMap();
		TagEntry.ValueGetter<T> valueGetter = new TagEntry.ValueGetter<T>() {
			@Nullable
			@Override
			public T direct(Identifier id) {
				return (T)((Optional)TagGroupLoader.this.registryGetter.apply(id)).orElse(null);
			}

			@Nullable
			@Override
			public Collection<T> tag(Identifier id) {
				return (Collection<T>)map.get(id);
			}
		};
		Multimap<Identifier, Identifier> multimap = HashMultimap.create();
		tags.forEach(
			(tagId, entries) -> entries.forEach(entry -> entry.entry.forEachRequiredTagId(referencedTagId -> addReference(multimap, tagId, referencedTagId)))
		);
		tags.forEach(
			(tagId, entries) -> entries.forEach(entry -> entry.entry.forEachOptionalTagId(referencedTagId -> addReference(multimap, tagId, referencedTagId)))
		);
		Set<Identifier> set = Sets.<Identifier>newHashSet();
		tags.keySet()
			.forEach(
				tagId -> resolveAll(
						tags,
						multimap,
						set,
						tagId,
						(tagId2, entries) -> this.resolveAll(valueGetter, entries)
								.ifLeft(
									missingReferences -> LOGGER.error(
											"Couldn't load tag {} as it is missing following references: {}",
											tagId2,
											missingReferences.stream().map(Objects::toString).collect(Collectors.joining(", "))
										)
								)
								.ifRight(resolvedEntries -> map.put(tagId2, resolvedEntries))
					)
			);
		return map;
	}

	public Map<Identifier, Collection<T>> load(ResourceManager manager) {
		return this.buildGroup(this.loadTags(manager));
	}

	public static record TrackedEntry(TagEntry entry, String source) {

		public String toString() {
			return this.entry + " (from " + this.source + ")";
		}
	}
}
