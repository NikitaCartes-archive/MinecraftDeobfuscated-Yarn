package net.minecraft.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Language {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Pattern field_11489 = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
	private static final Language INSTANCE = new Language();
	private final Map<String, String> translations = Maps.<String, String>newHashMap();
	private long timeLoaded;

	public Language() {
		try {
			InputStream inputStream = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");
			Throwable var2 = null;

			try {
				JsonElement jsonElement = new Gson().fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonElement.class);
				JsonObject jsonObject = JsonHelper.asObject(jsonElement, "strings");

				for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
					String string = field_11489.matcher(JsonHelper.asString((JsonElement)entry.getValue(), (String)entry.getKey())).replaceAll("%$1s");
					this.translations.put(entry.getKey(), string);
				}

				this.timeLoaded = Util.getMeasuringTimeMs();
			} catch (Throwable var16) {
				var2 = var16;
				throw var16;
			} finally {
				if (inputStream != null) {
					if (var2 != null) {
						try {
							inputStream.close();
						} catch (Throwable var15) {
							var2.addSuppressed(var15);
						}
					} else {
						inputStream.close();
					}
				}
			}
		} catch (JsonParseException | IOException var18) {
			LOGGER.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", (Throwable)var18);
		}
	}

	public static Language getInstance() {
		return INSTANCE;
	}

	@Environment(EnvType.CLIENT)
	public static synchronized void load(Map<String, String> map) {
		INSTANCE.translations.clear();
		INSTANCE.translations.putAll(map);
		INSTANCE.timeLoaded = Util.getMeasuringTimeMs();
	}

	public synchronized String translate(String string) {
		return this.getTranslation(string);
	}

	private String getTranslation(String string) {
		String string2 = (String)this.translations.get(string);
		return string2 == null ? string : string2;
	}

	public synchronized boolean hasTranslation(String string) {
		return this.translations.containsKey(string);
	}

	public long getTimeLoaded() {
		return this.timeLoaded;
	}
}
