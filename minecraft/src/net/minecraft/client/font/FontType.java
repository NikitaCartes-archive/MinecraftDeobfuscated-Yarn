package net.minecraft.client.font;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public enum FontType {
	BITMAP("bitmap", TextureFont.Loader::fromJson),
	TTF("ttf", TrueTypeFontLoader::fromJson),
	LEGACY_UNICODE("legacy_unicode", UnicodeTextureFont.Loader::fromJson);

	private static final Map<String, FontType> REGISTRY = SystemUtil.consume(Maps.<String, FontType>newHashMap(), hashMap -> {
		for (FontType fontType : values()) {
			hashMap.put(fontType.id, fontType);
		}
	});
	private final String id;
	private final Function<JsonObject, FontLoader> loaderFactory;

	private FontType(String string2, Function<JsonObject, FontLoader> function) {
		this.id = string2;
		this.loaderFactory = function;
	}

	public static FontType byId(String string) {
		FontType fontType = (FontType)REGISTRY.get(string);
		if (fontType == null) {
			throw new IllegalArgumentException("Invalid type: " + string);
		} else {
			return fontType;
		}
	}

	public FontLoader createLoader(JsonObject jsonObject) {
		return (FontLoader)this.loaderFactory.apply(jsonObject);
	}
}
