package net.minecraft.util.dynamic;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface EntryLoader {
	/**
	 * @return A collection of file Identifiers of all known entries of the given registry.
	 * Note that these are file Identifiers for use in a resource manager, not the logical names of the entries.
	 */
	<E> Collection<RegistryKey<E>> getKnownEntryPaths(RegistryKey<? extends Registry<E>> key);

	<E> Optional<DataResult<EntryLoader.Entry<E>>> load(
		DynamicOps<JsonElement> json, RegistryKey<? extends Registry<E>> registryId, RegistryKey<E> entryId, Decoder<E> decoder
	);

	static EntryLoader resourceBacked(ResourceManager resourceManager) {
		return new EntryLoader() {
			private static final String JSON = ".json";

			@Override
			public <E> Collection<RegistryKey<E>> getKnownEntryPaths(RegistryKey<? extends Registry<E>> key) {
				String string = getPath(key);
				Set<RegistryKey<E>> set = new HashSet();
				resourceManager.findResources(string, name -> name.endsWith(".json")).forEach(id -> {
					String string2 = id.getPath();
					String string3 = string2.substring(string.length() + 1, string2.length() - ".json".length());
					set.add(RegistryKey.of(key, new Identifier(id.getNamespace(), string3)));
				});
				return set;
			}

			@Override
			public <E> Optional<DataResult<EntryLoader.Entry<E>>> load(
				DynamicOps<JsonElement> json, RegistryKey<? extends Registry<E>> registryId, RegistryKey<E> entryId, Decoder<E> decoder
			) {
				Identifier identifier = createId(registryId, entryId);
				if (!resourceManager.containsResource(identifier)) {
					return Optional.empty();
				} else {
					try {
						Resource resource = resourceManager.getResource(identifier);

						Optional var9;
						try {
							Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);

							try {
								JsonElement jsonElement = JsonParser.parseReader(reader);
								var9 = Optional.of(decoder.parse(json, jsonElement).map(EntryLoader.Entry::of));
							} catch (Throwable var12) {
								try {
									reader.close();
								} catch (Throwable var11) {
									var12.addSuppressed(var11);
								}

								throw var12;
							}

							reader.close();
						} catch (Throwable var13) {
							if (resource != null) {
								try {
									resource.close();
								} catch (Throwable var10) {
									var13.addSuppressed(var10);
								}
							}

							throw var13;
						}

						if (resource != null) {
							resource.close();
						}

						return var9;
					} catch (JsonIOException | JsonSyntaxException | IOException var14) {
						return Optional.of(DataResult.error("Failed to parse " + identifier + " file: " + var14.getMessage()));
					}
				}
			}

			private static String getPath(RegistryKey<? extends Registry<?>> registryKey) {
				return registryKey.getValue().getPath();
			}

			private static <E> Identifier createId(RegistryKey<? extends Registry<E>> rootKey, RegistryKey<E> key) {
				return new Identifier(key.getValue().getNamespace(), getPath(rootKey) + "/" + key.getValue().getPath() + ".json");
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
		private static final Logger LOGGER = LogManager.getLogger();
		private final Map<RegistryKey<?>, EntryLoader.Impl.Element> values = Maps.<RegistryKey<?>, EntryLoader.Impl.Element>newIdentityHashMap();

		public <E> void add(DynamicRegistryManager.Impl registryManager, RegistryKey<E> key, Encoder<E> encoder, int rawId, E entry, Lifecycle lifecycle) {
			DataResult<JsonElement> dataResult = encoder.encodeStart(RegistryReadingOps.of(JsonOps.INSTANCE, registryManager), entry);
			Optional<PartialResult<JsonElement>> optional = dataResult.error();
			if (optional.isPresent()) {
				LOGGER.error("Error adding element: {}", ((PartialResult)optional.get()).message());
			} else {
				this.values.put(key, new EntryLoader.Impl.Element((JsonElement)dataResult.result().get(), rawId, lifecycle));
			}
		}

		@Override
		public <E> Collection<RegistryKey<E>> getKnownEntryPaths(RegistryKey<? extends Registry<E>> key) {
			return (Collection<RegistryKey<E>>)this.values.keySet().stream().flatMap(registryKey -> registryKey.method_39752(key).stream()).collect(Collectors.toList());
		}

		@Override
		public <E> Optional<DataResult<EntryLoader.Entry<E>>> load(
			DynamicOps<JsonElement> json, RegistryKey<? extends Registry<E>> registryId, RegistryKey<E> entryId, Decoder<E> decoder
		) {
			EntryLoader.Impl.Element element = (EntryLoader.Impl.Element)this.values.get(entryId);
			return element == null
				? Optional.of(DataResult.error("Unknown element: " + entryId))
				: Optional.of(decoder.parse(json, element.data).setLifecycle(element.lifecycle).map(value -> EntryLoader.Entry.of(value, element.id)));
		}

		static record Element(JsonElement data, int id, Lifecycle lifecycle) {
		}
	}
}
