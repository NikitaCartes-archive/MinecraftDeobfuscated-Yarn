/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
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
    private static final Logger LOGGER = LogManager.getLogger();
    private final ResourceManager resourceManager;
    private final byte[] sizes;
    private final String template;
    private final Map<Identifier, NativeImage> images = Maps.newHashMap();

    public UnicodeTextureFont(ResourceManager resourceManager, byte[] bs, String string) {
        this.resourceManager = resourceManager;
        this.sizes = bs;
        this.template = string;
        for (int i = 0; i < 256; ++i) {
            char c = (char)(i * 256);
            Identifier identifier = this.getGlyphId(c);
            try (Resource resource = this.resourceManager.getResource(identifier);
                 NativeImage nativeImage = NativeImage.fromInputStream(NativeImage.Format.RGBA, resource.getInputStream());){
                if (nativeImage.getWidth() == 256 && nativeImage.getHeight() == 256) {
                    for (int j = 0; j < 256; ++j) {
                        byte b = bs[c + j];
                        if (b == 0 || UnicodeTextureFont.method_2043(b) <= UnicodeTextureFont.method_2044(b)) continue;
                        bs[c + j] = 0;
                    }
                    continue;
                }
            } catch (IOException iOException) {
                // empty catch block
            }
            Arrays.fill(bs, (int)c, c + 256, (byte)0);
        }
    }

    @Override
    public void close() {
        this.images.values().forEach(NativeImage::close);
    }

    private Identifier getGlyphId(char c) {
        Identifier identifier = new Identifier(String.format(this.template, String.format("%02x", c / 256)));
        return new Identifier(identifier.getNamespace(), "textures/" + identifier.getPath());
    }

    @Override
    @Nullable
    public RenderableGlyph getGlyph(char c) {
        NativeImage nativeImage;
        byte b = this.sizes[c];
        if (b != 0 && (nativeImage = this.images.computeIfAbsent(this.getGlyphId(c), this::getGlyphImage)) != null) {
            int i = UnicodeTextureFont.method_2043(b);
            return new UnicodeTextureGlyph(c % 16 * 16 + i, (c & 0xFF) / 16 * 16, UnicodeTextureFont.method_2044(b) - i, 16, nativeImage);
        }
        return null;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private NativeImage getGlyphImage(Identifier identifier) {
        try (Resource resource = this.resourceManager.getResource(identifier);){
            NativeImage nativeImage = NativeImage.fromInputStream(NativeImage.Format.RGBA, resource.getInputStream());
            return nativeImage;
        } catch (IOException iOException) {
            LOGGER.error("Couldn't load texture {}", (Object)identifier, (Object)iOException);
            return null;
        }
    }

    private static int method_2043(byte b) {
        return b >> 4 & 0xF;
    }

    private static int method_2044(byte b) {
        return (b & 0xF) + 1;
    }

    @Environment(value=EnvType.CLIENT)
    static class UnicodeTextureGlyph
    implements RenderableGlyph {
        private final int width;
        private final int height;
        private final int unpackSkipPixels;
        private final int unpackSkipRows;
        private final NativeImage image;

        private UnicodeTextureGlyph(int i, int j, int k, int l, NativeImage nativeImage) {
            this.width = k;
            this.height = l;
            this.unpackSkipPixels = i;
            this.unpackSkipRows = j;
            this.image = nativeImage;
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
        public void upload(int i, int j) {
            this.image.upload(0, i, j, this.unpackSkipPixels, this.unpackSkipRows, this.width, this.height, false);
        }

        @Override
        public boolean hasColor() {
            return this.image.getFormat().getBytesPerPixel() > 1;
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

        public Loader(Identifier identifier, String string) {
            this.sizes = identifier;
            this.template = string;
        }

        public static FontLoader fromJson(JsonObject jsonObject) {
            return new Loader(new Identifier(JsonHelper.getString(jsonObject, "sizes")), JsonHelper.getString(jsonObject, "template"));
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        @Nullable
        public Font load(ResourceManager resourceManager) {
            try (Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(this.sizes);){
                byte[] bs = new byte[65536];
                resource.getInputStream().read(bs);
                UnicodeTextureFont unicodeTextureFont = new UnicodeTextureFont(resourceManager, bs, this.template);
                return unicodeTextureFont;
            } catch (IOException iOException) {
                LOGGER.error("Cannot load {}, unicode glyphs will not render correctly", (Object)this.sizes);
                return null;
            }
        }
    }
}

