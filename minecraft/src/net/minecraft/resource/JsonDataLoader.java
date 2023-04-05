package net.minecraft.resource;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
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
		Map<Identifier, JsonElement> map = new HashMap();
		load(resourceManager, this.dataType, this.gson, map);
		return map;
	}

	public static void load(ResourceManager manager, String dataType, Gson gson, Map<Identifier, JsonElement> results) {
		ResourceFinder resourceFinder = ResourceFinder.json(dataType);

		for (Entry<Identifier, Resource> entry : resourceFinder.findResources(manager).entrySet()) {
			Identifier identifier = (Identifier)entry.getKey();
			Identifier identifier2 = resourceFinder.toResourceId(identifier);

			try {
				Reader reader = ((Resource)entry.getValue()).getReader();

				try {
					JsonElement jsonElement = JsonHelper.deserialize(gson, reader, JsonElement.class);
					JsonElement jsonElement2 = (JsonElement)results.put(identifier2, jsonElement);
					if (jsonElement2 != null) {
						throw new IllegalStateException("Duplicate data file ignored with ID " + identifier2);
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
	}
}
