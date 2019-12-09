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

    public UnicodeTextureFont(ResourceManager resourceManager, byte[] sizes, String template) {
        this.resourceManager = resourceManager;
        this.sizes = sizes;
        this.template = template;
        for (int i = 0; i < 256; ++i) {
            char c = (char)(i * 256);
            Identifier identifier = this.getImageId(c);
            try (Resource resource = this.resourceManager.getResource(identifier);
                 NativeImage nativeImage = NativeImage.read(NativeImage.Format.RGBA, resource.getInputStream());){
                if (nativeImage.getWidth() == 256 && nativeImage.getHeight() == 256) {
                    for (int j = 0; j < 256; ++j) {
                        byte b = sizes[c + j];
                        if (b == 0 || UnicodeTextureFont.getStart(b) <= UnicodeTextureFont.getEnd(b)) continue;
                        sizes[c + j] = 0;
                    }
                    continue;
                }
            } catch (IOException iOException) {
                // empty catch block
            }
            Arrays.fill(sizes, (int)c, c + 256, (byte)0);
        }
    }

    @Override
    public void close() {
        this.images.values().forEach(NativeImage::close);
    }

    private Identifier getImageId(char character) {
        Identifier identifier = new Identifier(String.format(this.template, String.format("%02x", character / 256)));
        return new Identifier(identifier.getNamespace(), "textures/" + identifier.getPath());
    }

    @Override
    @Nullable
    public RenderableGlyph getGlyph(char character) {
        NativeImage nativeImage;
        byte b = this.sizes[character];
        if (b != 0 && (nativeImage = this.images.computeIfAbsent(this.getImageId(character), this::getGlyphImage)) != null) {
            int i = UnicodeTextureFont.getStart(b);
            return new UnicodeTextureGlyph(character % 16 * 16 + i, (character & 0xFF) / 16 * 16, UnicodeTextureFont.getEnd(b) - i, 16, nativeImage);
        }
        return null;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private NativeImage getGlyphImage(Identifier glyphId) {
        try (Resource resource = this.resourceManager.getResource(glyphId);){
            NativeImage nativeImage = NativeImage.read(NativeImage.Format.RGBA, resource.getInputStream());
            return nativeImage;
        } catch (IOException iOException) {
            LOGGER.error("Couldn't load texture {}", (Object)glyphId, (Object)iOException);
            return null;
        }
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

        private UnicodeTextureGlyph(int x, int y, int width, int height, NativeImage image) {
            this.width = width;
            this.height = height;
            this.unpackSkipPixels = x;
            this.unpackSkipRows = y;
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

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        @Nullable
        public Font load(ResourceManager manager) {
            try (Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(this.sizes);){
                byte[] bs = new byte[65536];
                resource.getInputStream().read(bs);
                UnicodeTextureFont unicodeTextureFont = new UnicodeTextureFont(manager, bs, this.template);
                return unicodeTextureFont;
            } catch (IOException iOException) {
                LOGGER.error("Cannot load {}, unicode glyphs will not render correctly", (Object)this.sizes);
                return null;
            }
        }
    }
}

