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
import net.minecraft.class_9111;
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
	private FT_Face field_48383;
	final float oversample;
	private final IntSet excludedCharacters = new IntArraySet();

	public TrueTypeFont(ByteBuffer buffer, FT_Face fT_Face, float f, float oversample, float g, float shiftY, String excludedCharacters) {
		this.buffer = buffer;
		this.field_48383 = fT_Face;
		this.oversample = oversample;
		excludedCharacters.codePoints().forEach(this.excludedCharacters::add);
		int i = Math.round(f * oversample);
		FreeType.FT_Set_Pixel_Sizes(fT_Face, i, i);
		float h = g * oversample;
		float j = -shiftY * oversample;

		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			FT_Vector fT_Vector = class_9111.method_56147(FT_Vector.malloc(memoryStack), h, j);
			FreeType.FT_Set_Transform(fT_Face, null, fT_Vector);
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
				class_9111.method_56145(FreeType.FT_Load_Glyph(fT_Face, i, 4194312), "Loading glyph");
				FT_GlyphSlot fT_GlyphSlot = (FT_GlyphSlot)Objects.requireNonNull(fT_Face.glyph(), "Glyph not initialized");
				float f = class_9111.method_56146(fT_GlyphSlot.advance());
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
		if (this.buffer != null && this.field_48383 != null) {
			return this.field_48383;
		} else {
			throw new IllegalStateException("Provider already closed");
		}
	}

	@Override
	public void close() {
		if (this.field_48383 != null) {
			class_9111.method_56145(FreeType.FT_Done_Face(this.field_48383), "Deleting face");
			this.field_48383 = null;
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

		TtfGlyph(float f, float g, int y2, int y1, float advance, int i) {
			this.width = y2;
			this.height = y1;
			this.advance = advance / TrueTypeFont.this.oversample;
			this.bearingX = f / TrueTypeFont.this.oversample;
			this.ascent = g / TrueTypeFont.this.oversample;
			this.glyphIndex = i;
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
				public float method_56129() {
					return TtfGlyph.this.bearingX;
				}

				@Override
				public float method_56130() {
					return TtfGlyph.this.ascent;
				}

				@Override
				public void upload(int x, int y) {
					FT_Face fT_Face = TrueTypeFont.this.getInfo();
					NativeImage nativeImage = new NativeImage(NativeImage.Format.LUMINANCE, TtfGlyph.this.width, TtfGlyph.this.height, false);
					nativeImage.makeGlyphBitmapSubpixel(fT_Face, TtfGlyph.this.glyphIndex);
					nativeImage.upload(0, x, y, 0, 0, TtfGlyph.this.width, TtfGlyph.this.height, false, true);
				}

				@Override
				public boolean hasColor() {
					return false;
				}
			});
		}
	}
}
