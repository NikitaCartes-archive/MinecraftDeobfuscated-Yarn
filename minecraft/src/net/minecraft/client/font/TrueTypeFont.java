package net.minecraft.client.font;

import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
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
	private final ByteBuffer field_21839;
	private final STBTTFontinfo info;
	private final float oversample;
	private final CharSet excludedCharacters = new CharArraySet();
	private final float shiftX;
	private final float shiftY;
	private final float scaleFactor;
	private final float ascent;

	public TrueTypeFont(ByteBuffer byteBuffer, STBTTFontinfo sTBTTFontinfo, float f, float g, float h, float i, String string) {
		this.field_21839 = byteBuffer;
		this.info = sTBTTFontinfo;
		this.oversample = g;
		string.chars().forEach(ix -> this.excludedCharacters.add((char)(ix & 65535)));
		this.shiftX = h * g;
		this.shiftY = i * g;
		this.scaleFactor = STBTruetype.stbtt_ScaleForPixelHeight(sTBTTFontinfo, f * g);

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			IntBuffer intBuffer = memoryStack.mallocInt(1);
			IntBuffer intBuffer2 = memoryStack.mallocInt(1);
			IntBuffer intBuffer3 = memoryStack.mallocInt(1);
			STBTruetype.stbtt_GetFontVMetrics(sTBTTFontinfo, intBuffer, intBuffer2, intBuffer3);
			this.ascent = (float)intBuffer.get(0) * this.scaleFactor;
		}
	}

	@Nullable
	public TrueTypeFont.TtfGlyph getGlyph(char c) {
		if (this.excludedCharacters.contains(c)) {
			return null;
		} else {
			TrueTypeFont.TtfGlyph var13;
			try (MemoryStack memoryStack = MemoryStack.stackPush()) {
				IntBuffer intBuffer = memoryStack.mallocInt(1);
				IntBuffer intBuffer2 = memoryStack.mallocInt(1);
				IntBuffer intBuffer3 = memoryStack.mallocInt(1);
				IntBuffer intBuffer4 = memoryStack.mallocInt(1);
				int i = STBTruetype.stbtt_FindGlyphIndex(this.info, c);
				if (i == 0) {
					return null;
				}

				STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(
					this.info, i, this.scaleFactor, this.scaleFactor, this.shiftX, this.shiftY, intBuffer, intBuffer2, intBuffer3, intBuffer4
				);
				int j = intBuffer3.get(0) - intBuffer.get(0);
				int k = intBuffer4.get(0) - intBuffer2.get(0);
				if (j == 0 || k == 0) {
					return null;
				}

				IntBuffer intBuffer5 = memoryStack.mallocInt(1);
				IntBuffer intBuffer6 = memoryStack.mallocInt(1);
				STBTruetype.stbtt_GetGlyphHMetrics(this.info, i, intBuffer5, intBuffer6);
				var13 = new TrueTypeFont.TtfGlyph(
					intBuffer.get(0),
					intBuffer3.get(0),
					-intBuffer2.get(0),
					-intBuffer4.get(0),
					(float)intBuffer5.get(0) * this.scaleFactor,
					(float)intBuffer6.get(0) * this.scaleFactor,
					i
				);
			}

			return var13;
		}
	}

	@Override
	public void close() {
		this.info.free();
		MemoryUtil.memFree(this.field_21839);
	}

	@Environment(EnvType.CLIENT)
	class TtfGlyph implements RenderableGlyph {
		private final int width;
		private final int height;
		private final float bearingX;
		private final float ascent;
		private final float advance;
		private final int glyphIndex;

		private TtfGlyph(int xMin, int xMax, int yMax, int yMin, float advance, float bearing, int index) {
			this.width = xMax - xMin;
			this.height = yMax - yMin;
			this.advance = advance / TrueTypeFont.this.oversample;
			this.bearingX = (bearing + (float)xMin + TrueTypeFont.this.shiftX) / TrueTypeFont.this.oversample;
			this.ascent = (TrueTypeFont.this.ascent - (float)yMax + TrueTypeFont.this.shiftY) / TrueTypeFont.this.oversample;
			this.glyphIndex = index;
		}

		@Override
		public int getWidth() {
			return this.width;
		}

		@Override
		public int getHeight() {
			return this.height;
		}

		@Override
		public float getOversample() {
			return TrueTypeFont.this.oversample;
		}

		@Override
		public float getAdvance() {
			return this.advance;
		}

		@Override
		public float getBearingX() {
			return this.bearingX;
		}

		@Override
		public float getAscent() {
			return this.ascent;
		}

		@Override
		public void upload(int x, int y) {
			NativeImage nativeImage = new NativeImage(NativeImage.Format.LUMINANCE, this.width, this.height, false);
			nativeImage.makeGlyphBitmapSubpixel(
				TrueTypeFont.this.info,
				this.glyphIndex,
				this.width,
				this.height,
				TrueTypeFont.this.scaleFactor,
				TrueTypeFont.this.scaleFactor,
				TrueTypeFont.this.shiftX,
				TrueTypeFont.this.shiftY,
				0,
				0
			);
			nativeImage.upload(0, x, y, 0, 0, this.width, this.height, false, true);
		}

		@Override
		public boolean hasColor() {
			return false;
		}
	}
}
