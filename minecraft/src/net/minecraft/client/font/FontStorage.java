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
	private static final EmptyGlyphRenderer field_2250 = new EmptyGlyphRenderer();
	private static final Glyph SPACE = () -> 4.0F;
	private static final Random RANDOM = new Random();
	private final TextureManager field_2248;
	private final Identifier field_2246;
	private GlyphRenderer field_2256;
	private final List<Font> fonts = Lists.<Font>newArrayList();
	private final Char2ObjectMap<GlyphRenderer> glyphRendererCache = new Char2ObjectOpenHashMap<>();
	private final Char2ObjectMap<Glyph> glyphCache = new Char2ObjectOpenHashMap<>();
	private final Int2ObjectMap<CharList> charactersByWidth = new Int2ObjectOpenHashMap<>();
	private final List<GlyphAtlasTexture> glyphAtlases = Lists.<GlyphAtlasTexture>newArrayList();

	public FontStorage(TextureManager textureManager, Identifier identifier) {
		this.field_2248 = textureManager;
		this.field_2246 = identifier;
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
		this.field_2256 = this.method_2012(BlankGlyph.INSTANCE);
		Set<Font> set = Sets.<Font>newHashSet();

		for (char c = 0; c < '\uffff'; c++) {
			for (Font font2 : list) {
				Glyph glyph = (Glyph)(c == ' ' ? SPACE : font2.method_2040(c));
				if (glyph != null) {
					set.add(font2);
					if (glyph != BlankGlyph.INSTANCE) {
						this.charactersByWidth.computeIfAbsent(MathHelper.ceil(glyph.getAdvance(false)), i -> new CharArrayList()).add(c);
					}
					break;
				}
			}
		}

		list.stream().filter(set::contains).forEach(this.fonts::add);
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
			RenderableGlyph renderableGlyph = font.method_2040(c);
			if (renderableGlyph != null) {
				return renderableGlyph;
			}
		}

		return BlankGlyph.INSTANCE;
	}

	public GlyphRenderer method_2014(char c) {
		return this.glyphRendererCache.computeIfAbsent(c, i -> (GlyphRenderer)(i == 32 ? field_2250 : this.method_2012(this.getRenderableGlyph((char)i))));
	}

	private GlyphRenderer method_2012(RenderableGlyph renderableGlyph) {
		for (GlyphAtlasTexture glyphAtlasTexture : this.glyphAtlases) {
			GlyphRenderer glyphRenderer = glyphAtlasTexture.method_2022(renderableGlyph);
			if (glyphRenderer != null) {
				return glyphRenderer;
			}
		}

		GlyphAtlasTexture glyphAtlasTexture2 = new GlyphAtlasTexture(
			new Identifier(this.field_2246.getNamespace(), this.field_2246.getPath() + "/" + this.glyphAtlases.size()), renderableGlyph.hasColor()
		);
		this.glyphAtlases.add(glyphAtlasTexture2);
		this.field_2248.method_4616(glyphAtlasTexture2.method_2023(), glyphAtlasTexture2);
		GlyphRenderer glyphRenderer2 = glyphAtlasTexture2.method_2022(renderableGlyph);
		return glyphRenderer2 == null ? this.field_2256 : glyphRenderer2;
	}

	public GlyphRenderer method_2013(Glyph glyph) {
		CharList charList = this.charactersByWidth.get(MathHelper.ceil(glyph.getAdvance(false)));
		return charList != null && !charList.isEmpty() ? this.method_2014(charList.get(RANDOM.nextInt(charList.size()))) : this.field_2256;
	}
}
