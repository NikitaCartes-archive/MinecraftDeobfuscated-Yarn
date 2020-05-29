package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
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
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Language {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson field_25307 = new Gson();
	private static final Pattern TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
	private static volatile Language INSTANCE = method_29429();

	private static Language method_29429() {
		Builder<String, String> builder = ImmutableMap.builder();
		BiConsumer<String, String> biConsumer = builder::put;

		try {
			InputStream inputStream = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");
			Throwable var3 = null;

			try {
				method_29425(inputStream, biConsumer);
			} catch (Throwable var13) {
				var3 = var13;
				throw var13;
			} finally {
				if (inputStream != null) {
					if (var3 != null) {
						try {
							inputStream.close();
						} catch (Throwable var12) {
							var3.addSuppressed(var12);
						}
					} else {
						inputStream.close();
					}
				}
			}
		} catch (JsonParseException | IOException var15) {
			LOGGER.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", (Throwable)var15);
		}

		final Map<String, String> map = builder.build();
		return new Language() {
			@Override
			public String get(String string) {
				return (String)map.getOrDefault(string, string);
			}

			@Override
			public boolean hasTranslation(String key) {
				return map.containsKey(key);
			}

			@Environment(EnvType.CLIENT)
			@Override
			public boolean method_29428() {
				return false;
			}

			@Override
			public String method_29426(String string, boolean bl) {
				return string;
			}
		};
	}

	public static void method_29425(InputStream inputStream, BiConsumer<String, String> biConsumer) {
		JsonObject jsonObject = field_25307.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class);

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String string = TOKEN_PATTERN.matcher(JsonHelper.asString((JsonElement)entry.getValue(), (String)entry.getKey())).replaceAll("%$1s");
			biConsumer.accept(entry.getKey(), string);
		}
	}

	public static Language getInstance() {
		return INSTANCE;
	}

	@Environment(EnvType.CLIENT)
	public static void method_29427(Language language) {
		INSTANCE = language;
	}

	public abstract String get(String string);

	public abstract boolean hasTranslation(String key);

	@Environment(EnvType.CLIENT)
	public abstract boolean method_29428();

	public abstract String method_29426(String string, boolean bl);
}
