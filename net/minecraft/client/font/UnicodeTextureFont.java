/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
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
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;
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
    private static final int field_40410 = 65536;
    private final byte[] sizes;
    private final FontImage[] fontImages = new FontImage[256];

    public UnicodeTextureFont(ResourceManager resourceManager, byte[] sizes, String template) {
        this.sizes = sizes;
        HashSet<Identifier> set = new HashSet<Identifier>();
        for (int i = 0; i < 256; ++i) {
            int j = i * 256;
            set.add(UnicodeTextureFont.getImageId(template, j));
        }
        String string = UnicodeTextureFont.getCommonPath(set);
        HashMap map = new HashMap();
        resourceManager.findResources(string, set::contains).forEach((id, resource) -> map.put(id, CompletableFuture.supplyAsync(() -> {
            NativeImage nativeImage;
            block8: {
                InputStream inputStream = resource.getInputStream();
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
                        LOGGER.error("Failed to read resource {} from pack {}", id, (Object)resource.getResourcePackName());
                        return null;
                    }
                }
                inputStream.close();
            }
            return nativeImage;
        }, Util.getMainWorkerExecutor())));
        ArrayList<CompletionStage> list = new ArrayList<CompletionStage>(256);
        for (int k = 0; k < 256; ++k) {
            int l = k * 256;
            int m = k;
            Identifier identifier = UnicodeTextureFont.getImageId(template, l);
            CompletableFuture completableFuture = (CompletableFuture)map.get(identifier);
            if (completableFuture == null) continue;
            list.add(completableFuture.thenAcceptAsync(image -> {
                if (image == null) {
                    return;
                }
                if (image.getWidth() == 256 && image.getHeight() == 256) {
                    for (int k = 0; k < 256; ++k) {
                        byte b = sizes[l + k];
                        if (b == 0 || UnicodeTextureFont.getStart(b) <= UnicodeTextureFont.getEnd(b)) continue;
                        bs[i + k] = 0;
                    }
                    this.fontImages[j] = new FontImage(sizes, (NativeImage)image);
                } else {
                    image.close();
                    Arrays.fill(sizes, l, l + 256, (byte)0);
                }
            }, (Executor)Util.getMainWorkerExecutor()));
        }
        CompletableFuture.allOf((CompletableFuture[])list.toArray(CompletableFuture[]::new)).join();
    }

    private static String getCommonPath(Set<Identifier> ids) {
        String string = StringUtils.getCommonPrefix((String[])ids.stream().map(Identifier::getPath).toArray(String[]::new));
        int i = string.lastIndexOf("/");
        if (i == -1) {
            return "";
        }
        return string.substring(0, i);
    }

    @Override
    public void close() {
        for (FontImage fontImage : this.fontImages) {
            if (fontImage == null) continue;
            fontImage.close();
        }
    }

    private static Identifier getImageId(String template, int codePoint) {
        String string = String.format(Locale.ROOT, "%02x", codePoint / 256);
        Identifier identifier = new Identifier(String.format(Locale.ROOT, template, string));
        return identifier.withPrefixedPath("textures/");
    }

    @Override
    @Nullable
    public Glyph getGlyph(int codePoint) {
        if (codePoint < 0 || codePoint >= this.sizes.length) {
            return null;
        }
        int i = codePoint / 256;
        FontImage fontImage = this.fontImages[i];
        return fontImage != null ? fontImage.getGlyph(codePoint) : null;
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

    static int getStart(byte size) {
        return size >> 4 & 0xF;
    }

    static int getEnd(byte size) {
        return (size & 0xF) + 1;
    }

    @Environment(value=EnvType.CLIENT)
    static class FontImage
    implements AutoCloseable {
        private final byte[] sizes;
        private final NativeImage image;

        FontImage(byte[] sizes, NativeImage image) {
            this.sizes = sizes;
            this.image = image;
        }

        @Override
        public void close() {
            this.image.close();
        }

        @Nullable
        public Glyph getGlyph(int codePoint) {
            byte b = this.sizes[codePoint];
            if (b != 0) {
                int i = UnicodeTextureFont.getStart(b);
                return new UnicodeTextureGlyph(codePoint % 16 * 16 + i, (codePoint & 0xFF) / 16 * 16, UnicodeTextureFont.getEnd(b) - i, 16, this.image);
            }
            return null;
        }
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
                String.format(Locale.ROOT, string, "");
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

