package net.minecraft.tag;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagContainer<T> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new Gson();
	private static final int JSON_EXTENSION_LENGTH = ".json".length();
	private final Tag<T> field_23691 = Tag.of(ImmutableSet.of());
	private BiMap<Identifier, Tag<T>> entries = HashBiMap.create();
	private final Function<Identifier, Optional<T>> getter;
	private final String dataType;
	private final String entryType;

	public TagContainer(Function<Identifier, Optional<T>> getter, String dataType, String string) {
		this.getter = getter;
		this.dataType = dataType;
		this.entryType = string;
	}

	@Nullable
	public Tag<T> get(Identifier id) {
		return (Tag<T>)this.entries.get(id);
	}

	public Tag<T> getOrCreate(Identifier id) {
		return (Tag<T>)this.entries.getOrDefault(id, this.field_23691);
	}

	@Nullable
	public Identifier method_26796(Tag<T> tag) {
		return tag instanceof Tag.Identified ? ((Tag.Identified)tag).getId() : (Identifier)this.entries.inverse().get(tag);
	}

	public Identifier method_26798(Tag<T> tag) {
		Identifier identifier = this.method_26796(tag);
		if (identifier == null) {
			throw new IllegalStateException("Unrecognized tag");
		} else {
			return identifier;
		}
	}

	public Collection<Identifier> getKeys() {
		return this.entries.keySet();
	}

	@Environment(EnvType.CLIENT)
	public Collection<Identifier> getTagsFor(T object) {
		List<Identifier> list = Lists.<Identifier>newArrayList();

		for (Entry<Identifier, Tag<T>> entry : this.entries.entrySet()) {
			if (((Tag)entry.getValue()).contains(object)) {
				list.add(entry.getKey());
			}
		}

		return list;
	}

	public CompletableFuture<Map<Identifier, Tag.Builder>> prepareReload(ResourceManager manager, Executor executor) {
		return CompletableFuture.supplyAsync(
			() -> {
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
											LOGGER.error(
												"Couldn't load {} tag list {} from {} in data pack {} as it is empty or null",
												this.entryType,
												identifier2,
												identifier,
												resource.getResourcePackName()
											);
										} else {
											((Tag.Builder)map.computeIfAbsent(identifier2, identifierx -> Tag.Builder.create())).read(jsonObject);
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
								LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", this.entryType, identifier2, identifier, resource.getResourcePackName(), var57);
							} finally {
								IOUtils.closeQuietly(resource);
							}
						}
					} catch (IOException var59) {
						LOGGER.error("Couldn't read {} tag list {} from {}", this.entryType, identifier2, identifier, var59);
					}
				}

				return map;
			},
			executor
		);
	}

	public void applyReload(Map<Identifier, Tag.Builder> map) {
		Map<Identifier, Tag<T>> map2 = Maps.<Identifier, Tag<T>>newHashMap();
		Function<Identifier, Tag<T>> function = map2::get;
		Function<Identifier, T> function2 = identifier -> ((Optional)this.getter.apply(identifier)).orElse(null);

		while (!map.isEmpty()) {
			boolean bl = false;
			Iterator<Entry<Identifier, Tag.Builder>> iterator = map.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Identifier, Tag.Builder> entry = (Entry<Identifier, Tag.Builder>)iterator.next();
				Optional<Tag<T>> optional = ((Tag.Builder)entry.getValue()).build(function, function2);
				if (optional.isPresent()) {
					map2.put(entry.getKey(), optional.get());
					iterator.remove();
					bl = true;
				}
			}

			if (!bl) {
				break;
			}
		}

		map.forEach(
			(identifier, builder) -> LOGGER.error(
					"Couldn't load {} tag {} as it is missing following references: {}",
					this.entryType,
					identifier,
					builder.streamUnresolvedEntries(function, function2).map(Objects::toString).collect(Collectors.joining(","))
				)
		);
		this.setEntries(map2);
	}

	protected void setEntries(Map<Identifier, Tag<T>> entries) {
		this.entries = ImmutableBiMap.copyOf(entries);
	}

	public Map<Identifier, Tag<T>> getEntries() {
		return this.entries;
	}
}
