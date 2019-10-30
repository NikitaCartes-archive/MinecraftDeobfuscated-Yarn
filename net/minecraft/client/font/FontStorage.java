/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class FontStorage
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final EmptyGlyphRenderer EMPTY_GLYPH_RENDERER = new EmptyGlyphRenderer();
    private static final Glyph SPACE = () -> 4.0f;
    private static final Random RANDOM = new Random();
    private final TextureManager textureManager;
    private final Identifier id;
    private GlyphRenderer blankGlyphRenderer;
    private GlyphRenderer whiteRectangleGlyphRenderer;
    private final List<Font> fonts = Lists.newArrayList();
    private final Char2ObjectMap<GlyphRenderer> glyphRendererCache = new Char2ObjectOpenHashMap<GlyphRenderer>();
    private final Char2ObjectMap<Glyph> glyphCache = new Char2ObjectOpenHashMap<Glyph>();
    private final Int2ObjectMap<CharList> charactersByWidth = new Int2ObjectOpenHashMap<CharList>();
    private final List<GlyphAtlasTexture> glyphAtlases = Lists.newArrayList();

    public FontStorage(TextureManager textureManager, Identifier identifier) {
        this.textureManager = textureManager;
        this.id = identifier;
    }

    public void setFonts(List<Font> list) {
        for (Font font : this.fonts) {
            font.close();
        }
        this.fonts.clear();
        this.closeGlyphAtlases();
        this.glyphAtlases.clear();
        this.glyphRendererCache.clear();
        this.glyphCache.clear();
        this.charactersByWidth.clear();
        this.blankGlyphRenderer = this.getGlyphRenderer(BlankGlyph.INSTANCE);
        this.whiteRectangleGlyphRenderer = this.getGlyphRenderer(WhiteRectangleGlyph.INSTANCE);
        HashSet<Font> set = Sets.newHashSet();
        block1: for (char c = '\u0000'; c < '\uffff'; c = (char)((char)(c + 1))) {
            for (Font font2 : list) {
                Glyph glyph = c == ' ' ? SPACE : font2.getGlyph(c);
                if (glyph == null) continue;
                set.add(font2);
                if (glyph == BlankGlyph.INSTANCE) continue block1;
                this.charactersByWidth.computeIfAbsent(MathHelper.ceil(glyph.getAdvance(false)), i -> new CharArrayList()).add(c);
                continue block1;
            }
        }
        list.stream().filter(set::contains).forEach(this.fonts::add);
    }

    @Override
    public void close() {
        this.closeGlyphAtlases();
    }

    public void closeGlyphAtlases() {
        for (GlyphAtlasTexture glyphAtlasTexture : this.glyphAtlases) {
            glyphAtlasTexture.close();
        }
    }

    public Glyph getGlyph(char c) {
        return this.glyphCache.computeIfAbsent(c, i -> i == 32 ? SPACE : this.getRenderableGlyph((char)i));
    }

    private RenderableGlyph getRenderableGlyph(char c) {
        for (Font font : this.fonts) {
            RenderableGlyph renderableGlyph = font.getGlyph(c);
            if (renderableGlyph == null) continue;
            return renderableGlyph;
        }
        return BlankGlyph.INSTANCE;
    }

    public GlyphRenderer getGlyphRenderer(char c) {
        return this.glyphRendererCache.computeIfAbsent(c, i -> i == 32 ? EMPTY_GLYPH_RENDERER : this.getGlyphRenderer(this.getRenderableGlyph((char)i)));
    }

    private GlyphRenderer getGlyphRenderer(RenderableGlyph renderableGlyph) {
        for (GlyphAtlasTexture glyphAtlasTexture : this.glyphAtlases) {
            GlyphRenderer glyphRenderer = glyphAtlasTexture.getGlyphRenderer(renderableGlyph);
            if (glyphRenderer == null) continue;
            return glyphRenderer;
        }
        GlyphAtlasTexture glyphAtlasTexture2 = new GlyphAtlasTexture(new Identifier(this.id.getNamespace(), this.id.getPath() + "/" + this.glyphAtlases.size()), renderableGlyph.hasColor());
        this.glyphAtlases.add(glyphAtlasTexture2);
        this.textureManager.registerTexture(glyphAtlasTexture2.getId(), glyphAtlasTexture2);
        GlyphRenderer glyphRenderer2 = glyphAtlasTexture2.getGlyphRenderer(renderableGlyph);
        return glyphRenderer2 == null ? this.blankGlyphRenderer : glyphRenderer2;
    }

    public GlyphRenderer getObfuscatedGlyphRenderer(Glyph glyph) {
        CharList charList = (CharList)this.charactersByWidth.get(MathHelper.ceil(glyph.getAdvance(false)));
        if (charList != null && !charList.isEmpty()) {
            return this.getGlyphRenderer(charList.get(RANDOM.nextInt(charList.size())).charValue());
        }
        return this.blankGlyphRenderer;
    }

    public GlyphRenderer getRectangleRenderer() {
        return this.whiteRectangleGlyphRenderer;
    }
}

