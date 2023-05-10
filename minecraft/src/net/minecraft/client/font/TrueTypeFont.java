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
	@Nullable
	private ByteBuffer buffer;
	@Nullable
	private STBTTFontinfo info;
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
	@Override
	public Glyph getGlyph(int codePoint) {
		STBTTFontinfo sTBTTFontinfo = this.getInfo();
		if (this.excludedCharacters.contains(codePoint)) {
			return null;
		} else {
			Glyph.EmptyGlyph var14;
			try (MemoryStack memoryStack = MemoryStack.stackPush()) {
				int i = STBTruetype.stbtt_FindGlyphIndex(sTBTTFontinfo, codePoint);
				if (i == 0) {
					return null;
				}

				IntBuffer intBuffer = memoryStack.mallocInt(1);
				IntBuffer intBuffer2 = memoryStack.mallocInt(1);
				IntBuffer intBuffer3 = memoryStack.mallocInt(1);
				IntBuffer intBuffer4 = memoryStack.mallocInt(1);
				IntBuffer intBuffer5 = memoryStack.mallocInt(1);
				IntBuffer intBuffer6 = memoryStack.mallocInt(1);
				STBTruetype.stbtt_GetGlyphHMetrics(sTBTTFontinfo, i, intBuffer5, intBuffer6);
				STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(
					sTBTTFontinfo, i, this.scaleFactor, this.scaleFactor, this.shiftX, this.shiftY, intBuffer, intBuffer2, intBuffer3, intBuffer4
				);
				float f = (float)intBuffer5.get(0) * this.scaleFactor;
				int j = intBuffer3.get(0) - intBuffer.get(0);
				int k = intBuffer4.get(0) - intBuffer2.get(0);
				if (j > 0 && k > 0) {
					return new TrueTypeFont.TtfGlyph(
						intBuffer.get(0), intBuffer3.get(0), -intBuffer2.get(0), -intBuffer4.get(0), f, (float)intBuffer6.get(0) * this.scaleFactor, i
					);
				}

				var14 = () -> f / this.oversample;
			}

			return var14;
		}
	}

	STBTTFontinfo getInfo() {
		if (this.buffer != null && this.info != null) {
			return this.info;
		} else {
			throw new IllegalArgumentException("Provider already closed");
		}
	}

	@Override
	public void close() {
		if (this.info != null) {
			this.info.free();
			this.info = null;
		}

		MemoryUtil.memFree(this.buffer);
		this.buffer = null;
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

		TtfGlyph(int x1, int x2, int y2, int y1, float advance, float bearingX, int glyphIndex) {
			this.width = x2 - x1;
			this.height = y2 - y1;
			this.advance = advance / TrueTypeFont.this.oversample;
			this.bearingX = (bearingX + (float)x1 + TrueTypeFont.this.shiftX) / TrueTypeFont.this.oversample;
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
						STBTTFontinfo sTBTTFontinfo = TrueTypeFont.this.getInfo();
						NativeImage nativeImage = new NativeImage(NativeImage.Format.LUMINANCE, TtfGlyph.this.width, TtfGlyph.this.height, false);
						nativeImage.makeGlyphBitmapSubpixel(
							sTBTTFontinfo,
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
