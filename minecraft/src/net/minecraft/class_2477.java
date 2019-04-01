package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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

public class class_2477 {
	private static final Logger field_11490 = LogManager.getLogger();
	private static final Pattern field_11489 = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
	private static final class_2477 field_11486 = new class_2477();
	private final Map<String, String> field_11487 = Maps.<String, String>newHashMap();
	private long field_11488;

	public class_2477() {
		try {
			InputStream inputStream = class_2477.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");
			JsonElement jsonElement = new Gson().fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonElement.class);
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "strings");

			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				String string = field_11489.matcher(class_3518.method_15287((JsonElement)entry.getValue(), (String)entry.getKey())).replaceAll("%$1s");
				this.field_11487.put(entry.getKey(), string);
			}

			this.field_11488 = class_156.method_658();
		} catch (JsonParseException var7) {
			field_11490.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", (Throwable)var7);
		}
	}

	public static class_2477 method_10517() {
		return field_11486;
	}

	@Environment(EnvType.CLIENT)
	public static synchronized void method_10515(Map<String, String> map) {
		field_11486.field_11487.clear();
		field_11486.field_11487.putAll(map);
		field_11486.field_11488 = class_156.method_658();
	}

	public synchronized String method_10520(String string) {
		return this.method_10518(string);
	}

	private String method_10518(String string) {
		String string2 = (String)this.field_11487.get(string);
		return string2 == null ? string : string2;
	}

	public synchronized boolean method_10516(String string) {
		return this.field_11487.containsKey(string);
	}

	public long method_10519() {
		return this.field_11488;
	}
}
