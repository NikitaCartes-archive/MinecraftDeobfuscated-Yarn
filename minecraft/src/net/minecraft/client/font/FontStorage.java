package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class FontStorage implements AutoCloseable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Random RANDOM = Random.create();
	private static final float MAX_ADVANCE = 32.0F;
	private final TextureManager textureManager;
	private final Identifier id;
	private BakedGlyph blankBakedGlyph;
	private BakedGlyph whiteRectangleBakedGlyph;
	private List<Font.FontFilterPair> allFonts = List.of();
	private List<Font> availableFonts = List.of();
	private final GlyphContainer<BakedGlyph> bakedGlyphCache = new GlyphContainer<>(BakedGlyph[]::new, BakedGlyph[][]::new);
	private final GlyphContainer<FontStorage.GlyphPair> glyphCache = new GlyphContainer<>(FontStorage.GlyphPair[]::new, FontStorage.GlyphPair[][]::new);
	private final Int2ObjectMap<IntList> charactersByWidth = new Int2ObjectOpenHashMap<>();
	private final List<GlyphAtlasTexture> glyphAtlases = Lists.<GlyphAtlasTexture>newArrayList();
	private final IntFunction<FontStorage.GlyphPair> glyphFinder = this::findGlyph;
	private final IntFunction<BakedGlyph> glyphBaker = this::bake;

	public FontStorage(TextureManager textureManager, Identifier id) {
		this.textureManager = textureManager;
		this.id = id;
	}

	public void setFonts(List<Font.FontFilterPair> allFonts, Set<FontFilterType> activeFilters) {
		this.allFonts = allFonts;
		this.setActiveFilters(activeFilters);
	}

	public void setActiveFilters(Set<FontFilterType> activeFilters) {
		this.availableFonts = List.of();
		this.clear();
		this.availableFonts = this.applyFilters(this.allFonts, activeFilters);
	}

	private void clear() {
		this.closeGlyphAtlases();
		this.bakedGlyphCache.clear();
		this.glyphCache.clear();
		this.charactersByWidth.clear();
		this.blankBakedGlyph = BuiltinEmptyGlyph.MISSING.bake(this::bake);
		this.whiteRectangleBakedGlyph = BuiltinEmptyGlyph.WHITE.bake(this::bake);
	}

	private List<Font> applyFilters(List<Font.FontFilterPair> allFonts, Set<FontFilterType> activeFilters) {
		IntSet intSet = new IntOpenHashSet();
		List<Font> list = new ArrayList();

		for (Font.FontFilterPair fontFilterPair : allFonts) {
			if (fontFilterPair.filter().isAllowed(activeFilters)) {
				list.add(fontFilterPair.provider());
				intSet.addAll(fontFilterPair.provider().getProvidedGlyphs());
			}
		}

		Set<Font> set = Sets.<Font>newHashSet();
		intSet.forEach(
			codePoint -> {
				for (Font font : list) {
					Glyph glyph = font.getGlyph(codePoint);
					if (glyph != null) {
						set.add(font);
						if (glyph != BuiltinEmptyGlyph.MISSING) {
							this.charactersByWidth
								.computeIfAbsent(MathHelper.ceil(glyph.getAdvance(false)), (Int2ObjectFunction<? extends IntList>)(i -> new IntArrayList()))
								.add(codePoint);
						}
						break;
					}
				}
			}
		);
		return list.stream().filter(set::contains).toList();
	}

	public void close() {
		this.closeGlyphAtlases();
	}

	private void closeGlyphAtlases() {
		for (GlyphAtlasTexture glyphAtlasTexture : this.glyphAtlases) {
			glyphAtlasTexture.close();
		}

		this.glyphAtlases.clear();
	}

	private static boolean isAdvanceInvalid(Glyph glyph) {
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
	private FontStorage.GlyphPair findGlyph(int codePoint) {
		Glyph glyph = null;

		for (Font font : this.availableFonts) {
			Glyph glyph2 = font.getGlyph(codePoint);
			if (glyph2 != null) {
				if (glyph == null) {
					glyph = glyph2;
				}

				if (!isAdvanceInvalid(glyph2)) {
					return new FontStorage.GlyphPair(glyph, glyph2);
				}
			}
		}

		return glyph != null ? new FontStorage.GlyphPair(glyph, BuiltinEmptyGlyph.MISSING) : FontStorage.GlyphPair.MISSING;
	}

	/**
	 * {@return the glyph of {@code codePoint}}
	 * 
	 * @implNote {@link BuiltinEmptyGlyph#MISSING} is returned for missing code points.
	 */
	public Glyph getGlyph(int codePoint, boolean validateAdvance) {
		return this.glyphCache.computeIfAbsent(codePoint, this.glyphFinder).getGlyph(validateAdvance);
	}

	private BakedGlyph bake(int codePoint) {
		for (Font font : this.availableFonts) {
			Glyph glyph = font.getGlyph(codePoint);
			if (glyph != null) {
				return glyph.bake(this::bake);
			}
		}

		LOGGER.warn("Couldn't find glyph for character {} (\\u{})", Character.toString(codePoint), String.format("%04x", codePoint));
		return this.blankBakedGlyph;
	}

	public BakedGlyph getBaked(int codePoint) {
		return this.bakedGlyphCache.computeIfAbsent(codePoint, this.glyphBaker);
	}

	private BakedGlyph bake(RenderableGlyph c) {
		for (GlyphAtlasTexture glyphAtlasTexture : this.glyphAtlases) {
			BakedGlyph bakedGlyph = glyphAtlasTexture.bake(c);
			if (bakedGlyph != null) {
				return bakedGlyph;
			}
		}

		Identifier identifier = this.id.withSuffixedPath("/" + this.glyphAtlases.size());
		boolean bl = c.hasColor();
		TextRenderLayerSet textRenderLayerSet = bl ? TextRenderLayerSet.of(identifier) : TextRenderLayerSet.ofIntensity(identifier);
		GlyphAtlasTexture glyphAtlasTexture2 = new GlyphAtlasTexture(textRenderLayerSet, bl);
		this.glyphAtlases.add(glyphAtlasTexture2);
		this.textureManager.registerTexture(identifier, glyphAtlasTexture2);
		BakedGlyph bakedGlyph2 = glyphAtlasTexture2.bake(c);
		return bakedGlyph2 == null ? this.blankBakedGlyph : bakedGlyph2;
	}

	public BakedGlyph getObfuscatedBakedGlyph(Glyph glyph) {
		IntList intList = this.charactersByWidth.get(MathHelper.ceil(glyph.getAdvance(false)));
		return intList != null && !intList.isEmpty() ? this.getBaked(intList.getInt(RANDOM.nextInt(intList.size()))) : this.blankBakedGlyph;
	}

	public Identifier getId() {
		return this.id;
	}

	public BakedGlyph getRectangleBakedGlyph() {
		return this.whiteRectangleBakedGlyph;
	}

	@Environment(EnvType.CLIENT)
	static record GlyphPair(Glyph glyph, Glyph advanceValidatedGlyph) {
		static final FontStorage.GlyphPair MISSING = new FontStorage.GlyphPair(BuiltinEmptyGlyph.MISSING, BuiltinEmptyGlyph.MISSING);

		Glyph getGlyph(boolean validateAdvance) {
			return validateAdvance ? this.advanceValidatedGlyph : this.glyph;
		}
	}
}
