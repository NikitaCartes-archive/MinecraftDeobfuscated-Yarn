package net.minecraft.client.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class FontStorage implements AutoCloseable {
	private static final EmptyGlyphRenderer EMPTY_GLYPH_RENDERER = new EmptyGlyphRenderer();
	private static final Glyph SPACE = () -> 4.0F;
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
		this.method_24290();
		this.closeGlyphAtlases();
		this.glyphRendererCache.clear();
		this.glyphCache.clear();
		this.charactersByWidth.clear();
		this.blankGlyphRenderer = this.getGlyphRenderer(BlankGlyph.INSTANCE);
		this.whiteRectangleGlyphRenderer = this.getGlyphRenderer(WhiteRectangleGlyph.INSTANCE);
		IntSet intSet = new IntOpenHashSet();

		for (Font font : fonts) {
			intSet.addAll(font.method_27442());
		}

		Set<Font> set = Sets.<Font>newHashSet();
		intSet.forEach(i -> {
			for (Font fontx : fonts) {
				Glyph glyph = (Glyph)(i == 32 ? SPACE : fontx.getGlyph(i));
				if (glyph != null) {
					set.add(fontx);
					if (glyph != BlankGlyph.INSTANCE) {
						this.charactersByWidth.computeIfAbsent(MathHelper.ceil(glyph.getAdvance(false)), ix -> new IntArrayList()).add(i);
					}
					break;
				}
			}
		});
		fonts.stream().filter(set::contains).forEach(this.fonts::add);
	}

	public void close() {
		this.method_24290();
		this.closeGlyphAtlases();
	}

	private void method_24290() {
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

	public Glyph getGlyph(int i) {
		return this.glyphCache.computeIfAbsent(i, ix -> (Glyph)(ix == 32 ? SPACE : this.getRenderableGlyph(ix)));
	}

	private RenderableGlyph getRenderableGlyph(int i) {
		for (Font font : this.fonts) {
			RenderableGlyph renderableGlyph = font.getGlyph(i);
			if (renderableGlyph != null) {
				return renderableGlyph;
			}
		}

		return BlankGlyph.INSTANCE;
	}

	public GlyphRenderer getGlyphRenderer(int i) {
		return this.glyphRendererCache
			.computeIfAbsent(i, ix -> (GlyphRenderer)(ix == 32 ? EMPTY_GLYPH_RENDERER : this.getGlyphRenderer(this.getRenderableGlyph(ix))));
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
