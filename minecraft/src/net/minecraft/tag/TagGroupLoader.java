package net.minecraft.tag;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
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
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceRef;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

public class TagGroupLoader<T> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = new Gson();
	private static final String JSON_EXTENSION = ".json";
	private static final int JSON_EXTENSION_LENGTH = ".json".length();
	private final Function<Identifier, Optional<T>> registryGetter;
	private final String dataType;

	public TagGroupLoader(Function<Identifier, Optional<T>> registryGetter, String dataType) {
		this.registryGetter = registryGetter;
		this.dataType = dataType;
	}

	public Map<Identifier, Tag.Builder> loadTags(ResourceManager manager) {
		Map<Identifier, Tag.Builder> map = Maps.<Identifier, Tag.Builder>newHashMap();

		for (Entry<Identifier, List<ResourceRef>> entry : manager.findAllResources(this.dataType, identifierx -> identifierx.getPath().endsWith(".json")).entrySet()) {
			Identifier identifier = (Identifier)entry.getKey();
			String string = identifier.getPath();
			Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(this.dataType.length() + 1, string.length() - JSON_EXTENSION_LENGTH));

			for (ResourceRef resourceRef : (List)entry.getValue()) {
				try {
					Resource resource = resourceRef.open();

					try {
						InputStream inputStream = resource.getInputStream();

						try {
							Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

							try {
								JsonObject jsonObject = JsonHelper.deserialize(GSON, reader, JsonObject.class);
								if (jsonObject == null) {
									throw new NullPointerException("Invalid JSON contents");
								}

								((Tag.Builder)map.computeIfAbsent(identifier2, identifierx -> Tag.Builder.create())).read(jsonObject, resourceRef.getPackName());
							} catch (Throwable var18) {
								try {
									reader.close();
								} catch (Throwable var17) {
									var18.addSuppressed(var17);
								}

								throw var18;
							}

							reader.close();
						} catch (Throwable var19) {
							if (inputStream != null) {
								try {
									inputStream.close();
								} catch (Throwable var16) {
									var19.addSuppressed(var16);
								}
							}

							throw var19;
						}

						if (inputStream != null) {
							inputStream.close();
						}
					} catch (Throwable var20) {
						if (resource != null) {
							try {
								resource.close();
							} catch (Throwable var15) {
								var20.addSuppressed(var15);
							}
						}

						throw var20;
					}

					if (resource != null) {
						resource.close();
					}
				} catch (Exception var21) {
					LOGGER.error("Couldn't read tag list {} from {} in data pack {}", identifier2, identifier, resourceRef.getPackName(), var21);
				}
			}
		}

		return map;
	}

	private static void method_32839(
		Map<Identifier, Tag.Builder> map,
		Multimap<Identifier, Identifier> multimap,
		Set<Identifier> set,
		Identifier identifier,
		BiConsumer<Identifier, Tag.Builder> biConsumer
	) {
		if (set.add(identifier)) {
			multimap.get(identifier).forEach(identifierx -> method_32839(map, multimap, set, identifierx, biConsumer));
			Tag.Builder builder = (Tag.Builder)map.get(identifier);
			if (builder != null) {
				biConsumer.accept(identifier, builder);
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

	public Map<Identifier, Tag<T>> buildGroup(Map<Identifier, Tag.Builder> tags) {
		Map<Identifier, Tag<T>> map = Maps.<Identifier, Tag<T>>newHashMap();
		Function<Identifier, Tag<T>> function = map::get;
		Function<Identifier, T> function2 = id -> ((Optional)this.registryGetter.apply(id)).orElse(null);
		Multimap<Identifier, Identifier> multimap = HashMultimap.create();
		tags.forEach((identifier, builder) -> builder.forEachTagId(identifier2 -> method_32844(multimap, identifier, identifier2)));
		tags.forEach((identifier, builder) -> builder.forEachGroupId(identifier2 -> method_32844(multimap, identifier, identifier2)));
		Set<Identifier> set = Sets.<Identifier>newHashSet();
		tags.keySet()
			.forEach(
				identifier -> method_32839(
						tags,
						multimap,
						set,
						identifier,
						(identifierx, builder) -> builder.build(function, function2)
								.ifLeft(
									collection -> LOGGER.error(
											"Couldn't load tag {} as it is missing following references: {}",
											identifierx,
											collection.stream().map(Objects::toString).collect(Collectors.joining(", "))
										)
								)
								.ifRight(tag -> map.put(identifierx, tag))
					)
			);
		return map;
	}

	public Map<Identifier, Tag<T>> load(ResourceManager manager) {
		return this.buildGroup(this.loadTags(manager));
	}
}
