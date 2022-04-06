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
	private static final String FILE_SUFFIX = ".json";
	private static final int FILE_SUFFIX_LENGTH = ".json".length();
	private final Gson gson;
	private final String dataType;

	public JsonDataLoader(Gson gson, String dataType) {
		this.gson = gson;
		this.dataType = dataType;
	}

	protected Map<Identifier, JsonElement> prepare(ResourceManager resourceManager, Profiler profiler) {
		Map<Identifier, JsonElement> map = Maps.<Identifier, JsonElement>newHashMap();
		int i = this.dataType.length() + 1;

		for (Entry<Identifier, Resource> entry : resourceManager.findResources(this.dataType, id -> id.getPath().endsWith(".json")).entrySet()) {
			Identifier identifier = (Identifier)entry.getKey();
			String string = identifier.getPath();
			Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(i, string.length() - FILE_SUFFIX_LENGTH));

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
				LOGGER.error("Couldn't parse data file {} from {}", identifier2, identifier, var15);
			}
		}

		return map;
	}
}
