package net.minecraft.client.font;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public enum FontType {
	BITMAP("bitmap", TextureFont.Loader::fromJson),
	TTF("ttf", TrueTypeFontLoader::fromJson),
	LEGACY_UNICODE("legacy_unicode", UnicodeTextureFont.Loader::fromJson);

	private static final Map<String, FontType> REGISTRY = Util.make(Maps.<String, FontType>newHashMap(), hashMap -> {
		for (FontType fontType : values()) {
			hashMap.put(fontType.id, fontType);
		}
	});
	private final String id;
	private final Function<JsonObject, FontLoader> loaderFactory;

	private FontType(String id, Function<JsonObject, FontLoader> factory) {
		this.id = id;
		this.loaderFactory = factory;
	}

	public static FontType byId(String id) {
		FontType fontType = (FontType)REGISTRY.get(id);
		if (fontType == null) {
			throw new IllegalArgumentException("Invalid type: " + id);
		} else {
			return fontType;
		}
	}

	public FontLoader createLoader(JsonObject json) {
		return (FontLoader)this.loaderFactory.apply(json);
	}
}
