package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class FontStorage implements AutoCloseable {
	private static final EmptyGlyphRenderer EMPTY_GLYPH_RENDERER = new EmptyGlyphRenderer();
	private static final Glyph SPACE = () -> 4.0F;
	private static final Glyph ZERO_WIDTH_NON_JOINER = () -> 0.0F;
	private static final int ZERO_WIDTH_NON_JOINER_CODE_POINT = 8204;
	private static final Random RANDOM = new Random();
	private final TextureManager textureManager;
	private final Identifier id;
	private GlyphRenderer blankGlyphRenderer;
	private GlyphRenderer whiteRectangleGlyphRenderer;
	private final List<Font> fonts = Lists.<Font>newArrayList();
	private final Int2ObjectMap<GlyphRenderer> glyphRendererCache = new Int2ObjectOpenHashMap<>();
	private final Int2ObjectMap<Glyph> glyphCache = new Int2ObjectOpenHashMap<>();
	private final Int2ObjectMap<IntList> charactersByWidth = new Int2ObjectOpenHashMap<>();
	private final List<GlyphAtlasTexture> glyphAtlases = Lists.<GlyphAtlasTexture>newArrayList();

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
		IntSet intSet = new IntOpenHashSet();

		for (Font font : fonts) {
			intSet.addAll(font.getProvidedGlyphs());
		}

		Set<Font> set = Sets.<Font>newHashSet();
		intSet.forEach(
			codePoint -> {
				for (Font fontx : fonts) {
					Glyph glyph = this.getEmptyGlyph(codePoint);
					if (glyph == null) {
						glyph = fontx.getGlyph(codePoint);
					}

					if (glyph != null) {
						set.add(fontx);
						if (glyph != BlankGlyph.INSTANCE) {
							this.charactersByWidth
								.computeIfAbsent(MathHelper.ceil(glyph.getAdvance(false)), (Int2ObjectFunction<? extends IntList>)(i -> new IntArrayList()))
								.add(codePoint);
						}
						break;
					}
				}
			}
		);
		fonts.stream().filter(set::contains).forEach(this.fonts::add);
	}

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

	public Glyph getGlyph(int codePoint) {
		return this.glyphCache.computeIfAbsent(codePoint, (Int2ObjectFunction<? extends Glyph>)(codePointx -> {
			Glyph glyph = this.getEmptyGlyph(codePointx);
			return (Glyph)(glyph == null ? this.getRenderableGlyph(codePointx) : glyph);
		}));
	}

	private RenderableGlyph getRenderableGlyph(int codePoint) {
		for (Font font : this.fonts) {
			RenderableGlyph renderableGlyph = font.getGlyph(codePoint);
			if (renderableGlyph != null) {
				return renderableGlyph;
			}
		}

		return BlankGlyph.INSTANCE;
	}

	public GlyphRenderer getGlyphRenderer(int codePoint) {
		return this.glyphRendererCache.computeIfAbsent(codePoint, (Int2ObjectFunction<? extends GlyphRenderer>)(codePointx -> {
			return (GlyphRenderer)(switch (codePointx) {
				case 32, 8204 -> EMPTY_GLYPH_RENDERER;
				default -> this.getGlyphRenderer(this.getRenderableGlyph(codePointx));
			});
		}));
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
		IntList intList = this.charactersByWidth.get(MathHelper.ceil(glyph.getAdvance(false)));
		return intList != null && !intList.isEmpty() ? this.getGlyphRenderer(intList.getInt(RANDOM.nextInt(intList.size()))) : this.blankGlyphRenderer;
	}

	public GlyphRenderer getRectangleRenderer() {
		return this.whiteRectangleGlyphRenderer;
	}
}
