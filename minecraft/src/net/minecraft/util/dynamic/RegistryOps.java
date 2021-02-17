package net.minecraft.util.dynamic;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.DataResult.PartialResult;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryOps<T> extends ForwardingDynamicOps<T> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final RegistryOps.EntryLoader entryLoader;
	private final DynamicRegistryManager.Impl registryManager;
	private final Map<RegistryKey<? extends Registry<?>>, RegistryOps.ValueHolder<?>> valueHolders;
	private final RegistryOps<JsonElement> entryOps;

	public static <T> RegistryOps<T> of(DynamicOps<T> delegate, ResourceManager resourceManager, DynamicRegistryManager.Impl registryManager) {
		return of(delegate, RegistryOps.EntryLoader.resourceBacked(resourceManager), registryManager);
	}

	public static <T> RegistryOps<T> of(DynamicOps<T> delegate, RegistryOps.EntryLoader entryLoader, DynamicRegistryManager.Impl registryManager) {
		RegistryOps<T> registryOps = new RegistryOps<>(delegate, entryLoader, registryManager, Maps.newIdentityHashMap());
		DynamicRegistryManager.load(registryManager, registryOps);
		return registryOps;
	}

	private RegistryOps(
		DynamicOps<T> delegate,
		RegistryOps.EntryLoader entryLoader,
		DynamicRegistryManager.Impl registryManager,
		IdentityHashMap<RegistryKey<? extends Registry<?>>, RegistryOps.ValueHolder<?>> valueHolders
	) {
		super(delegate);
		this.entryLoader = entryLoader;
		this.registryManager = registryManager;
		this.valueHolders = valueHolders;
		this.entryOps = delegate == JsonOps.INSTANCE ? this : new RegistryOps<>(JsonOps.INSTANCE, entryLoader, registryManager, valueHolders);
	}

	/**
	 * Encode an id for a registry element than a full object if possible.
	 * 
	 * <p>This method is called by casting an arbitrary dynamic ops to a registry
	 * reading ops.</p>
	 * 
	 * @see RegistryReadingOps#encodeOrId(Object, Object, RegistryKey, Codec)
	 */
	protected <E> DataResult<Pair<Supplier<E>, T>> decodeOrId(
		T object, RegistryKey<? extends Registry<E>> registryKey, Codec<E> codec, boolean allowInlineDefinitions
	) {
		Optional<MutableRegistry<E>> optional = this.registryManager.getOptionalMutable(registryKey);
		if (!optional.isPresent()) {
			return DataResult.error("Unknown registry: " + registryKey);
		} else {
			MutableRegistry<E> mutableRegistry = (MutableRegistry<E>)optional.get();
			DataResult<Pair<Identifier, T>> dataResult = Identifier.CODEC.decode(this.delegate, object);
			if (!dataResult.result().isPresent()) {
				return !allowInlineDefinitions
					? DataResult.error("Inline definitions not allowed here")
					: codec.decode(this, object).map(pairx -> pairx.mapFirst(objectx -> () -> objectx));
			} else {
				Pair<Identifier, T> pair = (Pair<Identifier, T>)dataResult.result().get();
				Identifier identifier = pair.getFirst();
				return this.readSupplier(registryKey, mutableRegistry, codec, identifier).map(supplier -> Pair.of(supplier, pair.getSecond()));
			}
		}
	}

	/**
	 * Loads elements into a registry just loaded from a decoder.
	 */
	public <E> DataResult<SimpleRegistry<E>> loadToRegistry(SimpleRegistry<E> registry, RegistryKey<? extends Registry<E>> registryKey, Codec<E> codec) {
		Collection<Identifier> collection = this.entryLoader.getKnownEntryPaths(registryKey);
		DataResult<SimpleRegistry<E>> dataResult = DataResult.success(registry, Lifecycle.stable());
		String string = registryKey.getValue().getPath() + "/";

		for (Identifier identifier : collection) {
			String string2 = identifier.getPath();
			if (!string2.endsWith(".json")) {
				LOGGER.warn("Skipping resource {} since it is not a json file", identifier);
			} else if (!string2.startsWith(string)) {
				LOGGER.warn("Skipping resource {} since it does not have a registry name prefix", identifier);
			} else {
				String string3 = string2.substring(string.length(), string2.length() - ".json".length());
				Identifier identifier2 = new Identifier(identifier.getNamespace(), string3);
				dataResult = dataResult.flatMap(simpleRegistry -> this.readSupplier(registryKey, simpleRegistry, codec, identifier2).map(supplier -> simpleRegistry));
			}
		}

		return dataResult.setPartial(registry);
	}

	/**
	 * Reads a supplier for a registry element.
	 * 
	 * <p>This logic is used by both {@code decodeOrId} and {@code loadToRegistry}.</p>
	 */
	private <E> DataResult<Supplier<E>> readSupplier(
		RegistryKey<? extends Registry<E>> registryKey, MutableRegistry<E> mutableRegistry, Codec<E> codec, Identifier elementId
	) {
		RegistryKey<E> registryKey2 = RegistryKey.of(registryKey, elementId);
		RegistryOps.ValueHolder<E> valueHolder = this.getValueHolder(registryKey);
		DataResult<Supplier<E>> dataResult = (DataResult<Supplier<E>>)valueHolder.values.get(registryKey2);
		if (dataResult != null) {
			return dataResult;
		} else {
			Supplier<E> supplier = Suppliers.memoize(() -> {
				E object = mutableRegistry.get(registryKey2);
				if (object == null) {
					throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + registryKey2);
				} else {
					return (T)object;
				}
			});
			valueHolder.values.put(registryKey2, DataResult.success(supplier));
			DataResult<Pair<E, OptionalInt>> dataResult2 = this.entryLoader.load(this.entryOps, registryKey, registryKey2, codec);
			Optional<Pair<E, OptionalInt>> optional = dataResult2.result();
			if (optional.isPresent()) {
				Pair<E, OptionalInt> pair = (Pair<E, OptionalInt>)optional.get();
				mutableRegistry.replace(pair.getSecond(), registryKey2, pair.getFirst(), dataResult2.lifecycle());
			}

			DataResult<Supplier<E>> dataResult3;
			if (!optional.isPresent() && mutableRegistry.get(registryKey2) != null) {
				dataResult3 = DataResult.success(() -> mutableRegistry.get(registryKey2), Lifecycle.stable());
			} else {
				dataResult3 = dataResult2.map(pair -> () -> mutableRegistry.get(registryKey2));
			}

			valueHolder.values.put(registryKey2, dataResult3);
			return dataResult3;
		}
	}

	private <E> RegistryOps.ValueHolder<E> getValueHolder(RegistryKey<? extends Registry<E>> registryRef) {
		return (RegistryOps.ValueHolder<E>)this.valueHolders.computeIfAbsent(registryRef, registryKey -> new RegistryOps.ValueHolder());
	}

	protected <E> DataResult<Registry<E>> method_31152(RegistryKey<? extends Registry<E>> registryKey) {
		return (DataResult<Registry<E>>)this.registryManager
			.getOptionalMutable(registryKey)
			.map(mutableRegistry -> DataResult.success(mutableRegistry, mutableRegistry.getLifecycle()))
			.orElseGet(() -> DataResult.error("Unknown registry: " + registryKey));
	}

	public interface EntryLoader {
		/**
		 * @return A collection of file Identifiers of all known entries of the given registry.
		 * Note that these are file Identifiers for use in a resource manager, not the logical names of the entries.
		 */
		Collection<Identifier> getKnownEntryPaths(RegistryKey<? extends Registry<?>> registryKey);

		<E> DataResult<Pair<E, OptionalInt>> load(
			DynamicOps<JsonElement> dynamicOps, RegistryKey<? extends Registry<E>> registryId, RegistryKey<E> entryId, Decoder<E> decoder
		);

		static RegistryOps.EntryLoader resourceBacked(ResourceManager resourceManager) {
			return new RegistryOps.EntryLoader() {
				@Override
				public Collection<Identifier> getKnownEntryPaths(RegistryKey<? extends Registry<?>> registryKey) {
					return resourceManager.findResources(registryKey.getValue().getPath(), string -> string.endsWith(".json"));
				}

				@Override
				public <E> DataResult<Pair<E, OptionalInt>> load(
					DynamicOps<JsonElement> dynamicOps, RegistryKey<? extends Registry<E>> registryId, RegistryKey<E> entryId, Decoder<E> decoder
				) {
					Identifier identifier = entryId.getValue();
					Identifier identifier2 = new Identifier(identifier.getNamespace(), registryId.getValue().getPath() + "/" + identifier.getPath() + ".json");

					try {
						Resource resource = resourceManager.getResource(identifier2);
						Throwable var8 = null;

						DataResult var13;
						try {
							Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
							Throwable var10 = null;

							try {
								JsonParser jsonParser = new JsonParser();
								JsonElement jsonElement = jsonParser.parse(reader);
								var13 = decoder.parse(dynamicOps, jsonElement).map(object -> Pair.of(object, OptionalInt.empty()));
							} catch (Throwable var38) {
								var10 = var38;
								throw var38;
							} finally {
								if (reader != null) {
									if (var10 != null) {
										try {
											reader.close();
										} catch (Throwable var37) {
											var10.addSuppressed(var37);
										}
									} else {
										reader.close();
									}
								}
							}
						} catch (Throwable var40) {
							var8 = var40;
							throw var40;
						} finally {
							if (resource != null) {
								if (var8 != null) {
									try {
										resource.close();
									} catch (Throwable var36) {
										var8.addSuppressed(var36);
									}
								} else {
									resource.close();
								}
							}
						}

						return var13;
					} catch (JsonIOException | JsonSyntaxException | IOException var42) {
						return DataResult.error("Failed to parse " + identifier2 + " file: " + var42.getMessage());
					}
				}

				public String toString() {
					return "ResourceAccess[" + resourceManager + "]";
				}
			};
		}

		public static final class Impl implements RegistryOps.EntryLoader {
			private final Map<RegistryKey<?>, JsonElement> values = Maps.<RegistryKey<?>, JsonElement>newIdentityHashMap();
			private final Object2IntMap<RegistryKey<?>> entryToRawId = new Object2IntOpenCustomHashMap<>(Util.identityHashStrategy());
			private final Map<RegistryKey<?>, Lifecycle> entryToLifecycle = Maps.<RegistryKey<?>, Lifecycle>newIdentityHashMap();

			public <E> void add(DynamicRegistryManager.Impl registryManager, RegistryKey<E> registryKey, Encoder<E> encoder, int rawId, E object, Lifecycle lifecycle) {
				DataResult<JsonElement> dataResult = encoder.encodeStart(RegistryReadingOps.of(JsonOps.INSTANCE, registryManager), object);
				Optional<PartialResult<JsonElement>> optional = dataResult.error();
				if (optional.isPresent()) {
					RegistryOps.LOGGER.error("Error adding element: {}", ((PartialResult)optional.get()).message());
				} else {
					this.values.put(registryKey, dataResult.result().get());
					this.entryToRawId.put(registryKey, rawId);
					this.entryToLifecycle.put(registryKey, lifecycle);
				}
			}

			@Override
			public Collection<Identifier> getKnownEntryPaths(RegistryKey<? extends Registry<?>> registryKey) {
				return (Collection<Identifier>)this.values
					.keySet()
					.stream()
					.filter(registryKey2 -> registryKey2.isOf(registryKey))
					.map(
						registryKey2 -> new Identifier(
								registryKey2.getValue().getNamespace(), registryKey.getValue().getPath() + "/" + registryKey2.getValue().getPath() + ".json"
							)
					)
					.collect(Collectors.toList());
			}

			@Override
			public <E> DataResult<Pair<E, OptionalInt>> load(
				DynamicOps<JsonElement> dynamicOps, RegistryKey<? extends Registry<E>> registryId, RegistryKey<E> entryId, Decoder<E> decoder
			) {
				JsonElement jsonElement = (JsonElement)this.values.get(entryId);
				return jsonElement == null
					? DataResult.error("Unknown element: " + entryId)
					: decoder.parse(dynamicOps, jsonElement)
						.setLifecycle((Lifecycle)this.entryToLifecycle.get(entryId))
						.map(object -> Pair.of(object, OptionalInt.of(this.entryToRawId.getInt(entryId))));
			}
		}
	}

	static final class ValueHolder<E> {
		private final Map<RegistryKey<E>, DataResult<Supplier<E>>> values = Maps.<RegistryKey<E>, DataResult<Supplier<E>>>newIdentityHashMap();

		private ValueHolder() {
		}
	}
}
