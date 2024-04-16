package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
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
	private final IntSet excludedCharacters = new IntArraySet();

	public TrueTypeFont(ByteBuffer buffer, FT_Face face, float size, float oversample, float shiftX, float shiftY, String excludedCharacters) {
		this.buffer = buffer;
		this.face = face;
		this.oversample = oversample;
		excludedCharacters.codePoints().forEach(this.excludedCharacters::add);
		int i = Math.round(size * oversample);
		FreeType.FT_Set_Pixel_Sizes(face, i, i);
		float f = shiftX * oversample;
		float g = -shiftY * oversample;

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			FT_Vector fT_Vector = FreeTypeUtil.set(FT_Vector.malloc(memoryStack), f, g);
			FreeType.FT_Set_Transform(face, null, fT_Vector);
		}
	}

	@Nullable
	@Override
	public Glyph getGlyph(int codePoint) {
		FT_Face fT_Face = this.getInfo();
		if (this.excludedCharacters.contains(codePoint)) {
			return null;
		} else {
			int i = FreeType.FT_Get_Char_Index(fT_Face, (long)codePoint);
			if (i == 0) {
				return null;
			} else {
				FreeTypeUtil.checkFatalError(FreeType.FT_Load_Glyph(fT_Face, i, 4194312), "Loading glyph");
				FT_GlyphSlot fT_GlyphSlot = (FT_GlyphSlot)Objects.requireNonNull(fT_Face.glyph(), "Glyph not initialized");
				float f = FreeTypeUtil.getX(fT_GlyphSlot.advance());
				FT_Bitmap fT_Bitmap = fT_GlyphSlot.bitmap();
				int j = fT_GlyphSlot.bitmap_left();
				int k = fT_GlyphSlot.bitmap_top();
				int l = fT_Bitmap.width();
				int m = fT_Bitmap.rows();
				return (Glyph)(l > 0 && m > 0 ? new TrueTypeFont.TtfGlyph((float)j, (float)k, l, m, f, i) : () -> f / this.oversample);
			}
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
		FT_Face fT_Face = this.getInfo();
		IntSet intSet = new IntOpenHashSet();

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			IntBuffer intBuffer = memoryStack.mallocInt(1);

			for (long l = FreeType.FT_Get_First_Char(fT_Face, intBuffer); intBuffer.get(0) != 0; l = FreeType.FT_Get_Next_Char(fT_Face, l, intBuffer)) {
				intSet.add((int)l);
			}
		}

		intSet.removeAll(this.excludedCharacters);
		return intSet;
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
}
