package net.minecraft.resource;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

/**
 * An abstract implementation of resource reloader that reads JSON files
 * into Gson representations in the prepare stage.
 */
public abstract class JsonDataLoader extends SinglePreparationResourceReloader<Map<Identifier, JsonElement>> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Gson gson;
	private final String dataType;

	public JsonDataLoader(Gson gson, String dataType) {
		this.gson = gson;
		this.dataType = dataType;
	}

	protected Map<Identifier, JsonElement> prepare(ResourceManager resourceManager, Profiler profiler) {
		Map<Identifier, JsonElement> map = Maps.<Identifier, JsonElement>newHashMap();
		ResourceFinder resourceFinder = ResourceFinder.json(this.dataType);

		for (Entry<Identifier, Resource> entry : resourceFinder.findResources(resourceManager).entrySet()) {
			Identifier identifier = (Identifier)entry.getKey();
			Identifier identifier2 = resourceFinder.toResourceId(identifier);

			try {
				Reader reader = ((Resource)entry.getValue()).getReader();

				try {
					JsonElement jsonElement = JsonHelper.deserialize(this.gson, reader, JsonElement.class);
					if (jsonElement != null) {
						JsonElement jsonElement2 = (JsonElement)map.put(identifier2, jsonElement);
						if (jsonElement2 != null) {
							throw new IllegalStateException("Duplicate data file ignored with ID " + identifier2);
						}
					} else {
						LOGGER.error("Couldn't load data file {} from {} as it's null or empty", identifier2, identifier);
					}
				} catch (Throwable var13) {
					if (reader != null) {
						try {
							reader.close();
						} catch (Throwable var12) {
							var13.addSuppressed(var12);
						}
					}

					throw var13;
				}

				if (reader != null) {
					reader.close();
				}
			} catch (IllegalArgumentException | IOException | JsonParseException var14) {
				LOGGER.error("Couldn't parse data file {} from {}", identifier2, identifier, var14);
			}
		}

		return map;
	}
}
