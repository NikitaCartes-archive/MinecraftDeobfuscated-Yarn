package net.minecraft.resource;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class JsonDataLoader extends SinglePreparationResourceReloadListener<Map<Identifier, JsonObject>> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int FILE_SUFFIX_LENGTH = ".json".length();
	private final Gson gson;
	private final String dataType;

	public JsonDataLoader(Gson gson, String string) {
		this.gson = gson;
		this.dataType = string;
	}

	protected Map<Identifier, JsonObject> method_20731(ResourceManager resourceManager, Profiler profiler) {
		Map<Identifier, JsonObject> map = Maps.<Identifier, JsonObject>newHashMap();
		int i = this.dataType.length() + 1;

		for (Identifier identifier : resourceManager.findResources(this.dataType, stringx -> stringx.endsWith(".json"))) {
			String string = identifier.getPath();
			Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(i, string.length() - FILE_SUFFIX_LENGTH));

			try {
				Resource resource = resourceManager.getResource(identifier);
				Throwable var10 = null;

				try {
					InputStream inputStream = resource.getInputStream();
					Throwable var12 = null;

					try {
						Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
						Throwable var14 = null;

						try {
							JsonObject jsonObject = JsonHelper.deserialize(this.gson, reader, JsonObject.class);
							if (jsonObject != null) {
								JsonObject jsonObject2 = (JsonObject)map.put(identifier2, jsonObject);
								if (jsonObject2 != null) {
									throw new IllegalStateException("Duplicate data file ignored with ID " + identifier2);
								}
							} else {
								LOGGER.error("Couldn't load data file {} from {} as it's null or empty", identifier2, identifier);
							}
						} catch (Throwable var62) {
							var14 = var62;
							throw var62;
						} finally {
							if (reader != null) {
								if (var14 != null) {
									try {
										reader.close();
									} catch (Throwable var61) {
										var14.addSuppressed(var61);
									}
								} else {
									reader.close();
								}
							}
						}
					} catch (Throwable var64) {
						var12 = var64;
						throw var64;
					} finally {
						if (inputStream != null) {
							if (var12 != null) {
								try {
									inputStream.close();
								} catch (Throwable var60) {
									var12.addSuppressed(var60);
								}
							} else {
								inputStream.close();
							}
						}
					}
				} catch (Throwable var66) {
					var10 = var66;
					throw var66;
				} finally {
					if (resource != null) {
						if (var10 != null) {
							try {
								resource.close();
							} catch (Throwable var59) {
								var10.addSuppressed(var59);
							}
						} else {
							resource.close();
						}
					}
				}
			} catch (IllegalArgumentException | IOException | JsonParseException var68) {
				LOGGER.error("Couldn't parse data file {} from {}", identifier2, identifier, var68);
			}
		}

		return map;
	}
}