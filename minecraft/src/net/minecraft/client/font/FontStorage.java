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
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class FontStorage implements AutoCloseable {
	private static final Random RANDOM = Random.create();
	private static final float field_39934 = 32.0F;
	private final TextureManager textureManager;
	private final Identifier id;
	private GlyphRenderer blankGlyphRenderer;
	private GlyphRenderer whiteRectangleGlyphRenderer;
	private final List<Font> fonts = Lists.<Font>newArrayList();
	private final Int2ObjectMap<GlyphRenderer> glyphRendererCache = new Int2ObjectOpenHashMap<>();
	private final Int2ObjectMap<FontStorage.class_7647> glyphCache = new Int2ObjectOpenHashMap<>();
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
		this.blankGlyphRenderer = BuiltinEmptyGlyph.MISSING.bake(this::getGlyphRenderer);
		this.whiteRectangleGlyphRenderer = BuiltinEmptyGlyph.WHITE.bake(this::getGlyphRenderer);
		IntSet intSet = new IntOpenHashSet();

		for (Font font : fonts) {
			intSet.addAll(font.getProvidedGlyphs());
		}

		Set<Font> set = Sets.<Font>newHashSet();
		intSet.forEach(
			codePoint -> {
				for (Font fontx : fonts) {
					Glyph glyph = fontx.getGlyph(codePoint);
					if (glyph != null) {
						set.add(fontx);
						if (glyph != BuiltinEmptyGlyph.MISSING) {
							this.charactersByWidth
								.computeIfAbsent(MathHelper.ceil(glyph.getAdvance(false)), (Int2ObjectFunction<? extends IntList>)(advance -> new IntArrayList()))
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

	private static boolean method_45079(Glyph glyph) {
		float f = glyph.getAdvance(false);
		if (!(f < 0.0F) && !(f > 32.0F)) {
			float g = glyph.getAdvance(true);
			return g < 0.0F || g > 32.0F;
		} else {
			return true;
		}
	}

	/**
	 * {@return the glyph of {@code codePoint}}
	 * 
	 * @apiNote Call {@link #getGlyph} instead, as that method provides caching.
	 */
	private FontStorage.class_7647 findGlyph(int codePoint) {
		Glyph glyph = null;

		for (Font font : this.fonts) {
			Glyph glyph2 = font.getGlyph(codePoint);
			if (glyph2 != null) {
				if (glyph == null) {
					glyph = glyph2;
				}

				if (!method_45079(glyph2)) {
					return new FontStorage.class_7647(glyph, glyph2);
				}
			}
		}

		return glyph != null ? new FontStorage.class_7647(glyph, BuiltinEmptyGlyph.MISSING) : FontStorage.class_7647.field_39935;
	}

	/**
	 * {@return the glyph of {@code codePoint}}
	 * 
	 * @implNote {@link BuiltinEmptyGlyph#MISSING} is returned for missing code points.
	 */
	public Glyph getGlyph(int codePoint, boolean bl) {
		return this.glyphCache.computeIfAbsent(codePoint, this::findGlyph).method_45080(bl);
	}

	private GlyphRenderer findGlyphRenderer(int codePoint) {
		for (Font font : this.fonts) {
			Glyph glyph = font.getGlyph(codePoint);
			if (glyph != null) {
				return glyph.bake(this::getGlyphRenderer);
			}
		}

		return this.blankGlyphRenderer;
	}

	public GlyphRenderer getGlyphRenderer(int codePoint) {
		return this.glyphRendererCache.computeIfAbsent(codePoint, this::findGlyphRenderer);
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

	@Environment(EnvType.CLIENT)
	static record class_7647(Glyph glyphInfo, Glyph glyphInfoNotFishy) {
		static final FontStorage.class_7647 field_39935 = new FontStorage.class_7647(BuiltinEmptyGlyph.MISSING, BuiltinEmptyGlyph.MISSING);

		Glyph method_45080(boolean bl) {
			return bl ? this.glyphInfoNotFishy : this.glyphInfo;
		}
	}
}
