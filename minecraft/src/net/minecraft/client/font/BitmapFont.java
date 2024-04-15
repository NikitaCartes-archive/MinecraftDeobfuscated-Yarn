package net.minecraft.client.font;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class BitmapFont implements Font {
	static final Logger LOGGER = LogUtils.getLogger();
	private final NativeImage image;
	private final GlyphContainer<BitmapFont.BitmapFontGlyph> glyphs;

	BitmapFont(NativeImage image, GlyphContainer<BitmapFont.BitmapFontGlyph> glyphs) {
		this.image = image;
		this.glyphs = glyphs;
	}

	@Override
	public void close() {
		this.image.close();
	}

	@Nullable
	@Override
	public Glyph getGlyph(int codePoint) {
		return this.glyphs.get(codePoint);
	}

	@Override
	public IntSet getProvidedGlyphs() {
		return IntSets.unmodifiable(this.glyphs.getProvidedGlyphs());
	}

	@Environment(EnvType.CLIENT)
	static record BitmapFontGlyph(float scaleFactor, NativeImage image, int x, int y, int width, int height, int advance, int ascent) implements Glyph {

		@Override
		public float getAdvance() {
			return (float)this.advance;
		}

		@Override
		public GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> function) {
			return (GlyphRenderer)function.apply(
				new RenderableGlyph() {
					@Override
					public float getOversample() {
						return 1.0F / BitmapFontGlyph.this.scaleFactor;
					}
	
					@Override
					public int getWidth() {
						return BitmapFontGlyph.this.width;
					}
	
					@Override
					public int getHeight() {
						return BitmapFontGlyph.this.height;
					}
	
					@Override
					public float getAscent() {
						return (float)BitmapFontGlyph.this.ascent;
					}
	
					@Override
					public void upload(int x, int y) {
						BitmapFontGlyph.this.image
							.upload(0, x, y, BitmapFontGlyph.this.x, BitmapFontGlyph.this.y, BitmapFontGlyph.this.width, BitmapFontGlyph.this.height, false, false);
					}
	
					@Override
					public boolean hasColor() {
						return BitmapFontGlyph.this.image.getFormat().getChannelCount() > 1;
					}
				}
			);
		}
	}

	@Environment(EnvType.CLIENT)
	public static record Loader(Identifier file, int height, int ascent, int[][] codepointGrid) implements FontLoader {
		private static final Codec<int[][]> CODE_POINT_GRID_CODEC = Codec.STRING.listOf().<int[][]>xmap(strings -> {
			int i = strings.size();
			int[][] is = new int[i][];

			for (int j = 0; j < i; j++) {
				is[j] = ((String)strings.get(j)).codePoints().toArray();
			}

			return is;
		}, codePointGrid -> {
			List<String> list = new ArrayList(codePointGrid.length);

			for (int[] is : codePointGrid) {
				list.add(new String(is, 0, is.length));
			}

			return list;
		}).validate(BitmapFont.Loader::validateCodePointGrid);
		public static final MapCodec<BitmapFont.Loader> CODEC = RecordCodecBuilder.<BitmapFont.Loader>mapCodec(
				instance -> instance.group(
							Identifier.CODEC.fieldOf("file").forGetter(BitmapFont.Loader::file),
							Codec.INT.optionalFieldOf("height", Integer.valueOf(8)).forGetter(BitmapFont.Loader::height),
							Codec.INT.fieldOf("ascent").forGetter(BitmapFont.Loader::ascent),
							CODE_POINT_GRID_CODEC.fieldOf("chars").forGetter(BitmapFont.Loader::codepointGrid)
						)
						.apply(instance, BitmapFont.Loader::new)
			)
			.validate(BitmapFont.Loader::validate);

		private static DataResult<int[][]> validateCodePointGrid(int[][] codePointGrid) {
			int i = codePointGrid.length;
			if (i == 0) {
				return DataResult.error(() -> "Expected to find data in codepoint grid");
			} else {
				int[] is = codePointGrid[0];
				int j = is.length;
				if (j == 0) {
					return DataResult.error(() -> "Expected to find data in codepoint grid");
				} else {
					for (int k = 1; k < i; k++) {
						int[] js = codePointGrid[k];
						if (js.length != j) {
							return DataResult.error(
								() -> "Lines in codepoint grid have to be the same length (found: " + js.length + " codepoints, expected: " + j + "), pad with \\u0000"
							);
						}
					}

					return DataResult.success(codePointGrid);
				}
			}
		}

		private static DataResult<BitmapFont.Loader> validate(BitmapFont.Loader fontLoader) {
			return fontLoader.ascent > fontLoader.height
				? DataResult.error(() -> "Ascent " + fontLoader.ascent + " higher than height " + fontLoader.height)
				: DataResult.success(fontLoader);
		}

		@Override
		public FontType getType() {
			return FontType.BITMAP;
		}

		@Override
		public Either<FontLoader.Loadable, FontLoader.Reference> build() {
			return Either.left(this::load);
		}

		private Font load(ResourceManager resourceManager) throws IOException {
			Identifier identifier = this.file.withPrefixedPath("textures/");
			InputStream inputStream = resourceManager.open(identifier);

			BitmapFont var22;
			try {
				NativeImage nativeImage = NativeImage.read(NativeImage.Format.RGBA, inputStream);
				int i = nativeImage.getWidth();
				int j = nativeImage.getHeight();
				int k = i / this.codepointGrid[0].length;
				int l = j / this.codepointGrid.length;
				float f = (float)this.height / (float)l;
				GlyphContainer<BitmapFont.BitmapFontGlyph> glyphContainer = new GlyphContainer<>(BitmapFont.BitmapFontGlyph[]::new, BitmapFont.BitmapFontGlyph[][]::new);

				for (int m = 0; m < this.codepointGrid.length; m++) {
					int n = 0;

					for (int o : this.codepointGrid[m]) {
						int p = n++;
						if (o != 0) {
							int q = this.findCharacterStartX(nativeImage, k, l, p, m);
							BitmapFont.BitmapFontGlyph bitmapFontGlyph = glyphContainer.put(
								o, new BitmapFont.BitmapFontGlyph(f, nativeImage, p * k, m * l, k, l, (int)(0.5 + (double)((float)q * f)) + 1, this.ascent)
							);
							if (bitmapFontGlyph != null) {
								BitmapFont.LOGGER.warn("Codepoint '{}' declared multiple times in {}", Integer.toHexString(o), identifier);
							}
						}
					}
				}

				var22 = new BitmapFont(nativeImage, glyphContainer);
			} catch (Throwable var21) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var20) {
						var21.addSuppressed(var20);
					}
				}

				throw var21;
			}

			if (inputStream != null) {
				inputStream.close();
			}

			return var22;
		}

		private int findCharacterStartX(NativeImage image, int characterWidth, int characterHeight, int charPosX, int charPosY) {
			int i;
			for (i = characterWidth - 1; i >= 0; i--) {
				int j = charPosX * characterWidth + i;

				for (int k = 0; k < characterHeight; k++) {
					int l = charPosY * characterHeight + k;
					if (image.getOpacity(j, l) != 0) {
						return i + 1;
					}
				}
			}

			return i + 1;
		}
	}
}
