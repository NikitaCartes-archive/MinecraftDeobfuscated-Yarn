package net.minecraft.util.dynamic;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.DataResult.PartialResult;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.slf4j.Logger;

public interface EntryLoader {
	/**
	 * @return A collection of file Identifiers of all known entries of the given registry.
	 * Note that these are file Identifiers for use in a resource manager, not the logical names of the entries.
	 */
	<E> Map<RegistryKey<E>, EntryLoader.Parseable<E>> getKnownEntryPaths(RegistryKey<? extends Registry<E>> key);

	<E> Optional<EntryLoader.Parseable<E>> createParseable(RegistryKey<E> key);

	static EntryLoader resourceBacked(ResourceManager resourceManager) {
		return new EntryLoader() {
			private static final String JSON = ".json";

			@Override
			public <E> Map<RegistryKey<E>, EntryLoader.Parseable<E>> getKnownEntryPaths(RegistryKey<? extends Registry<E>> key) {
				String string = getPath(key.getValue());
				Map<RegistryKey<E>, EntryLoader.Parseable<E>> map = Maps.<RegistryKey<E>, EntryLoader.Parseable<E>>newHashMap();
				resourceManager.findResources(string, id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
					String string2 = id.getPath();
					String string3 = string2.substring(string.length() + 1, string2.length() - ".json".length());
					RegistryKey<E> registryKey2 = RegistryKey.of(key, new Identifier(id.getNamespace(), string3));
					map.put(registryKey2, (EntryLoader.Parseable<>)(jsonOps, decoder) -> {
						try {
							Resource resource = resourceRef.open();

							DataResult var6x;
							try {
								var6x = this.parse(jsonOps, decoder, resource);
							} catch (Throwable var9) {
								if (resource != null) {
									try {
										resource.close();
									} catch (Throwable var8x) {
										var9.addSuppressed(var8x);
									}
								}

								throw var9;
							}

							if (resource != null) {
								resource.close();
							}

							return var6x;
						} catch (JsonIOException | JsonSyntaxException | IOException var10) {
							return DataResult.error("Failed to parse " + id + " file: " + var10.getMessage());
						}
					});
				});
				return map;
			}

			@Override
			public <E> Optional<EntryLoader.Parseable<E>> createParseable(RegistryKey<E> key) {
				Identifier identifier = createId(key);
				return !resourceManager.containsResource(identifier) ? Optional.empty() : Optional.of((EntryLoader.Parseable<>)(jsonOps, decoder) -> {
					try {
						Resource resource = resourceManager.getResource(identifier);

						DataResult var6;
						try {
							var6 = this.parse(jsonOps, decoder, resource);
						} catch (Throwable var9) {
							if (resource != null) {
								try {
									resource.close();
								} catch (Throwable var8) {
									var9.addSuppressed(var8);
								}
							}

							throw var9;
						}

						if (resource != null) {
							resource.close();
						}

						return var6;
					} catch (JsonIOException | JsonSyntaxException | IOException var10) {
						return DataResult.error("Failed to parse " + identifier + " file: " + var10.getMessage());
					}
				});
			}

			private <E> DataResult<EntryLoader.Entry<E>> parse(DynamicOps<JsonElement> jsonOps, Decoder<E> decoder, Resource resource) throws IOException {
				Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);

				DataResult var6;
				try {
					JsonElement jsonElement = JsonParser.parseReader(reader);
					var6 = decoder.parse(jsonOps, jsonElement).map(EntryLoader.Entry::of);
				} catch (Throwable var8) {
					try {
						reader.close();
					} catch (Throwable var7) {
						var8.addSuppressed(var7);
					}

					throw var8;
				}

				reader.close();
				return var6;
			}

			private static String getPath(Identifier id) {
				return id.getPath();
			}

			private static <E> Identifier createId(RegistryKey<E> rootKey) {
				return new Identifier(rootKey.getValue().getNamespace(), getPath(rootKey.getRegistry()) + "/" + rootKey.getValue().getPath() + ".json");
			}

			public String toString() {
				return "ResourceAccess[" + resourceManager + "]";
			}
		};
	}

	public static record Entry<E>(E value, OptionalInt fixedId) {
		public static <E> EntryLoader.Entry<E> of(E value) {
			return new EntryLoader.Entry<>(value, OptionalInt.empty());
		}

		public static <E> EntryLoader.Entry<E> of(E value, int id) {
			return new EntryLoader.Entry<>(value, OptionalInt.of(id));
		}
	}

	public static final class Impl implements EntryLoader {
		private static final Logger LOGGER = LogUtils.getLogger();
		private final Map<RegistryKey<?>, EntryLoader.Impl.Element> values = Maps.<RegistryKey<?>, EntryLoader.Impl.Element>newIdentityHashMap();

		public <E> void add(DynamicRegistryManager registryManager, RegistryKey<E> key, Encoder<E> encoder, int rawId, E entry, Lifecycle lifecycle) {
			DataResult<JsonElement> dataResult = encoder.encodeStart(RegistryOps.of(JsonOps.INSTANCE, registryManager), entry);
			Optional<PartialResult<JsonElement>> optional = dataResult.error();
			if (optional.isPresent()) {
				LOGGER.error("Error adding element: {}", ((PartialResult)optional.get()).message());
			} else {
				this.values.put(key, new EntryLoader.Impl.Element((JsonElement)dataResult.result().get(), rawId, lifecycle));
			}
		}

		@Override
		public <E> Map<RegistryKey<E>, EntryLoader.Parseable<E>> getKnownEntryPaths(RegistryKey<? extends Registry<E>> key) {
			return (Map<RegistryKey<E>, EntryLoader.Parseable<E>>)this.values
				.entrySet()
				.stream()
				.filter(entry -> ((RegistryKey)entry.getKey()).isOf(key))
				.collect(Collectors.toMap(entry -> (RegistryKey)entry.getKey(), entry -> ((EntryLoader.Impl.Element)entry.getValue())::parse));
		}

		@Override
		public <E> Optional<EntryLoader.Parseable<E>> createParseable(RegistryKey<E> key) {
			EntryLoader.Impl.Element element = (EntryLoader.Impl.Element)this.values.get(key);
			if (element == null) {
				DataResult<EntryLoader.Entry<E>> dataResult = DataResult.error("Unknown element: " + key);
				return Optional.of((EntryLoader.Parseable<>)(jsonOps, decoder) -> dataResult);
			} else {
				return Optional.of(element::parse);
			}
		}

		static record Element(JsonElement data, int id, Lifecycle lifecycle) {
			public <E> DataResult<EntryLoader.Entry<E>> parse(DynamicOps<JsonElement> jsonOps, Decoder<E> decoder) {
				return decoder.parse(jsonOps, this.data).setLifecycle(this.lifecycle).map(value -> EntryLoader.Entry.of(value, this.id));
			}
		}
	}

	@FunctionalInterface
	public interface Parseable<E> {
		DataResult<EntryLoader.Entry<E>> parseElement(DynamicOps<JsonElement> jsonOps, Decoder<E> decoder);
	}
}
