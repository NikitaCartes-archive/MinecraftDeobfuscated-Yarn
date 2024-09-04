package net.minecraft.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

/**
 * An abstract implementation of resource reloader that reads JSON files
 * into values in the prepare stage.
 */
public abstract class JsonDataLoader<T> extends SinglePreparationResourceReloader<Map<Identifier, T>> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final DynamicOps<JsonElement> ops;
	private final Codec<T> codec;
	private final String dataType;

	protected JsonDataLoader(RegistryWrapper.WrapperLookup registries, Codec<T> codec, String dataType) {
		this(registries.getOps(JsonOps.INSTANCE), codec, dataType);
	}

	protected JsonDataLoader(Codec<T> codec, String dataType) {
		this(JsonOps.INSTANCE, codec, dataType);
	}

	private JsonDataLoader(DynamicOps<JsonElement> ops, Codec<T> codec, String dataType) {
		this.ops = ops;
		this.codec = codec;
		this.dataType = dataType;
	}

	protected Map<Identifier, T> prepare(ResourceManager resourceManager, Profiler profiler) {
		Map<Identifier, T> map = new HashMap();
		load(resourceManager, this.dataType, this.ops, this.codec, map);
		return map;
	}

	public static <T> void load(ResourceManager manager, String dataType, DynamicOps<JsonElement> ops, Codec<T> codec, Map<Identifier, T> result) {
		ResourceFinder resourceFinder = ResourceFinder.json(dataType);

		for (Entry<Identifier, Resource> entry : resourceFinder.findResources(manager).entrySet()) {
			Identifier identifier = (Identifier)entry.getKey();
			Identifier identifier2 = resourceFinder.toResourceId(identifier);

			try {
				Reader reader = ((Resource)entry.getValue()).getReader();

				try {
					codec.parse(ops, JsonParser.parseReader(reader)).ifSuccess(value -> {
						if (result.putIfAbsent(identifier2, value) != null) {
							throw new IllegalStateException("Duplicate data file ignored with ID " + identifier2);
						}
					}).ifError(error -> LOGGER.error("Couldn't parse data file '{}' from '{}': {}", identifier2, identifier, error));
				} catch (Throwable var14) {
					if (reader != null) {
						try {
							reader.close();
						} catch (Throwable var13) {
							var14.addSuppressed(var13);
						}
					}

					throw var14;
				}

				if (reader != null) {
					reader.close();
				}
			} catch (IllegalArgumentException | IOException | JsonParseException var15) {
				LOGGER.error("Couldn't parse data file '{}' from '{}'", identifier2, identifier, var15);
			}
		}
	}
}
