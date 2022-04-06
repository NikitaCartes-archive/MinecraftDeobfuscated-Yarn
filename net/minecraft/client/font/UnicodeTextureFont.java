/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class UnicodeTextureFont
implements Font {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_32232 = 256;
    private static final int field_32233 = 256;
    private static final int field_32234 = 256;
    private static final byte field_37905 = 0;
    private final ResourceManager resourceManager;
    private final byte[] sizes;
    private final String template;
    private final Map<Identifier, NativeImage> images = Maps.newHashMap();

    public UnicodeTextureFont(ResourceManager resourceManager, byte[] sizes, String template) {
        this.resourceManager = resourceManager;
        this.sizes = sizes;
        this.template = template;
        for (int i = 0; i < 256; ++i) {
            int j = i * 256;
            Identifier identifier = this.getImageId(j);
            try (InputStream inputStream = this.resourceManager.open(identifier);
                 NativeImage nativeImage = NativeImage.read(NativeImage.Format.RGBA, inputStream);){
                if (nativeImage.getWidth() == 256 && nativeImage.getHeight() == 256) {
                    for (int k = 0; k < 256; ++k) {
                        byte b = sizes[j + k];
                        if (b == 0 || UnicodeTextureFont.getStart(b) <= UnicodeTextureFont.getEnd(b)) continue;
                        sizes[j + k] = 0;
                    }
                    continue;
                }
            } catch (IOException iOException) {
                // empty catch block
            }
            Arrays.fill(sizes, j, j + 256, (byte)0);
        }
    }

    @Override
    public void close() {
        this.images.values().forEach(NativeImage::close);
    }

    private Identifier getImageId(int codePoint) {
        Identifier identifier = new Identifier(String.format(this.template, String.format("%02x", codePoint / 256)));
        return new Identifier(identifier.getNamespace(), "textures/" + identifier.getPath());
    }

    @Override
    @Nullable
    public Glyph getGlyph(int codePoint) {
        NativeImage nativeImage;
        if (codePoint < 0 || codePoint >= this.sizes.length) {
            return null;
        }
        byte b = this.sizes[codePoint];
        if (b != 0 && (nativeImage = this.images.computeIfAbsent(this.getImageId(codePoint), this::getGlyphImage)) != null) {
            int i = UnicodeTextureFont.getStart(b);
            return new UnicodeTextureGlyph(codePoint % 16 * 16 + i, (codePoint & 0xFF) / 16 * 16, UnicodeTextureFont.getEnd(b) - i, 16, nativeImage);
        }
        return null;
    }

    @Override
    public IntSet getProvidedGlyphs() {
        IntOpenHashSet intSet = new IntOpenHashSet();
        for (int i = 0; i < this.sizes.length; ++i) {
            if (this.sizes[i] == 0) continue;
            intSet.add(i);
        }
        return intSet;
    }

    @Nullable
    private NativeImage getGlyphImage(Identifier glyphId) {
        NativeImage nativeImage;
        block8: {
            InputStream inputStream = this.resourceManager.open(glyphId);
            try {
                nativeImage = NativeImage.read(NativeImage.Format.RGBA, inputStream);
                if (inputStream == null) break block8;
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
                } catch (IOException iOException) {
                    LOGGER.error("Couldn't load texture {}", (Object)glyphId, (Object)iOException);
                    return null;
                }
            }
            inputStream.close();
        }
        return nativeImage;
    }

    private static int getStart(byte size) {
        return size >> 4 & 0xF;
    }

    private static int getEnd(byte size) {
        return (size & 0xF) + 1;
    }

    @Environment(value=EnvType.CLIENT)
    record UnicodeTextureGlyph(int unpackSkipPixels, int unpackSkipRows, int width, int height, NativeImage image) implements Glyph
    {
        @Override
        public float getAdvance() {
            return this.width / 2 + 1;
        }

        @Override
        public float getShadowOffset() {
            return 0.5f;
        }

        @Override
        public float getBoldOffset() {
            return 0.5f;
        }

        @Override
        public GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> function) {
            return function.apply(new RenderableGlyph(){

                @Override
                public float getOversample() {
                    return 2.0f;
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
                public void upload(int x, int y) {
                    image.upload(0, x, y, unpackSkipPixels, unpackSkipRows, width, height, false, false);
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
        private final Identifier sizes;
        private final String template;

        public Loader(Identifier sizes, String template) {
            this.sizes = sizes;
            this.template = template;
        }

        public static FontLoader fromJson(JsonObject json) {
            return new Loader(new Identifier(JsonHelper.getString(json, "sizes")), Loader.getLegacyUnicodeTemplate(json));
        }

        private static String getLegacyUnicodeTemplate(JsonObject json) {
            String string = JsonHelper.getString(json, "template");
            try {
                String.format(string, "");
            } catch (IllegalFormatException illegalFormatException) {
                throw new JsonParseException("Invalid legacy unicode template supplied, expected single '%s': " + string);
            }
            return string;
        }

        @Override
        @Nullable
        public Font load(ResourceManager manager) {
            UnicodeTextureFont unicodeTextureFont;
            block8: {
                InputStream inputStream = MinecraftClient.getInstance().getResourceManager().open(this.sizes);
                try {
                    byte[] bs = inputStream.readNBytes(65536);
                    unicodeTextureFont = new UnicodeTextureFont(manager, bs, this.template);
                    if (inputStream == null) break block8;
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
                    } catch (IOException iOException) {
                        LOGGER.error("Cannot load {}, unicode glyphs will not render correctly", (Object)this.sizes);
                        return null;
                    }
                }
                inputStream.close();
            }
            return unicodeTextureFont;
        }
    }
}

