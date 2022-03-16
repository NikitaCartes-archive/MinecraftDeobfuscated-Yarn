/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class BitmapFont
implements Font {
    static final Logger LOGGER = LogUtils.getLogger();
    private final NativeImage image;
    private final Int2ObjectMap<BitmapFontGlyph> glyphs;

    BitmapFont(NativeImage image, Int2ObjectMap<BitmapFontGlyph> glyphs) {
        this.image = image;
        this.glyphs = glyphs;
    }

    @Override
    public void close() {
        this.image.close();
    }

    @Override
    @Nullable
    public Glyph getGlyph(int codePoint) {
        return (Glyph)this.glyphs.get(codePoint);
    }

    @Override
    public IntSet getProvidedGlyphs() {
        return IntSets.unmodifiable(this.glyphs.keySet());
    }

    @Environment(value=EnvType.CLIENT)
    record BitmapFontGlyph(float scaleFactor, NativeImage image, int x, int y, int width, int height, int advance, int ascent) implements Glyph
    {
        @Override
        public float getAdvance() {
            return this.advance;
        }

        @Override
        public GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> function) {
            return function.apply(new RenderableGlyph(){

                @Override
                public float getOversample() {
                    return 1.0f / scaleFactor;
                }

                @Override
                public int getWidth() {
                    return width;
                }

                @Override
                public int getHeight() {
                    return height;
                }

                @Override
                public float getAscent() {
                    return RenderableGlyph.super.getAscent() + 7.0f - (float)ascent;
                }

                @Override
                public void upload(int x, int y) {
                    image.upload(0, x, y, x, y, width, height, false, false);
                }

                @Override
                public boolean hasColor() {
                    return image.getFormat().getChannelCount() > 1;
                }
            });
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Loader
    implements FontLoader {
        private final Identifier filename;
        private final List<int[]> chars;
        private final int height;
        private final int ascent;

        public Loader(Identifier id, int height, int ascent, List<int[]> chars) {
            this.filename = new Identifier(id.getNamespace(), "textures/" + id.getPath());
            this.chars = chars;
            this.height = height;
            this.ascent = ascent;
        }

        public static Loader fromJson(JsonObject json) {
            int i = JsonHelper.getInt(json, "height", 8);
            int j = JsonHelper.getInt(json, "ascent");
            if (j > i) {
                throw new JsonParseException("Ascent " + j + " higher than height " + i);
            }
            ArrayList<int[]> list = Lists.newArrayList();
            JsonArray jsonArray = JsonHelper.getArray(json, "chars");
            for (int k = 0; k < jsonArray.size(); ++k) {
                int l;
                String string = JsonHelper.asString(jsonArray.get(k), "chars[" + k + "]");
                int[] is = string.codePoints().toArray();
                if (k > 0 && is.length != (l = ((int[])list.get(0)).length)) {
                    throw new JsonParseException("Elements of chars have to be the same length (found: " + is.length + ", expected: " + l + "), pad with space or \\u0000");
                }
                list.add(is);
            }
            if (list.isEmpty() || ((int[])list.get(0)).length == 0) {
                throw new JsonParseException("Expected to find data in chars, found none.");
            }
            return new Loader(new Identifier(JsonHelper.getString(json, "file")), i, j, list);
        }

        @Override
        @Nullable
        public Font load(ResourceManager manager) {
            BitmapFont bitmapFont;
            block10: {
                Resource resource = manager.getResource(this.filename);
                try {
                    NativeImage nativeImage = NativeImage.read(NativeImage.Format.RGBA, resource.getInputStream());
                    int i = nativeImage.getWidth();
                    int j = nativeImage.getHeight();
                    int k = i / this.chars.get(0).length;
                    int l = j / this.chars.size();
                    float f = (float)this.height / (float)l;
                    Int2ObjectOpenHashMap<BitmapFontGlyph> int2ObjectMap = new Int2ObjectOpenHashMap<BitmapFontGlyph>();
                    for (int m = 0; m < this.chars.size(); ++m) {
                        int n = 0;
                        for (int o : this.chars.get(m)) {
                            int q;
                            BitmapFontGlyph bitmapFontGlyph;
                            int p = n++;
                            if (o == 0 || o == 32 || (bitmapFontGlyph = int2ObjectMap.put(o, new BitmapFontGlyph(f, nativeImage, p * k, m * l, k, l, (int)(0.5 + (double)((float)(q = this.findCharacterStartX(nativeImage, k, l, p, m)) * f)) + 1, this.ascent))) == null) continue;
                            LOGGER.warn("Codepoint '{}' declared multiple times in {}", (Object)Integer.toHexString(o), (Object)this.filename);
                        }
                    }
                    bitmapFont = new BitmapFont(nativeImage, int2ObjectMap);
                    if (resource == null) break block10;
                } catch (Throwable throwable) {
                    try {
                        if (resource != null) {
                            try {
                                resource.close();
                            } catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    } catch (IOException iOException) {
                        throw new RuntimeException(iOException.getMessage());
                    }
                }
                resource.close();
            }
            return bitmapFont;
        }

        private int findCharacterStartX(NativeImage image, int characterWidth, int characterHeight, int charPosX, int charPosY) {
            int i;
            for (i = characterWidth - 1; i >= 0; --i) {
                int j = charPosX * characterWidth + i;
                for (int k = 0; k < characterHeight; ++k) {
                    int l = charPosY * characterHeight + k;
                    if (image.getOpacity(j, l) == 0) continue;
                    return i + 1;
                }
            }
            return i + 1;
        }
    }
}

