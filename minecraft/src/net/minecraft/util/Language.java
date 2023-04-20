package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.TextVisitFactory;
import org.slf4j.Logger;

public abstract class Language {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = new Gson();
	private static final Pattern TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
	public static final String DEFAULT_LANGUAGE = "en_us";
	private static volatile Language instance = create();

	private static Language create() {
		Builder<String, String> builder = ImmutableMap.builder();
		BiConsumer<String, String> biConsumer = builder::put;
		load(biConsumer, "/assets/minecraft/lang/en_us.json");
		final Map<String, String> map = builder.build();
		return new Language() {
			@Override
			public String get(String key, String fallback) {
				return (String)map.getOrDefault(key, fallback);
			}

			@Override
			public boolean hasTranslation(String key) {
				return map.containsKey(key);
			}

			@Override
			public boolean isRightToLeft() {
				return false;
			}

			@Override
			public OrderedText reorder(StringVisitable text) {
				return visitor -> text.visit(
							(style, string) -> TextVisitFactory.visitFormatted(string, style, visitor) ? Optional.empty() : StringVisitable.TERMINATE_VISIT, Style.EMPTY
						)
						.isPresent();
			}
		};
	}

	private static void load(BiConsumer<String, String> entryConsumer, String path) {
		try {
			InputStream inputStream = Language.class.getResourceAsStream(path);

			try {
				load(inputStream, entryConsumer);
			} catch (Throwable var6) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var5) {
						var6.addSuppressed(var5);
					}
				}

				throw var6;
			}

			if (inputStream != null) {
				inputStream.close();
			}
		} catch (JsonParseException | IOException var7) {
			LOGGER.error("Couldn't read strings from {}", path, var7);
		}
	}

	public static void load(InputStream inputStream, BiConsumer<String, String> entryConsumer) {
		JsonObject jsonObject = GSON.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class);

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String string = TOKEN_PATTERN.matcher(JsonHelper.asString((JsonElement)entry.getValue(), (String)entry.getKey())).replaceAll("%$1s");
			entryConsumer.accept((String)entry.getKey(), string);
		}
	}

	public static Language getInstance() {
		return instance;
	}

	public static void setInstance(Language language) {
		instance = language;
	}

	public String get(String key) {
		return this.get(key, key);
	}

	public abstract String get(String key, String fallback);

	public abstract boolean hasTranslation(String key);

	public abstract boolean isRightToLeft();

	public abstract OrderedText reorder(StringVisitable text);

	public List<OrderedText> reorder(List<StringVisitable> texts) {
		return (List<OrderedText>)texts.stream().map(this::reorder).collect(ImmutableList.toImmutableList());
	}
}
