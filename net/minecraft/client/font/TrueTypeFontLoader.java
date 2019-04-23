/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.TrueTypeFont;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TrueTypeFontLoader
implements FontLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Identifier filename;
    private final float size;
    private final float oversample;
    private final float shiftX;
    private final float shiftY;
    private final String excludedCharacters;

    public TrueTypeFontLoader(Identifier identifier, float f, float g, float h, float i, String string) {
        this.filename = identifier;
        this.size = f;
        this.oversample = g;
        this.shiftX = h;
        this.shiftY = i;
        this.excludedCharacters = string;
    }

    public static FontLoader fromJson(JsonObject jsonObject) {
        float f = 0.0f;
        float g = 0.0f;
        if (jsonObject.has("shift")) {
            JsonArray jsonArray = jsonObject.getAsJsonArray("shift");
            if (jsonArray.size() != 2) {
                throw new JsonParseException("Expected 2 elements in 'shift', found " + jsonArray.size());
            }
            f = JsonHelper.asFloat(jsonArray.get(0), "shift[0]");
            g = JsonHelper.asFloat(jsonArray.get(1), "shift[1]");
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (jsonObject.has("skip")) {
            JsonElement jsonElement = jsonObject.get("skip");
            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray2 = JsonHelper.asArray(jsonElement, "skip");
                for (int i = 0; i < jsonArray2.size(); ++i) {
                    stringBuilder.append(JsonHelper.asString(jsonArray2.get(i), "skip[" + i + "]"));
                }
            } else {
                stringBuilder.append(JsonHelper.asString(jsonElement, "skip"));
            }
        }
        return new TrueTypeFontLoader(new Identifier(JsonHelper.getString(jsonObject, "file")), JsonHelper.getFloat(jsonObject, "size", 11.0f), JsonHelper.getFloat(jsonObject, "oversample", 1.0f), f, g, stringBuilder.toString());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    @Nullable
    public Font load(ResourceManager resourceManager) {
        try (Resource resource = resourceManager.getResource(new Identifier(this.filename.getNamespace(), "font/" + this.filename.getPath()));){
            LOGGER.info("Loading font");
            ByteBuffer byteBuffer = TextureUtil.readResource(resource.getInputStream());
            byteBuffer.flip();
            LOGGER.info("Reading font");
            TrueTypeFont trueTypeFont = new TrueTypeFont(TrueTypeFont.getSTBTTFontInfo(byteBuffer), this.size, this.oversample, this.shiftX, this.shiftY, this.excludedCharacters);
            return trueTypeFont;
        } catch (IOException iOException) {
            LOGGER.error("Couldn't load truetype font {}", (Object)this.filename, (Object)iOException);
            return null;
        }
    }
}

