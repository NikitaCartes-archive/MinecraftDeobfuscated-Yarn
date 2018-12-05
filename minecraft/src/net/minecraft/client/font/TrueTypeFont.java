package net.minecraft.client.font;

import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_383;
import net.minecraft.class_390;
import net.minecraft.client.texture.NativeImage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

@Environment(EnvType.CLIENT)
public class TrueTypeFont implements class_390 {
	private static final Logger LOGGER = LogManager.getLogger();
	private final STBTTFontinfo info;
	private final float field_2321;
	private final CharSet field_2319 = new CharArraySet();
	private final float field_2320;
	private final float field_2318;
	private final float stbScaleFactor;
	private final float ascent;

	public TrueTypeFont(STBTTFontinfo sTBTTFontinfo, float f, float g, float h, float i, String string) {
		this.info = sTBTTFontinfo;
		this.field_2321 = g;
		string.chars().forEach(ix -> this.field_2319.add((char)(ix & 65535)));
		this.field_2320 = h * g;
		this.field_2318 = i * g;
		this.stbScaleFactor = STBTruetype.stbtt_ScaleForPixelHeight(sTBTTFontinfo, f * g);

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			IntBuffer intBuffer = memoryStack.mallocInt(1);
			IntBuffer intBuffer2 = memoryStack.mallocInt(1);
			IntBuffer intBuffer3 = memoryStack.mallocInt(1);
			STBTruetype.stbtt_GetFontVMetrics(sTBTTFontinfo, intBuffer, intBuffer2, intBuffer3);
			this.ascent = (float)intBuffer.get(0) * this.stbScaleFactor;
		}
	}

	@Nullable
	public TrueTypeFont.class_397 method_2051(char c) {
		if (this.field_2319.contains(c)) {
			return null;
		} else {
			TrueTypeFont.class_397 var13;
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
					this.info, i, this.stbScaleFactor, this.stbScaleFactor, this.field_2320, this.field_2318, intBuffer, intBuffer2, intBuffer3, intBuffer4
				);
				int j = intBuffer3.get(0) - intBuffer.get(0);
				int k = intBuffer4.get(0) - intBuffer2.get(0);
				if (j == 0 || k == 0) {
					return null;
				}

				IntBuffer intBuffer5 = memoryStack.mallocInt(1);
				IntBuffer intBuffer6 = memoryStack.mallocInt(1);
				STBTruetype.stbtt_GetGlyphHMetrics(this.info, i, intBuffer5, intBuffer6);
				var13 = new TrueTypeFont.class_397(
					intBuffer.get(0),
					intBuffer3.get(0),
					-intBuffer2.get(0),
					-intBuffer4.get(0),
					(float)intBuffer5.get(0) * this.stbScaleFactor,
					(float)intBuffer6.get(0) * this.stbScaleFactor,
					i
				);
			}

			return var13;
		}
	}

	public static STBTTFontinfo method_15975(ByteBuffer byteBuffer) throws IOException {
		STBTTFontinfo sTBTTFontinfo = STBTTFontinfo.create();
		if (!STBTruetype.stbtt_InitFont(sTBTTFontinfo, byteBuffer)) {
			throw new IOException("Invalid ttf");
		} else {
			return sTBTTFontinfo;
		}
	}

	@Environment(EnvType.CLIENT)
	class class_397 implements class_383 {
		private final int field_2338;
		private final int field_2337;
		private final float field_2334;
		private final float field_2333;
		private final float advance;
		private final int field_2335;

		private class_397(int i, int j, int k, int l, float f, float g, int m) {
			this.field_2338 = j - i;
			this.field_2337 = k - l;
			this.advance = f / TrueTypeFont.this.field_2321;
			this.field_2334 = (g + (float)i + TrueTypeFont.this.field_2320) / TrueTypeFont.this.field_2321;
			this.field_2333 = (TrueTypeFont.this.ascent - (float)k + TrueTypeFont.this.field_2318) / TrueTypeFont.this.field_2321;
			this.field_2335 = m;
		}

		@Override
		public int method_2031() {
			return this.field_2338;
		}

		@Override
		public int method_2032() {
			return this.field_2337;
		}

		@Override
		public float method_2035() {
			return TrueTypeFont.this.field_2321;
		}

		@Override
		public float getAdvance() {
			return this.advance;
		}

		@Override
		public float getBearingX() {
			return this.field_2334;
		}

		@Override
		public float method_15976() {
			return this.field_2333;
		}

		@Override
		public void method_2030(int i, int j) {
			try (NativeImage nativeImage = new NativeImage(NativeImage.Format.field_4998, this.field_2338, this.field_2337, false)) {
				nativeImage.method_4316(
					TrueTypeFont.this.info,
					this.field_2335,
					this.field_2338,
					this.field_2337,
					TrueTypeFont.this.stbScaleFactor,
					TrueTypeFont.this.stbScaleFactor,
					TrueTypeFont.this.field_2320,
					TrueTypeFont.this.field_2318,
					0,
					0
				);
				nativeImage.upload(0, i, j, 0, 0, this.field_2338, this.field_2337, false);
			}
		}

		@Override
		public boolean method_2033() {
			return false;
		}
	}
}
