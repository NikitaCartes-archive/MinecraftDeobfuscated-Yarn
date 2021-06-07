/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
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
public class UnicodeTextureFont
implements Font {
    static final Logger LOGGER = LogManager.getLogger();
    private static final int field_32232 = 256;
    private static final int field_32233 = 256;
    private static final int field_32234 = 256;
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
            try (Resource resource = this.resourceManager.getResource(identifier);
                 NativeImage nativeImage = NativeImage.read(NativeImage.Format.ABGR, resource.getInputStream());){
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
    public RenderableGlyph getGlyph(int codePoint) {
        NativeImage nativeImage;
        if (codePoint < 0 || codePoint > 65535) {
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
        for (int i = 0; i < 65535; ++i) {
            if (this.sizes[i] == 0) continue;
            intSet.add(i);
        }
        return intSet;
    }

    @Nullable
    private NativeImage getGlyphImage(Identifier glyphId) {
        NativeImage nativeImage;
        block8: {
            Resource resource = this.resourceManager.getResource(glyphId);
            try {
                nativeImage = NativeImage.read(NativeImage.Format.ABGR, resource.getInputStream());
                if (resource == null) break block8;
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
                    LOGGER.error("Couldn't load texture {}", (Object)glyphId, (Object)iOException);
                    return null;
                }
            }
            resource.close();
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
    static class UnicodeTextureGlyph
    implements RenderableGlyph {
        private final int width;
        private final int height;
        private final int unpackSkipPixels;
        private final int unpackSkipRows;
        private final NativeImage image;

        UnicodeTextureGlyph(int unpackSkipPixels, int unpackSkipRows, int width, int height, NativeImage image) {
            this.width = width;
            this.height = height;
            this.unpackSkipPixels = unpackSkipPixels;
            this.unpackSkipRows = unpackSkipRows;
            this.image = image;
        }

        @Override
        public float getOversample() {
            return 2.0f;
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
            return this.width / 2 + 1;
        }

        @Override
        public void upload(int x, int y) {
            this.image.upload(0, x, y, this.unpackSkipPixels, this.unpackSkipRows, this.width, this.height, false, false);
        }

        @Override
        public boolean hasColor() {
            return this.image.getFormat().getChannelCount() > 1;
        }

        @Override
        public float getShadowOffset() {
            return 0.5f;
        }

        @Override
        public float getBoldOffset() {
            return 0.5f;
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
            return new Loader(new Identifier(JsonHelper.getString(json, "sizes")), JsonHelper.getString(json, "template"));
        }

        @Override
        @Nullable
        public Font load(ResourceManager manager) {
            UnicodeTextureFont unicodeTextureFont;
            block8: {
                Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(this.sizes);
                try {
                    byte[] bs = new byte[65536];
                    resource.getInputStream().read(bs);
                    unicodeTextureFont = new UnicodeTextureFont(manager, bs, this.template);
                    if (resource == null) break block8;
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
                        LOGGER.error("Cannot load {}, unicode glyphs will not render correctly", (Object)this.sizes);
                        return null;
                    }
                }
                resource.close();
            }
            return unicodeTextureFont;
        }
    }
}

