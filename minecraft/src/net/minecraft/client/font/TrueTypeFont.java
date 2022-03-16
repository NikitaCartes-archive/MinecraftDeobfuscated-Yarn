package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.Function;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class TrueTypeFont implements Font {
	private final ByteBuffer buffer;
	final STBTTFontinfo info;
	final float oversample;
	private final IntSet excludedCharacters = new IntArraySet();
	final float shiftX;
	final float shiftY;
	final float scaleFactor;
	final float ascent;

	public TrueTypeFont(ByteBuffer buffer, STBTTFontinfo info, float size, float oversample, float shiftX, float shiftY, String excludedCharacters) {
		this.buffer = buffer;
		this.info = info;
		this.oversample = oversample;
		excludedCharacters.codePoints().forEach(this.excludedCharacters::add);
		this.shiftX = shiftX * oversample;
		this.shiftY = shiftY * oversample;
		this.scaleFactor = STBTruetype.stbtt_ScaleForPixelHeight(info, size * oversample);

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			IntBuffer intBuffer = memoryStack.mallocInt(1);
			IntBuffer intBuffer2 = memoryStack.mallocInt(1);
			IntBuffer intBuffer3 = memoryStack.mallocInt(1);
			STBTruetype.stbtt_GetFontVMetrics(info, intBuffer, intBuffer2, intBuffer3);
			this.ascent = (float)intBuffer.get(0) * this.scaleFactor;
		}
	}

	@Nullable
	public TrueTypeFont.TtfGlyph getGlyph(int i) {
		if (this.excludedCharacters.contains(i)) {
			return null;
		} else {
			Object intBuffer5;
			try (MemoryStack memoryStack = MemoryStack.stackPush()) {
				IntBuffer intBuffer = memoryStack.mallocInt(1);
				IntBuffer intBuffer2 = memoryStack.mallocInt(1);
				IntBuffer intBuffer3 = memoryStack.mallocInt(1);
				IntBuffer intBuffer4 = memoryStack.mallocInt(1);
				int j = STBTruetype.stbtt_FindGlyphIndex(this.info, i);
				if (j == 0) {
					return null;
				}

				STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(
					this.info, j, this.scaleFactor, this.scaleFactor, this.shiftX, this.shiftY, intBuffer, intBuffer2, intBuffer3, intBuffer4
				);
				int k = intBuffer3.get(0) - intBuffer.get(0);
				int l = intBuffer4.get(0) - intBuffer2.get(0);
				if (k > 0 && l > 0) {
					IntBuffer intBuffer5x = memoryStack.mallocInt(1);
					IntBuffer intBuffer6 = memoryStack.mallocInt(1);
					STBTruetype.stbtt_GetGlyphHMetrics(this.info, j, intBuffer5x, intBuffer6);
					return new TrueTypeFont.TtfGlyph(
						intBuffer.get(0),
						intBuffer3.get(0),
						-intBuffer2.get(0),
						-intBuffer4.get(0),
						(float)intBuffer5x.get(0) * this.scaleFactor,
						(float)intBuffer6.get(0) * this.scaleFactor,
						j
					);
				}

				intBuffer5 = null;
			}

			return (TrueTypeFont.TtfGlyph)intBuffer5;
		}
	}

	@Override
	public void close() {
		this.info.free();
		MemoryUtil.memFree(this.buffer);
	}

	@Override
	public IntSet getProvidedGlyphs() {
		return (IntSet)IntStream.range(0, 65535)
			.filter(codePoint -> !this.excludedCharacters.contains(codePoint))
			.collect(IntOpenHashSet::new, IntCollection::add, IntCollection::addAll);
	}

	@Environment(EnvType.CLIENT)
	class TtfGlyph implements Glyph {
		final int width;
		final int height;
		final float bearingX;
		final float ascent;
		private final float advance;
		final int glyphIndex;

		TtfGlyph(int x1, int x2, int y2, int y1, float f, float g, int glyphIndex) {
			this.width = x2 - x1;
			this.height = y2 - y1;
			this.advance = f / TrueTypeFont.this.oversample;
			this.bearingX = (g + (float)x1 + TrueTypeFont.this.shiftX) / TrueTypeFont.this.oversample;
			this.ascent = (TrueTypeFont.this.ascent - (float)y2 + TrueTypeFont.this.shiftY) / TrueTypeFont.this.oversample;
			this.glyphIndex = glyphIndex;
		}

		@Override
		public float getAdvance() {
			return this.advance;
		}

		@Override
		public GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> function) {
			return (GlyphRenderer)function.apply(
				new RenderableGlyph() {
					@Override
					public int getWidth() {
						return TtfGlyph.this.width;
					}

					@Override
					public int getHeight() {
						return TtfGlyph.this.height;
					}

					@Override
					public float getOversample() {
						return TrueTypeFont.this.oversample;
					}

					@Override
					public float getBearingX() {
						return TtfGlyph.this.bearingX;
					}

					@Override
					public float getAscent() {
						return TtfGlyph.this.ascent;
					}

					@Override
					public void upload(int x, int y) {
						NativeImage nativeImage = new NativeImage(NativeImage.Format.LUMINANCE, TtfGlyph.this.width, TtfGlyph.this.height, false);
						nativeImage.makeGlyphBitmapSubpixel(
							TrueTypeFont.this.info,
							TtfGlyph.this.glyphIndex,
							TtfGlyph.this.width,
							TtfGlyph.this.height,
							TrueTypeFont.this.scaleFactor,
							TrueTypeFont.this.scaleFactor,
							TrueTypeFont.this.shiftX,
							TrueTypeFont.this.shiftY,
							0,
							0
						);
						nativeImage.upload(0, x, y, 0, 0, TtfGlyph.this.width, TtfGlyph.this.height, false, true);
					}

					@Override
					public boolean hasColor() {
						return false;
					}
				}
			);
		}
	}
}
