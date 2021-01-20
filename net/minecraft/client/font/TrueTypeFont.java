/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class TrueTypeFont
implements Font {
    private final ByteBuffer field_21839;
    private final STBTTFontinfo info;
    private final float oversample;
    private final IntSet excludedCharacters = new IntArraySet();
    private final float shiftX;
    private final float shiftY;
    private final float scaleFactor;
    private final float ascent;

    public TrueTypeFont(ByteBuffer byteBuffer, STBTTFontinfo info, float f, float oversample, float g, float h, String string) {
        this.field_21839 = byteBuffer;
        this.info = info;
        this.oversample = oversample;
        string.codePoints().forEach(this.excludedCharacters::add);
        this.shiftX = g * oversample;
        this.shiftY = h * oversample;
        this.scaleFactor = STBTruetype.stbtt_ScaleForPixelHeight(info, f * oversample);
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            STBTruetype.stbtt_GetFontVMetrics(info, intBuffer, intBuffer2, intBuffer3);
            this.ascent = (float)intBuffer.get(0) * this.scaleFactor;
        }
    }

    @Override
    @Nullable
    public TtfGlyph getGlyph(int i) {
        if (this.excludedCharacters.contains(i)) {
            return null;
        }
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            IntBuffer intBuffer4 = memoryStack.mallocInt(1);
            int j = STBTruetype.stbtt_FindGlyphIndex(this.info, i);
            if (j == 0) {
                TtfGlyph ttfGlyph = null;
                return ttfGlyph;
            }
            STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(this.info, j, this.scaleFactor, this.scaleFactor, this.shiftX, this.shiftY, intBuffer, intBuffer2, intBuffer3, intBuffer4);
            int k = intBuffer3.get(0) - intBuffer.get(0);
            int l = intBuffer4.get(0) - intBuffer2.get(0);
            if (k == 0 || l == 0) {
                TtfGlyph ttfGlyph = null;
                return ttfGlyph;
            }
            IntBuffer intBuffer5 = memoryStack.mallocInt(1);
            IntBuffer intBuffer6 = memoryStack.mallocInt(1);
            STBTruetype.stbtt_GetGlyphHMetrics(this.info, j, intBuffer5, intBuffer6);
            TtfGlyph ttfGlyph = new TtfGlyph(intBuffer.get(0), intBuffer3.get(0), -intBuffer2.get(0), -intBuffer4.get(0), (float)intBuffer5.get(0) * this.scaleFactor, (float)intBuffer6.get(0) * this.scaleFactor, j);
            return ttfGlyph;
        }
    }

    @Override
    public void close() {
        this.info.free();
        MemoryUtil.memFree(this.field_21839);
    }

    @Override
    public IntSet getProvidedGlyphs() {
        return IntStream.range(0, 65535).filter(i -> !this.excludedCharacters.contains(i)).collect(IntOpenHashSet::new, IntCollection::add, IntCollection::addAll);
    }

    @Override
    @Nullable
    public /* synthetic */ RenderableGlyph getGlyph(int codePoint) {
        return this.getGlyph(codePoint);
    }

    @Environment(value=EnvType.CLIENT)
    class TtfGlyph
    implements RenderableGlyph {
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
            nativeImage.makeGlyphBitmapSubpixel(TrueTypeFont.this.info, this.glyphIndex, this.width, this.height, TrueTypeFont.this.scaleFactor, TrueTypeFont.this.scaleFactor, TrueTypeFont.this.shiftX, TrueTypeFont.this.shiftY, 0, 0);
            nativeImage.upload(0, x, y, 0, 0, this.width, this.height, false, true);
        }

        @Override
        public boolean hasColor() {
            return false;
        }
    }
}

