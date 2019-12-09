/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.TextureFont;
import net.minecraft.client.font.TrueTypeFontLoader;
import net.minecraft.client.font.UnicodeTextureFont;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public enum FontType {
    BITMAP("bitmap", TextureFont.Loader::fromJson),
    TTF("ttf", TrueTypeFontLoader::fromJson),
    LEGACY_UNICODE("legacy_unicode", UnicodeTextureFont.Loader::fromJson);

    private static final Map<String, FontType> REGISTRY;
    private final String id;
    private final Function<JsonObject, FontLoader> loaderFactory;

    private FontType(String id, Function<JsonObject, FontLoader> factory) {
        this.id = id;
        this.loaderFactory = factory;
    }

    public static FontType byId(String id) {
        FontType fontType = REGISTRY.get(id);
        if (fontType == null) {
            throw new IllegalArgumentException("Invalid type: " + id);
        }
        return fontType;
    }

    public FontLoader createLoader(JsonObject json) {
        return this.loaderFactory.apply(json);
    }

    static {
        REGISTRY = Util.make(Maps.newHashMap(), hashMap -> {
            for (FontType fontType : FontType.values()) {
                hashMap.put(fontType.id, fontType);
            }
        });
    }
}

