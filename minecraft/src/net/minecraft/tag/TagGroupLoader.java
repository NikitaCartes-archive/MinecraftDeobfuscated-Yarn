package net.minecraft.tag;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagGroupLoader<T> {
	private static final Logger LOGGER = LogManager.getLogger();
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

		for (Identifier identifier : manager.findResources(this.dataType, stringx -> stringx.endsWith(".json"))) {
			String string = identifier.getPath();
			Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(this.dataType.length() + 1, string.length() - JSON_EXTENSION_LENGTH));

			try {
				for (Resource resource : manager.getAllResources(identifier)) {
					try {
						InputStream inputStream = resource.getInputStream();
						Throwable var10 = null;

						try {
							Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
							Throwable var12 = null;

							try {
								JsonObject jsonObject = JsonHelper.deserialize(GSON, reader, JsonObject.class);
								if (jsonObject == null) {
									LOGGER.error("Couldn't load tag list {} from {} in data pack {} as it is empty or null", identifier2, identifier, resource.getResourcePackName());
								} else {
									((Tag.Builder)map.computeIfAbsent(identifier2, identifierx -> Tag.Builder.create())).read(jsonObject, resource.getResourcePackName());
								}
							} catch (Throwable var53) {
								var12 = var53;
								throw var53;
							} finally {
								if (reader != null) {
									if (var12 != null) {
										try {
											reader.close();
										} catch (Throwable var52) {
											var12.addSuppressed(var52);
										}
									} else {
										reader.close();
									}
								}
							}
						} catch (Throwable var55) {
							var10 = var55;
							throw var55;
						} finally {
							if (inputStream != null) {
								if (var10 != null) {
									try {
										inputStream.close();
									} catch (Throwable var51) {
										var10.addSuppressed(var51);
									}
								} else {
									inputStream.close();
								}
							}
						}
					} catch (RuntimeException | IOException var57) {
						LOGGER.error("Couldn't read tag list {} from {} in data pack {}", identifier2, identifier, resource.getResourcePackName(), var57);
					} finally {
						IOUtils.closeQuietly(resource);
					}
				}
			} catch (IOException var59) {
				LOGGER.error("Couldn't read tag list {} from {}", identifier2, identifier, var59);
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

	public TagGroup<T> buildGroup(Map<Identifier, Tag.Builder> tags) {
		Map<Identifier, Tag<T>> map = Maps.<Identifier, Tag<T>>newHashMap();
		Function<Identifier, Tag<T>> function = map::get;
		Function<Identifier, T> function2 = identifier -> ((Optional)this.registryGetter.apply(identifier)).orElse(null);
		Multimap<Identifier, Identifier> multimap = HashMultimap.create();
		tags.forEach((identifier, builder) -> builder.method_32826(identifier2 -> method_32844(multimap, identifier, identifier2)));
		tags.forEach((identifier, builder) -> builder.method_32828(identifier2 -> method_32844(multimap, identifier, identifier2)));
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
											collection.stream().map(Objects::toString).collect(Collectors.joining(","))
										)
								)
								.ifRight(tag -> {
									Tag var10000 = (Tag)map.put(identifierx, tag);
								})
					)
			);
		return TagGroup.create(map);
	}

	public TagGroup<T> load(ResourceManager manager) {
		return this.buildGroup(this.loadTags(manager));
	}
}
