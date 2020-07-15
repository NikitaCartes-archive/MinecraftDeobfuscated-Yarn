package net.minecraft.util.dynamic;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryOps<T> extends ForwardingDynamicOps<T> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ResourceManager resourceManager;
	private final DynamicRegistryManager registryManager;
	private final Map<RegistryKey<? extends Registry<?>>, RegistryOps.ValueHolder<?>> valueHolders = Maps.<RegistryKey<? extends Registry<?>>, RegistryOps.ValueHolder<?>>newIdentityHashMap();

	public static <T> RegistryOps<T> of(DynamicOps<T> delegate, ResourceManager resourceManager, DynamicRegistryManager registryTracker) {
		return new RegistryOps<>(delegate, resourceManager, registryTracker);
	}

	private RegistryOps(DynamicOps<T> delegate, ResourceManager resourceManager, DynamicRegistryManager registryTracker) {
		super(delegate);
		this.resourceManager = resourceManager;
		this.registryManager = registryTracker;
	}

	/**
	 * Encode an id for a registry element than a full object if possible.
	 * 
	 * <p>This method is called by casting an arbitrary dynamic ops to a registry
	 * reading ops.</p>
	 * 
	 * @see RegistryReadingOps#encodeOrId(Object, Object, RegistryKey, MapCodec)
	 */
	protected <E> DataResult<Pair<Supplier<E>, T>> decodeOrId(T object, RegistryKey<? extends Registry<E>> registryKey, MapCodec<E> mapCodec) {
		Optional<MutableRegistry<E>> optional = this.registryManager.getOptional(registryKey);
		if (!optional.isPresent()) {
			return DataResult.error("Unknown registry: " + registryKey);
		} else {
			MutableRegistry<E> mutableRegistry = (MutableRegistry<E>)optional.get();
			DataResult<Pair<Identifier, T>> dataResult = Identifier.CODEC.decode(this.delegate, object);
			if (!dataResult.result().isPresent()) {
				return SimpleRegistry.method_30516(registryKey, mapCodec).codec().decode(this.delegate, object).map(pairx -> pairx.mapFirst(pairxx -> {
						mutableRegistry.add((RegistryKey<E>)pairxx.getFirst(), pairxx.getSecond());
						mutableRegistry.markLoaded((RegistryKey<E>)pairxx.getFirst());
						return pairxx::getSecond;
					}));
			} else {
				Pair<Identifier, T> pair = (Pair<Identifier, T>)dataResult.result().get();
				Identifier identifier = pair.getFirst();
				return this.readSupplier(registryKey, mutableRegistry, mapCodec, identifier).map(supplier -> Pair.of(supplier, pair.getSecond()));
			}
		}
	}

	/**
	 * Loads elements into a registry just loaded from a decoder.
	 */
	public <E> DataResult<SimpleRegistry<E>> loadToRegistry(SimpleRegistry<E> registry, RegistryKey<? extends Registry<E>> registryRef, MapCodec<E> mapCodec) {
		Identifier identifier = registryRef.getValue();
		Collection<Identifier> collection = this.resourceManager.findResources(identifier, stringx -> stringx.endsWith(".json"));
		DataResult<SimpleRegistry<E>> dataResult = DataResult.success(registry, Lifecycle.stable());

		for (Identifier identifier2 : collection) {
			String string = identifier2.getPath();
			if (!string.endsWith(".json")) {
				LOGGER.warn("Skipping resource {} since it is not a json file", identifier2);
			} else if (!string.startsWith(identifier.getPath() + "/")) {
				LOGGER.warn("Skipping resource {} since it does not have a registry name prefix", identifier2);
			} else {
				String string2 = string.substring(0, string.length() - ".json".length()).substring(identifier.getPath().length() + 1);
				int i = string2.indexOf(47);
				if (i < 0) {
					LOGGER.warn("Skipping resource {} since it does not have a namespace", identifier2);
				} else {
					String string3 = string2.substring(0, i);
					String string4 = string2.substring(i + 1);
					Identifier identifier3 = new Identifier(string3, string4);
					dataResult = dataResult.flatMap(simpleRegistry -> this.readSupplier(registryRef, simpleRegistry, mapCodec, identifier3).map(supplier -> simpleRegistry));
				}
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
		RegistryKey<? extends Registry<E>> registryRef, MutableRegistry<E> registry, MapCodec<E> mapCodec, Identifier elementId
	) {
		RegistryKey<E> registryKey = RegistryKey.of(registryRef, elementId);
		E object = registry.get(registryKey);
		if (object != null) {
			return DataResult.success(() -> object, Lifecycle.stable());
		} else {
			RegistryOps.ValueHolder<E> valueHolder = this.getValueHolder(registryRef);
			DataResult<Supplier<E>> dataResult = (DataResult<Supplier<E>>)valueHolder.values.get(registryKey);
			if (dataResult != null) {
				return dataResult;
			} else {
				Supplier<E> supplier = Suppliers.memoize(() -> {
					E objectx = registry.get(registryKey);
					if (objectx == null) {
						throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + registryKey);
					} else {
						return (T)objectx;
					}
				});
				valueHolder.values.put(registryKey, DataResult.success(supplier));
				DataResult<E> dataResult2 = this.readElement(registryRef, registryKey, mapCodec);
				dataResult2.result().ifPresent(objectx -> registry.add(registryKey, objectx));
				DataResult<Supplier<E>> dataResult3 = dataResult2.map(objectx -> () -> objectx);
				valueHolder.values.put(registryKey, dataResult3);
				return dataResult3;
			}
		}
	}

	/**
	 * Reads the actual element.
	 */
	private <E> DataResult<E> readElement(RegistryKey<? extends Registry<E>> registryRef, RegistryKey<E> elementRef, MapCodec<E> mapCodec) {
		Identifier identifier = new Identifier(
			registryRef.getValue().getNamespace(),
			registryRef.getValue().getPath() + "/" + elementRef.getValue().getNamespace() + "/" + elementRef.getValue().getPath() + ".json"
		);

		try {
			Resource resource = this.resourceManager.getResource(identifier);
			Throwable var6 = null;

			DataResult var11;
			try {
				Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
				Throwable var8 = null;

				try {
					JsonParser jsonParser = new JsonParser();
					JsonElement jsonElement = jsonParser.parse(reader);
					var11 = mapCodec.codec().parse(new RegistryOps<>(JsonOps.INSTANCE, this.resourceManager, this.registryManager), jsonElement);
				} catch (Throwable var36) {
					var8 = var36;
					throw var36;
				} finally {
					if (reader != null) {
						if (var8 != null) {
							try {
								reader.close();
							} catch (Throwable var35) {
								var8.addSuppressed(var35);
							}
						} else {
							reader.close();
						}
					}
				}
			} catch (Throwable var38) {
				var6 = var38;
				throw var38;
			} finally {
				if (resource != null) {
					if (var6 != null) {
						try {
							resource.close();
						} catch (Throwable var34) {
							var6.addSuppressed(var34);
						}
					} else {
						resource.close();
					}
				}
			}

			return var11;
		} catch (JsonIOException | JsonSyntaxException | IOException var40) {
			return DataResult.error("Failed to parse file: " + var40.getMessage());
		}
	}

	private <E> RegistryOps.ValueHolder<E> getValueHolder(RegistryKey<? extends Registry<E>> registryRef) {
		return (RegistryOps.ValueHolder<E>)this.valueHolders.computeIfAbsent(registryRef, registryKey -> new RegistryOps.ValueHolder());
	}

	static final class ValueHolder<E> {
		private final Map<RegistryKey<E>, DataResult<Supplier<E>>> values = Maps.<RegistryKey<E>, DataResult<Supplier<E>>>newIdentityHashMap();

		private ValueHolder() {
		}
	}
}
