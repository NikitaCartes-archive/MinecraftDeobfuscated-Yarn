/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.TrueTypeFont;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Struct;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class TrueTypeFontLoader
implements FontLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Identifier filename;
    private final float size;
    private final float oversample;
    private final float shiftX;
    private final float shiftY;
    private final String excludedCharacters;

    public TrueTypeFontLoader(Identifier filename, float size, float oversample, float shiftX, float shiftY, String excludedCharacters) {
        this.filename = filename;
        this.size = size;
        this.oversample = oversample;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
        this.excludedCharacters = excludedCharacters;
    }

    public static FontLoader fromJson(JsonObject json) {
        float f = 0.0f;
        float g = 0.0f;
        if (json.has("shift")) {
            JsonArray jsonArray = json.getAsJsonArray("shift");
            if (jsonArray.size() != 2) {
                throw new JsonParseException("Expected 2 elements in 'shift', found " + jsonArray.size());
            }
            f = JsonHelper.asFloat(jsonArray.get(0), "shift[0]");
            g = JsonHelper.asFloat(jsonArray.get(1), "shift[1]");
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (json.has("skip")) {
            JsonElement jsonElement = json.get("skip");
            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray2 = JsonHelper.asArray(jsonElement, "skip");
                for (int i = 0; i < jsonArray2.size(); ++i) {
                    stringBuilder.append(JsonHelper.asString(jsonArray2.get(i), "skip[" + i + "]"));
                }
            } else {
                stringBuilder.append(JsonHelper.asString(jsonElement, "skip"));
            }
        }
        return new TrueTypeFontLoader(new Identifier(JsonHelper.getString(json, "file")), JsonHelper.getFloat(json, "size", 11.0f), JsonHelper.getFloat(json, "oversample", 1.0f), f, g, stringBuilder.toString());
    }

    @Override
    @Nullable
    public Font load(ResourceManager manager) {
        TrueTypeFont trueTypeFont;
        block10: {
            Struct sTBTTFontinfo = null;
            ByteBuffer byteBuffer = null;
            InputStream inputStream = manager.open(new Identifier(this.filename.getNamespace(), "font/" + this.filename.getPath()));
            try {
                LOGGER.debug("Loading font {}", (Object)this.filename);
                sTBTTFontinfo = STBTTFontinfo.malloc();
                byteBuffer = TextureUtil.readResource(inputStream);
                byteBuffer.flip();
                LOGGER.debug("Reading font {}", (Object)this.filename);
                if (!STBTruetype.stbtt_InitFont((STBTTFontinfo)sTBTTFontinfo, byteBuffer)) {
                    throw new IOException("Invalid ttf");
                }
                trueTypeFont = new TrueTypeFont(byteBuffer, (STBTTFontinfo)sTBTTFontinfo, this.size, this.oversample, this.shiftX, this.shiftY, this.excludedCharacters);
                if (inputStream == null) break block10;
            } catch (Throwable throwable) {
                try {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                } catch (Exception exception) {
                    LOGGER.error("Couldn't load truetype font {}", (Object)this.filename, (Object)exception);
                    if (sTBTTFontinfo != null) {
                        sTBTTFontinfo.free();
                    }
                    MemoryUtil.memFree(byteBuffer);
                    return null;
                }
            }
            inputStream.close();
        }
        return trueTypeFont;
    }
}

