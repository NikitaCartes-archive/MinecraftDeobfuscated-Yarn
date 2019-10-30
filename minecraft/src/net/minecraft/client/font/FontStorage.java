package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class FontStorage implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final EmptyGlyphRenderer EMPTY_GLYPH_RENDERER = new EmptyGlyphRenderer();
	private static final Glyph SPACE = () -> 4.0F;
	private static final Random RANDOM = new Random();
	private final TextureManager textureManager;
	private final Identifier id;
	private GlyphRenderer blankGlyphRenderer;
	private GlyphRenderer whiteRectangleGlyphRenderer;
	private final List<Font> fonts = Lists.<Font>newArrayList();
	private final Char2ObjectMap<GlyphRenderer> glyphRendererCache = new Char2ObjectOpenHashMap<>();
	private final Char2ObjectMap<Glyph> glyphCache = new Char2ObjectOpenHashMap<>();
	private final Int2ObjectMap<CharList> charactersByWidth = new Int2ObjectOpenHashMap<>();
	private final List<GlyphAtlasTexture> glyphAtlases = Lists.<GlyphAtlasTexture>newArrayList();

	public FontStorage(TextureManager textureManager, Identifier id) {
		this.textureManager = textureManager;
		this.id = id;
	}

	public void setFonts(List<Font> fonts) {
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
		Set<Font> set = Sets.<Font>newHashSet();

		for (char c = 0; c < '\uffff'; c++) {
			for (Font font2 : fonts) {
				Glyph glyph = (Glyph)(c == ' ' ? SPACE : font2.getGlyph(c));
				if (glyph != null) {
					set.add(font2);
					if (glyph != BlankGlyph.INSTANCE) {
						this.charactersByWidth.computeIfAbsent(MathHelper.ceil(glyph.getAdvance(false)), i -> new CharArrayList()).add(c);
					}
					break;
				}
			}
		}

		fonts.stream().filter(set::contains).forEach(this.fonts::add);
	}

	public void close() {
		this.closeGlyphAtlases();
	}

	public void closeGlyphAtlases() {
		for (GlyphAtlasTexture glyphAtlasTexture : this.glyphAtlases) {
			glyphAtlasTexture.close();
		}
	}

	public Glyph getGlyph(char c) {
		return this.glyphCache.computeIfAbsent(c, i -> (Glyph)(i == 32 ? SPACE : this.getRenderableGlyph((char)i)));
	}

	private RenderableGlyph getRenderableGlyph(char c) {
		for (Font font : this.fonts) {
			RenderableGlyph renderableGlyph = font.getGlyph(c);
			if (renderableGlyph != null) {
				return renderableGlyph;
			}
		}

		return BlankGlyph.INSTANCE;
	}

	public GlyphRenderer getGlyphRenderer(char c) {
		return this.glyphRendererCache
			.computeIfAbsent(c, i -> (GlyphRenderer)(i == 32 ? EMPTY_GLYPH_RENDERER : this.getGlyphRenderer(this.getRenderableGlyph((char)i))));
	}

	private GlyphRenderer getGlyphRenderer(RenderableGlyph c) {
		for (GlyphAtlasTexture glyphAtlasTexture : this.glyphAtlases) {
			GlyphRenderer glyphRenderer = glyphAtlasTexture.getGlyphRenderer(c);
			if (glyphRenderer != null) {
				return glyphRenderer;
			}
		}

		GlyphAtlasTexture glyphAtlasTexture2 = new GlyphAtlasTexture(
			new Identifier(this.id.getNamespace(), this.id.getPath() + "/" + this.glyphAtlases.size()), c.hasColor()
		);
		this.glyphAtlases.add(glyphAtlasTexture2);
		this.textureManager.registerTexture(glyphAtlasTexture2.getId(), glyphAtlasTexture2);
		GlyphRenderer glyphRenderer2 = glyphAtlasTexture2.getGlyphRenderer(c);
		return glyphRenderer2 == null ? this.blankGlyphRenderer : glyphRenderer2;
	}

	public GlyphRenderer getObfuscatedGlyphRenderer(Glyph glyph) {
		CharList charList = this.charactersByWidth.get(MathHelper.ceil(glyph.getAdvance(false)));
		return charList != null && !charList.isEmpty() ? this.getGlyphRenderer(charList.get(RANDOM.nextInt(charList.size()))) : this.blankGlyphRenderer;
	}

	public GlyphRenderer getRectangleRenderer() {
		return this.whiteRectangleGlyphRenderer;
	}
}
