/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BlankGlyph;
import net.minecraft.client.font.EmptyGlyphRenderer;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphAtlasTexture;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.font.WhiteRectangleGlyph;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class FontStorage
implements AutoCloseable {
    private static final EmptyGlyphRenderer EMPTY_GLYPH_RENDERER = new EmptyGlyphRenderer();
    private static final Glyph SPACE = () -> 4.0f;
    private static final Glyph ZERO_WIDTH_NON_JOINER = () -> 0.0f;
    private static final int ZERO_WIDTH_NON_JOINER_CODE_POINT = 8204;
    private static final Random RANDOM = new Random();
    private final TextureManager textureManager;
    private final Identifier id;
    private GlyphRenderer blankGlyphRenderer;
    private GlyphRenderer whiteRectangleGlyphRenderer;
    private final List<Font> fonts = Lists.newArrayList();
    private final Int2ObjectMap<GlyphRenderer> glyphRendererCache = new Int2ObjectOpenHashMap<GlyphRenderer>();
    private final Int2ObjectMap<Glyph> glyphCache = new Int2ObjectOpenHashMap<Glyph>();
    private final Int2ObjectMap<IntList> charactersByWidth = new Int2ObjectOpenHashMap<IntList>();
    private final List<GlyphAtlasTexture> glyphAtlases = Lists.newArrayList();

    public FontStorage(TextureManager textureManager, Identifier id) {
        this.textureManager = textureManager;
        this.id = id;
    }

    public void setFonts(List<Font> fonts) {
        this.closeFonts();
        this.closeGlyphAtlases();
        this.glyphRendererCache.clear();
        this.glyphCache.clear();
        this.charactersByWidth.clear();
        this.blankGlyphRenderer = this.getGlyphRenderer(BlankGlyph.INSTANCE);
        this.whiteRectangleGlyphRenderer = this.getGlyphRenderer(WhiteRectangleGlyph.INSTANCE);
        IntOpenHashSet intSet = new IntOpenHashSet();
        for (Font font : fonts) {
            intSet.addAll(font.getProvidedGlyphs());
        }
        HashSet set = Sets.newHashSet();
        intSet.forEach(codePoint -> {
            for (Font font : fonts) {
                Glyph glyph = this.getEmptyGlyph(codePoint);
                if (glyph == null) {
                    glyph = font.getGlyph(codePoint);
                }
                if (glyph == null) continue;
                set.add(font);
                if (glyph == BlankGlyph.INSTANCE) break;
                this.charactersByWidth.computeIfAbsent(MathHelper.ceil(glyph.getAdvance(false)), i -> new IntArrayList()).add(codePoint);
                break;
            }
        });
        fonts.stream().filter(set::contains).forEach(this.fonts::add);
    }

    @Override
    public void close() {
        this.closeFonts();
        this.closeGlyphAtlases();
    }

    private void closeFonts() {
        for (Font font : this.fonts) {
            font.close();
        }
        this.fonts.clear();
    }

    private void closeGlyphAtlases() {
        for (GlyphAtlasTexture glyphAtlasTexture : this.glyphAtlases) {
            glyphAtlasTexture.close();
        }
        this.glyphAtlases.clear();
    }

    /**
     * {@return the pre-defined empty glyph for the code point, or
     * {@code null} if it is not defined}
     * 
     * @implNote Pre-defined empty glyphs include the space ({@code U+0020})
     * and zero-width non joiner ({@code U+200C}).
     */
    @Nullable
    private Glyph getEmptyGlyph(int codePoint) {
        return switch (codePoint) {
            case 32 -> SPACE;
            case 8204 -> ZERO_WIDTH_NON_JOINER;
            default -> null;
        };
    }

    public Glyph getGlyph(int codePoint2) {
        return this.glyphCache.computeIfAbsent(codePoint2, codePoint -> {
            Glyph glyph = this.getEmptyGlyph(codePoint);
            return glyph == null ? this.getRenderableGlyph(codePoint) : glyph;
        });
    }

    private RenderableGlyph getRenderableGlyph(int codePoint) {
        for (Font font : this.fonts) {
            RenderableGlyph renderableGlyph = font.getGlyph(codePoint);
            if (renderableGlyph == null) continue;
            return renderableGlyph;
        }
        return BlankGlyph.INSTANCE;
    }

    public GlyphRenderer getGlyphRenderer(int codePoint2) {
        return this.glyphRendererCache.computeIfAbsent(codePoint2, codePoint -> switch (codePoint) {
            case 32, 8204 -> EMPTY_GLYPH_RENDERER;
            default -> this.getGlyphRenderer(this.getRenderableGlyph(codePoint));
        });
    }

    private GlyphRenderer getGlyphRenderer(RenderableGlyph c) {
        for (GlyphAtlasTexture glyphAtlasTexture : this.glyphAtlases) {
            GlyphRenderer glyphRenderer = glyphAtlasTexture.getGlyphRenderer(c);
            if (glyphRenderer == null) continue;
            return glyphRenderer;
        }
        GlyphAtlasTexture glyphAtlasTexture2 = new GlyphAtlasTexture(new Identifier(this.id.getNamespace(), this.id.getPath() + "/" + this.glyphAtlases.size()), c.hasColor());
        this.glyphAtlases.add(glyphAtlasTexture2);
        this.textureManager.registerTexture(glyphAtlasTexture2.getId(), glyphAtlasTexture2);
        GlyphRenderer glyphRenderer2 = glyphAtlasTexture2.getGlyphRenderer(c);
        return glyphRenderer2 == null ? this.blankGlyphRenderer : glyphRenderer2;
    }

    public GlyphRenderer getObfuscatedGlyphRenderer(Glyph glyph) {
        IntList intList = (IntList)this.charactersByWidth.get(MathHelper.ceil(glyph.getAdvance(false)));
        if (intList != null && !intList.isEmpty()) {
            return this.getGlyphRenderer(intList.getInt(RANDOM.nextInt(intList.size())));
        }
        return this.blankGlyphRenderer;
    }

    public GlyphRenderer getRectangleRenderer() {
        return this.whiteRectangleGlyphRenderer;
    }
}

