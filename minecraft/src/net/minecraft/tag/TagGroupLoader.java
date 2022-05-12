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
import net.minecraft.class_7475;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class TagGroupLoader<T> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String JSON_EXTENSION = ".json";
	private static final int JSON_EXTENSION_LENGTH = ".json".length();
	final Function<Identifier, Optional<T>> registryGetter;
	private final String dataType;

	public TagGroupLoader(Function<Identifier, Optional<T>> registryGetter, String dataType) {
		this.registryGetter = registryGetter;
		this.dataType = dataType;
	}

	public Map<Identifier, List<TagGroupLoader.TrackedEntry>> loadTags(ResourceManager manager) {
		Map<Identifier, List<TagGroupLoader.TrackedEntry>> map = Maps.<Identifier, List<TagGroupLoader.TrackedEntry>>newHashMap();

		for (Entry<Identifier, List<Resource>> entry : manager.findAllResources(this.dataType, id -> id.getPath().endsWith(".json")).entrySet()) {
			Identifier identifier = (Identifier)entry.getKey();
			String string = identifier.getPath();
			Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(this.dataType.length() + 1, string.length() - JSON_EXTENSION_LENGTH));

			for (Resource resource : (List)entry.getValue()) {
				try {
					Reader reader = resource.getReader();

					try {
						JsonElement jsonElement = JsonParser.parseReader(reader);
						List<TagGroupLoader.TrackedEntry> list = (List<TagGroupLoader.TrackedEntry>)map.computeIfAbsent(identifier2, identifierx -> new ArrayList());
						class_7475 lv = class_7475.field_39269.parse(new Dynamic<>(JsonOps.INSTANCE, jsonElement)).getOrThrow(false, LOGGER::error);
						if (lv.replace()) {
							list.clear();
						}

						String string2 = resource.getResourcePackName();
						lv.entries().forEach(tagEntry -> list.add(new TagGroupLoader.TrackedEntry(tagEntry, string2)));
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

	private static void method_32839(
		Map<Identifier, List<TagGroupLoader.TrackedEntry>> map,
		Multimap<Identifier, Identifier> multimap,
		Set<Identifier> set,
		Identifier identifier,
		BiConsumer<Identifier, List<TagGroupLoader.TrackedEntry>> biConsumer
	) {
		if (set.add(identifier)) {
			multimap.get(identifier).forEach(identifierx -> method_32839(map, multimap, set, identifierx, biConsumer));
			List<TagGroupLoader.TrackedEntry> list = (List<TagGroupLoader.TrackedEntry>)map.get(identifier);
			if (list != null) {
				biConsumer.accept(identifier, list);
			}
		}
	}

	private static boolean method_32836(Multimap<Identifier, Identifier> multimap, Identifier identifier, Identifier identifier2) {
		Collection<Identifier> collection = multimap.get(identifier2);
		return collection.contains(identifier) ? true : collection.stream().anyMatch(identifier2x -> method_32836(multimap, identifier, identifier2x));
	}

	private static void method_32844(Multimap<Identifier, Identifier> multimap, Identifier identifier, Identifier identifier2) {
		if (!method_32836(multimap, identifier, identifier2)) {
			multimap.put(identifier, identifier2);
		}
	}

	private Either<Collection<TagGroupLoader.TrackedEntry>, Collection<T>> method_43952(
		TagEntry.ValueGetter<T> valueGetter, List<TagGroupLoader.TrackedEntry> list
	) {
		Builder<T> builder = ImmutableSet.builder();
		List<TagGroupLoader.TrackedEntry> list2 = new ArrayList();

		for (TagGroupLoader.TrackedEntry trackedEntry : list) {
			if (!trackedEntry.entry().resolve(valueGetter, builder::add)) {
				list2.add(trackedEntry);
			}
		}

		return list2.isEmpty() ? Either.right(builder.build()) : Either.left(list2);
	}

	public Map<Identifier, Collection<T>> buildGroup(Map<Identifier, List<TagGroupLoader.TrackedEntry>> map) {
		final Map<Identifier, Collection<T>> map2 = Maps.<Identifier, Collection<T>>newHashMap();
		TagEntry.ValueGetter<T> valueGetter = new TagEntry.ValueGetter<T>() {
			@Nullable
			@Override
			public T direct(Identifier id) {
				return (T)((Optional)TagGroupLoader.this.registryGetter.apply(id)).orElse(null);
			}

			@Nullable
			@Override
			public Collection<T> tag(Identifier id) {
				return (Collection<T>)map2.get(id);
			}
		};
		Multimap<Identifier, Identifier> multimap = HashMultimap.create();
		map.forEach(
			(identifier, list) -> list.forEach(trackedEntry -> trackedEntry.entry.forEachRequiredTagId(identifier2 -> method_32844(multimap, identifier, identifier2)))
		);
		map.forEach(
			(identifier, list) -> list.forEach(trackedEntry -> trackedEntry.entry.forEachOptionalTagId(identifier2 -> method_32844(multimap, identifier, identifier2)))
		);
		Set<Identifier> set = Sets.<Identifier>newHashSet();
		map.keySet()
			.forEach(
				identifier -> method_32839(
						map,
						multimap,
						set,
						identifier,
						(identifierx, list) -> this.method_43952(valueGetter, list)
								.ifLeft(
									collection -> LOGGER.error(
											"Couldn't load tag {} as it is missing following references: {}",
											identifierx,
											collection.stream().map(Objects::toString).collect(Collectors.joining(", "))
										)
								)
								.ifRight(collection -> map2.put(identifierx, collection))
					)
			);
		return map2;
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
