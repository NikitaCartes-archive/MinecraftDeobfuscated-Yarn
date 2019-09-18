/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TextureFont
implements Font {
    private static final Logger LOGGER = LogManager.getLogger();
    private final NativeImage image;
    private final Char2ObjectMap<TextureFontGlyph> characterToGlyphMap;

    public TextureFont(NativeImage nativeImage, Char2ObjectMap<TextureFontGlyph> char2ObjectMap) {
        this.image = nativeImage;
        this.characterToGlyphMap = char2ObjectMap;
    }

    @Override
    public void close() {
        this.image.close();
    }

    @Override
    @Nullable
    public RenderableGlyph getGlyph(char c) {
        return (RenderableGlyph)this.characterToGlyphMap.get(c);
    }

    @Environment(value=EnvType.CLIENT)
    static final class TextureFontGlyph
    implements RenderableGlyph {
        private final float scaleFactor;
        private final NativeImage image;
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private final int advance;
        private final int ascent;

        private TextureFontGlyph(float f, NativeImage nativeImage, int i, int j, int k, int l, int m, int n) {
            this.scaleFactor = f;
            this.image = nativeImage;
            this.x = i;
            this.y = j;
            this.width = k;
            this.height = l;
            this.advance = m;
            this.ascent = n;
        }

        @Override
        public float getOversample() {
            return 1.0f / this.scaleFactor;
        }

        @Override
        public int getWidth() {
            return this.width;
        }

        @Override
        public int getHeight() {
            return this.height;
        }

        @Override
        public float getAdvance() {
            return this.advance;
        }

        @Override
        public float getAscent() {
            return RenderableGlyph.super.getAscent() + 7.0f - (float)this.ascent;
        }

        @Override
        public void upload(int i, int j) {
            this.image.upload(0, i, j, this.x, this.y, this.width, this.height, false, false);
        }

        @Override
        public boolean hasColor() {
            return this.image.getFormat().getChannelCount() > 1;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Loader
    implements FontLoader {
        private final Identifier filename;
        private final List<String> chars;
        private final int height;
        private final int ascent;

        public Loader(Identifier identifier, int i, int j, List<String> list) {
            this.filename = new Identifier(identifier.getNamespace(), "textures/" + identifier.getPath());
            this.chars = list;
            this.height = i;
            this.ascent = j;
        }

        public static Loader fromJson(JsonObject jsonObject) {
            int i = JsonHelper.getInt(jsonObject, "height", 8);
            int j = JsonHelper.getInt(jsonObject, "ascent");
            if (j > i) {
                throw new JsonParseException("Ascent " + j + " higher than height " + i);
            }
            ArrayList<String> list = Lists.newArrayList();
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "chars");
            for (int k = 0; k < jsonArray.size(); ++k) {
                int m;
                int l;
                String string = JsonHelper.asString(jsonArray.get(k), "chars[" + k + "]");
                if (k > 0 && (l = string.length()) != (m = ((String)list.get(0)).length())) {
                    throw new JsonParseException("Elements of chars have to be the same length (found: " + l + ", expected: " + m + "), pad with space or \\u0000");
                }
                list.add(string);
            }
            if (list.isEmpty() || ((String)list.get(0)).isEmpty()) {
                throw new JsonParseException("Expected to find data in chars, found none.");
            }
            return new Loader(new Identifier(JsonHelper.getString(jsonObject, "file")), i, j, list);
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        @Nullable
        public Font load(ResourceManager resourceManager) {
            try (Resource resource = resourceManager.getResource(this.filename);){
                NativeImage nativeImage = NativeImage.read(NativeImage.Format.RGBA, resource.getInputStream());
                int i = nativeImage.getWidth();
                int j = nativeImage.getHeight();
                int k = i / this.chars.get(0).length();
                int l = j / this.chars.size();
                float f = (float)this.height / (float)l;
                Char2ObjectOpenHashMap<TextureFontGlyph> char2ObjectMap = new Char2ObjectOpenHashMap<TextureFontGlyph>();
                int m = 0;
                while (true) {
                    String string;
                    if (m < this.chars.size()) {
                        string = this.chars.get(m);
                    } else {
                        TextureFont textureFont = new TextureFont(nativeImage, char2ObjectMap);
                        return textureFont;
                    }
                    for (int n = 0; n < string.length(); ++n) {
                        int o;
                        TextureFontGlyph textureFontGlyph;
                        char c = string.charAt(n);
                        if (c == '\u0000' || c == ' ' || (textureFontGlyph = char2ObjectMap.put(c, new TextureFontGlyph(f, nativeImage, n * k, m * l, k, l, (int)(0.5 + (double)((float)(o = this.findCharacterStartX(nativeImage, k, l, n, m)) * f)) + 1, this.ascent))) == null) continue;
                        LOGGER.warn("Codepoint '{}' declared multiple times in {}", (Object)Integer.toHexString(c), (Object)this.filename);
                    }
                    ++m;
                }
            } catch (IOException iOException) {
                throw new RuntimeException(iOException.getMessage());
            }
        }

        private int findCharacterStartX(NativeImage nativeImage, int i, int j, int k, int l) {
            int m;
            for (m = i - 1; m >= 0; --m) {
                int n = k * i + m;
                for (int o = 0; o < j; ++o) {
                    int p = l * j + o;
                    if (nativeImage.getPixelOpacity(n, p) == 0) continue;
                    return m + 1;
                }
            }
            return m + 1;
        }
    }
}

