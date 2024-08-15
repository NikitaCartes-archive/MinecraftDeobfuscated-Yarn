package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Locale;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_GlyphSlot;
import org.lwjgl.util.freetype.FT_Vector;
import org.lwjgl.util.freetype.FreeType;

@Environment(EnvType.CLIENT)
public class TrueTypeFont implements Font {
	@Nullable
	private ByteBuffer buffer;
	@Nullable
	private FT_Face face;
	final float oversample;
	private final GlyphContainer<TrueTypeFont.class_9908> field_52695 = new GlyphContainer<>(TrueTypeFont.class_9908[]::new, TrueTypeFont.class_9908[][]::new);

	public TrueTypeFont(ByteBuffer buffer, FT_Face face, float size, float oversample, float shiftX, float shiftY, String excludedCharacters) {
		this.buffer = buffer;
		this.face = face;
		this.oversample = oversample;
		IntSet intSet = new IntArraySet();
		excludedCharacters.codePoints().forEach(intSet::add);
		int i = Math.round(size * oversample);
		FreeType.FT_Set_Pixel_Sizes(face, i, i);
		float f = shiftX * oversample;
		float g = -shiftY * oversample;

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			FT_Vector fT_Vector = FreeTypeUtil.set(FT_Vector.malloc(memoryStack), f, g);
			FreeType.FT_Set_Transform(face, null, fT_Vector);
			IntBuffer intBuffer = memoryStack.mallocInt(1);
			int j = (int)FreeType.FT_Get_First_Char(face, intBuffer);

			while (true) {
				int k = intBuffer.get(0);
				if (k == 0) {
					return;
				}

				if (!intSet.contains(j)) {
					this.field_52695.put(j, new TrueTypeFont.class_9908(k));
				}

				j = (int)FreeType.FT_Get_Next_Char(face, (long)j, intBuffer);
			}
		}
	}

	@Nullable
	@Override
	public Glyph getGlyph(int codePoint) {
		TrueTypeFont.class_9908 lv = this.field_52695.get(codePoint);
		return lv != null ? this.method_61901(codePoint, lv) : null;
	}

	private Glyph method_61901(int i, TrueTypeFont.class_9908 arg) {
		Glyph glyph = arg.field_52697;
		if (glyph == null) {
			FT_Face fT_Face = this.getInfo();
			synchronized (fT_Face) {
				glyph = arg.field_52697;
				if (glyph == null) {
					glyph = this.method_61902(i, fT_Face, arg.field_52696);
					arg.field_52697 = glyph;
				}
			}
		}

		return glyph;
	}

	private Glyph method_61902(int i, FT_Face fT_Face, int j) {
		int k = FreeType.FT_Load_Glyph(fT_Face, j, 4194312);
		if (k != 0) {
			FreeTypeUtil.checkFatalError(k, String.format(Locale.ROOT, "Loading glyph U+%06X", i));
		}

		FT_GlyphSlot fT_GlyphSlot = fT_Face.glyph();
		if (fT_GlyphSlot == null) {
			throw new NullPointerException(String.format(Locale.ROOT, "Glyph U+%06X not initialized", i));
		} else {
			float f = FreeTypeUtil.getX(fT_GlyphSlot.advance());
			FT_Bitmap fT_Bitmap = fT_GlyphSlot.bitmap();
			int l = fT_GlyphSlot.bitmap_left();
			int m = fT_GlyphSlot.bitmap_top();
			int n = fT_Bitmap.width();
			int o = fT_Bitmap.rows();
			return (Glyph)(n > 0 && o > 0 ? new TrueTypeFont.TtfGlyph((float)l, (float)m, n, o, f, j) : () -> f / this.oversample);
		}
	}

	FT_Face getInfo() {
		if (this.buffer != null && this.face != null) {
			return this.face;
		} else {
			throw new IllegalStateException("Provider already closed");
		}
	}

	@Override
	public void close() {
		if (this.face != null) {
			synchronized (FreeTypeUtil.LOCK) {
				FreeTypeUtil.checkError(FreeType.FT_Done_Face(this.face), "Deleting face");
			}

			this.face = null;
		}

		MemoryUtil.memFree(this.buffer);
		this.buffer = null;
	}

	@Override
	public IntSet getProvidedGlyphs() {
		return this.field_52695.getProvidedGlyphs();
	}

	@Environment(EnvType.CLIENT)
	class TtfGlyph implements Glyph {
		final int width;
		final int height;
		final float bearingX;
		final float ascent;
		private final float advance;
		final int glyphIndex;

		TtfGlyph(final float bearingX, final float ascent, final int width, final int height, final float advance, final int glyphIndex) {
			this.width = width;
			this.height = height;
			this.advance = advance / TrueTypeFont.this.oversample;
			this.bearingX = bearingX / TrueTypeFont.this.oversample;
			this.ascent = ascent / TrueTypeFont.this.oversample;
			this.glyphIndex = glyphIndex;
		}

		@Override
		public float getAdvance() {
			return this.advance;
		}

		@Override
		public GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> function) {
			return (GlyphRenderer)function.apply(new RenderableGlyph() {
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
					FT_Face fT_Face = TrueTypeFont.this.getInfo();
					NativeImage nativeImage = new NativeImage(NativeImage.Format.LUMINANCE, TtfGlyph.this.width, TtfGlyph.this.height, false);
					if (nativeImage.makeGlyphBitmapSubpixel(fT_Face, TtfGlyph.this.glyphIndex)) {
						nativeImage.upload(0, x, y, 0, 0, TtfGlyph.this.width, TtfGlyph.this.height, false, true);
					} else {
						nativeImage.close();
					}
				}

				@Override
				public boolean hasColor() {
					return false;
				}
			});
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_9908 {
		final int field_52696;
		@Nullable
		volatile Glyph field_52697;

		class_9908(int i) {
			this.field_52696 = i;
		}
	}
}
