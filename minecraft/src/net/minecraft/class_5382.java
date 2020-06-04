package net.minecraft;

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
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.dimension.DimensionTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_5382<T> extends class_5379<T> {
	private static final Logger field_25509 = LogManager.getLogger();
	private final ResourceManager field_25510;
	private final DimensionTracker field_25511;
	private final Map<RegistryKey<? extends Registry<?>>, class_5382.class_5383<?>> field_25512 = Maps.<RegistryKey<? extends Registry<?>>, class_5382.class_5383<?>>newIdentityHashMap();

	public static <T> class_5382<T> method_29753(DynamicOps<T> dynamicOps, ResourceManager resourceManager, DimensionTracker dimensionTracker) {
		return new class_5382<>(dynamicOps, resourceManager, dimensionTracker);
	}

	private class_5382(DynamicOps<T> dynamicOps, ResourceManager resourceManager, DimensionTracker dimensionTracker) {
		super(dynamicOps);
		this.field_25510 = resourceManager;
		this.field_25511 = dimensionTracker;
	}

	protected <E> DataResult<Pair<Supplier<E>, T>> method_29759(T object, RegistryKey<Registry<E>> registryKey, Codec<E> codec) {
		DataResult<Pair<Identifier, T>> dataResult = Identifier.field_25139.decode(this.field_25503, object);
		if (!dataResult.result().isPresent()) {
			return codec.decode(this.field_25503, object).map(pairx -> pairx.mapFirst(objectx -> () -> objectx));
		} else {
			Optional<MutableRegistry<E>> optional = this.field_25511.method_29726(registryKey);
			if (!optional.isPresent()) {
				return DataResult.error("Unknown registry: " + registryKey);
			} else {
				Pair<Identifier, T> pair = (Pair<Identifier, T>)dataResult.result().get();
				Identifier identifier = pair.getFirst();
				return this.method_29763(registryKey, (MutableRegistry<E>)optional.get(), codec, identifier).map(supplier -> Pair.of(supplier, pair.getSecond()));
			}
		}
	}

	public <E> DataResult<SimpleRegistry<E>> method_29755(SimpleRegistry<E> simpleRegistry, RegistryKey<Registry<E>> registryKey, Codec<E> codec) {
		Identifier identifier = registryKey.getValue();
		Collection<Identifier> collection = this.field_25510.method_29489(identifier, stringx -> stringx.endsWith(".json"));
		DataResult<SimpleRegistry<E>> dataResult = DataResult.success(simpleRegistry, Lifecycle.stable());

		for (Identifier identifier2 : collection) {
			String string = identifier2.getPath();
			if (!string.endsWith(".json")) {
				field_25509.warn("Skipping resource {} since it is not a json file", identifier2);
			} else if (!string.startsWith(identifier.getPath() + "/")) {
				field_25509.warn("Skipping resource {} since it does not have a registry name prefix", identifier2);
			} else {
				String string2 = string.substring(0, string.length() - ".json".length()).substring(identifier.getPath().length() + 1);
				int i = string2.indexOf(47);
				if (i < 0) {
					field_25509.warn("Skipping resource {} since it does not have a namespace", identifier2);
				} else {
					String string3 = string2.substring(0, i);
					String string4 = string2.substring(i + 1);
					Identifier identifier3 = new Identifier(string3, string4);
					dataResult = dataResult.flatMap(simpleRegistryx -> this.method_29763(registryKey, simpleRegistryx, codec, identifier3).map(supplier -> simpleRegistryx));
				}
			}
		}

		return dataResult.setPartial(simpleRegistry);
	}

	private <E> DataResult<Supplier<E>> method_29763(
		RegistryKey<Registry<E>> registryKey, MutableRegistry<E> mutableRegistry, Codec<E> codec, Identifier identifier
	) {
		RegistryKey<E> registryKey2 = RegistryKey.of(registryKey, identifier);
		E object = mutableRegistry.get(registryKey2);
		if (object != null) {
			return DataResult.success(() -> object, Lifecycle.stable());
		} else {
			class_5382.class_5383<E> lv = this.method_29761(registryKey);
			DataResult<Supplier<E>> dataResult = (DataResult<Supplier<E>>)lv.field_25513.get(registryKey2);
			if (dataResult != null) {
				return dataResult;
			} else {
				Supplier<E> supplier = Suppliers.memoize(() -> {
					E objectx = mutableRegistry.get(registryKey2);
					if (objectx == null) {
						throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + registryKey2);
					} else {
						return (T)objectx;
					}
				});
				lv.field_25513.put(registryKey2, DataResult.success(supplier));
				DataResult<E> dataResult2 = this.method_29764(registryKey, registryKey2, codec);
				dataResult2.result().ifPresent(objectx -> mutableRegistry.add(registryKey2, objectx));
				DataResult<Supplier<E>> dataResult3 = dataResult2.map(objectx -> () -> objectx);
				lv.field_25513.put(registryKey2, dataResult3);
				return dataResult3;
			}
		}
	}

	private <E> DataResult<E> method_29764(RegistryKey<Registry<E>> registryKey, RegistryKey<E> registryKey2, Codec<E> codec) {
		Identifier identifier = new Identifier(
			registryKey.getValue().getNamespace(),
			registryKey.getValue().getPath() + "/" + registryKey2.getValue().getNamespace() + "/" + registryKey2.getValue().getPath() + ".json"
		);

		try {
			Resource resource = this.field_25510.getResource(identifier);
			Throwable var6 = null;

			DataResult var11;
			try {
				Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
				Throwable var8 = null;

				try {
					JsonParser jsonParser = new JsonParser();
					JsonElement jsonElement = jsonParser.parse(reader);
					var11 = codec.parse(new class_5382<>(JsonOps.INSTANCE, this.field_25510, this.field_25511), jsonElement);
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

	private <E> class_5382.class_5383<E> method_29761(RegistryKey<Registry<E>> registryKey) {
		return (class_5382.class_5383<E>)this.field_25512.computeIfAbsent(registryKey, registryKeyx -> new class_5382.class_5383());
	}

	static final class class_5383<E> {
		private final Map<RegistryKey<E>, DataResult<Supplier<E>>> field_25513 = Maps.<RegistryKey<E>, DataResult<Supplier<E>>>newIdentityHashMap();

		private class_5383() {
		}
	}
}
