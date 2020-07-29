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
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
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
	private final DynamicRegistryManager.Impl registryManager;
	private final Map<RegistryKey<? extends Registry<?>>, RegistryOps.ValueHolder<?>> valueHolders = Maps.<RegistryKey<? extends Registry<?>>, RegistryOps.ValueHolder<?>>newIdentityHashMap();

	public static <T> RegistryOps<T> of(DynamicOps<T> delegate, ResourceManager resourceManager, DynamicRegistryManager.Impl impl) {
		RegistryOps<T> registryOps = new RegistryOps<>(delegate, resourceManager, impl);
		DynamicRegistryManager.load(impl, registryOps);
		return registryOps;
	}

	private RegistryOps(DynamicOps<T> delegate, ResourceManager resourceManager, DynamicRegistryManager.Impl impl) {
		super(delegate);
		this.resourceManager = resourceManager;
		this.registryManager = impl;
	}

	/**
	 * Encode an id for a registry element than a full object if possible.
	 * 
	 * <p>This method is called by casting an arbitrary dynamic ops to a registry
	 * reading ops.</p>
	 * 
	 * @see RegistryReadingOps#encodeOrId(Object, Object, RegistryKey, MapCodec)
	 */
	protected <E> DataResult<Pair<Supplier<E>, T>> decodeOrId(T object, RegistryKey<? extends Registry<E>> registryKey, Codec<E> codec) {
		Optional<MutableRegistry<E>> optional = this.registryManager.getOptional(registryKey);
		if (!optional.isPresent()) {
			return DataResult.error("Unknown registry: " + registryKey);
		} else {
			MutableRegistry<E> mutableRegistry = (MutableRegistry<E>)optional.get();
			DataResult<Pair<Identifier, T>> dataResult = Identifier.CODEC.decode(this.delegate, object);
			if (!dataResult.result().isPresent()) {
				return codec.decode(this.delegate, object).map(pairx -> pairx.mapFirst(objectx -> () -> objectx));
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
	public <E> DataResult<SimpleRegistry<E>> loadToRegistry(SimpleRegistry<E> registry, RegistryKey<? extends Registry<E>> registryRef, Codec<E> codec) {
		Identifier identifier = registryRef.getValue();
		Collection<Identifier> collection = this.resourceManager.findResources(identifier.getPath(), stringx -> stringx.endsWith(".json"));
		DataResult<SimpleRegistry<E>> dataResult = DataResult.success(registry, Lifecycle.stable());
		String string = identifier.getPath() + "/";

		for (Identifier identifier2 : collection) {
			String string2 = identifier2.getPath();
			if (!string2.endsWith(".json")) {
				LOGGER.warn("Skipping resource {} since it is not a json file", identifier2);
			} else if (!string2.startsWith(string)) {
				LOGGER.warn("Skipping resource {} since it does not have a registry name prefix", identifier2);
			} else {
				String string3 = string2.substring(string.length(), string2.length() - ".json".length());
				Identifier identifier3 = new Identifier(identifier2.getNamespace(), string3);
				dataResult = dataResult.flatMap(simpleRegistry -> this.readSupplier(registryRef, simpleRegistry, codec, identifier3).map(supplier -> simpleRegistry));
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
		RegistryKey<? extends Registry<E>> registryRef, MutableRegistry<E> mutableRegistry, Codec<E> codec, Identifier elementId
	) {
		RegistryKey<E> registryKey = RegistryKey.of(registryRef, elementId);
		RegistryOps.ValueHolder<E> valueHolder = this.getValueHolder(registryRef);
		DataResult<Supplier<E>> dataResult = (DataResult<Supplier<E>>)valueHolder.values.get(registryKey);
		if (dataResult != null) {
			return dataResult;
		} else {
			Supplier<E> supplier = Suppliers.memoize(() -> {
				E object = mutableRegistry.get(registryKey);
				if (object == null) {
					throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + registryKey);
				} else {
					return (T)object;
				}
			});
			valueHolder.values.put(registryKey, DataResult.success(supplier));
			DataResult<E> dataResult2 = this.readElement(registryRef, registryKey, codec);
			DataResult<E> dataResult3;
			if (dataResult2.result().isPresent()) {
				mutableRegistry.method_31062(registryKey, dataResult2.result().get());
				dataResult3 = dataResult2;
			} else {
				E object = mutableRegistry.get(registryKey);
				if (object != null) {
					dataResult3 = DataResult.success(object, Lifecycle.stable());
				} else {
					dataResult3 = dataResult2;
				}
			}

			DataResult<Supplier<E>> dataResult4 = dataResult3.map(objectx -> () -> object);
			valueHolder.values.put(registryKey, dataResult4);
			return dataResult4;
		}
	}

	/**
	 * Reads the actual element.
	 */
	private <E> DataResult<E> readElement(RegistryKey<? extends Registry<E>> registryRef, RegistryKey<E> elementRef, Codec<E> codec) {
		Identifier identifier = elementRef.getValue();
		Identifier identifier2 = new Identifier(identifier.getNamespace(), registryRef.getValue().getPath() + "/" + identifier.getPath() + ".json");

		try {
			Resource resource = this.resourceManager.getResource(identifier2);
			Throwable var7 = null;

			DataResult var12;
			try {
				Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
				Throwable var9 = null;

				try {
					JsonParser jsonParser = new JsonParser();
					JsonElement jsonElement = jsonParser.parse(reader);
					var12 = codec.parse(new RegistryOps<>(JsonOps.INSTANCE, this.resourceManager, this.registryManager), jsonElement);
				} catch (Throwable var37) {
					var9 = var37;
					throw var37;
				} finally {
					if (reader != null) {
						if (var9 != null) {
							try {
								reader.close();
							} catch (Throwable var36) {
								var9.addSuppressed(var36);
							}
						} else {
							reader.close();
						}
					}
				}
			} catch (Throwable var39) {
				var7 = var39;
				throw var39;
			} finally {
				if (resource != null) {
					if (var7 != null) {
						try {
							resource.close();
						} catch (Throwable var35) {
							var7.addSuppressed(var35);
						}
					} else {
						resource.close();
					}
				}
			}

			return var12;
		} catch (JsonIOException | JsonSyntaxException | IOException var41) {
			return DataResult.error("Failed to parse " + identifier2 + " file: " + var41.getMessage());
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
