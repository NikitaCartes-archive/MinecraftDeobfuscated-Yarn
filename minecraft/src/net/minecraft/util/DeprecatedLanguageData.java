package net.minecraft.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

public record DeprecatedLanguageData(List<String> removed, Map<String, String> renamed) {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final DeprecatedLanguageData NONE = new DeprecatedLanguageData(List.of(), Map.of());
	public static final Codec<DeprecatedLanguageData> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.STRING.listOf().fieldOf("removed").forGetter(DeprecatedLanguageData::removed),
					Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("renamed").forGetter(DeprecatedLanguageData::renamed)
				)
				.apply(instance, DeprecatedLanguageData::new)
	);

	public static DeprecatedLanguageData fromInputStream(InputStream stream) {
		JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
		return CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(error -> new IllegalStateException("Failed to parse deprecated language data: " + error));
	}

	public static DeprecatedLanguageData fromPath(String path) {
		try {
			InputStream inputStream = Language.class.getResourceAsStream(path);

			DeprecatedLanguageData var2;
			label49: {
				try {
					if (inputStream != null) {
						var2 = fromInputStream(inputStream);
						break label49;
					}
				} catch (Throwable var5) {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (Throwable var4) {
							var5.addSuppressed(var4);
						}
					}

					throw var5;
				}

				if (inputStream != null) {
					inputStream.close();
				}

				return NONE;
			}

			if (inputStream != null) {
				inputStream.close();
			}

			return var2;
		} catch (Exception var6) {
			LOGGER.error("Failed to read {}", path, var6);
			return NONE;
		}
	}

	public static DeprecatedLanguageData create() {
		return fromPath("/assets/minecraft/lang/deprecated.json");
	}

	public void apply(Map<String, String> map) {
		for (String string : this.removed) {
			map.remove(string);
		}

		this.renamed.forEach((oldKey, newKey) -> {
			String stringx = (String)map.remove(oldKey);
			if (stringx == null) {
				LOGGER.warn("Missing translation key for rename: {}", oldKey);
				map.remove(newKey);
			} else {
				map.put(newKey, stringx);
			}
		});
	}
}
