/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.UntrackMemoryUtil;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public final class NativeImage
implements AutoCloseable {
    private static final Set<StandardOpenOption> WRITE_TO_FILE_OPEN_OPTIONS = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    private final Format format;
    private final int width;
    private final int height;
    private final boolean isStbImage;
    private long pointer;
    private final int sizeBytes;

    public NativeImage(int i, int j, boolean bl) {
        this(Format.RGBA, i, j, bl);
    }

    public NativeImage(Format format, int i, int j, boolean bl) {
        this.format = format;
        this.width = i;
        this.height = j;
        this.sizeBytes = i * j * format.getBytesPerPixel();
        this.isStbImage = false;
        this.pointer = bl ? MemoryUtil.nmemCalloc(1L, this.sizeBytes) : MemoryUtil.nmemAlloc(this.sizeBytes);
    }

    private NativeImage(Format format, int i, int j, boolean bl, long l) {
        this.format = format;
        this.width = i;
        this.height = j;
        this.isStbImage = bl;
        this.pointer = l;
        this.sizeBytes = i * j * format.getBytesPerPixel();
    }

    public String toString() {
        return "NativeImage[" + (Object)((Object)this.format) + " " + this.width + "x" + this.height + "@" + this.pointer + (this.isStbImage ? "S" : "N") + "]";
    }

    public static NativeImage fromInputStream(InputStream inputStream) throws IOException {
        return NativeImage.fromInputStream(Format.RGBA, inputStream);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static NativeImage fromInputStream(@Nullable Format format, InputStream inputStream) throws IOException {
        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = TextureUtil.readResource(inputStream);
            byteBuffer.rewind();
            NativeImage nativeImage = NativeImage.fromByteBuffer(format, byteBuffer);
            return nativeImage;
        } finally {
            MemoryUtil.memFree(byteBuffer);
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static NativeImage fromByteBuffer(ByteBuffer byteBuffer) throws IOException {
        return NativeImage.fromByteBuffer(Format.RGBA, byteBuffer);
    }

    public static NativeImage fromByteBuffer(@Nullable Format format, ByteBuffer byteBuffer) throws IOException {
        if (format != null && !format.method_4338()) {
            throw new UnsupportedOperationException("Don't know how to read format " + (Object)((Object)format));
        }
        if (MemoryUtil.memAddress(byteBuffer) == 0L) {
            throw new IllegalArgumentException("Invalid buffer");
        }
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            ByteBuffer byteBuffer2 = STBImage.stbi_load_from_memory(byteBuffer, intBuffer, intBuffer2, intBuffer3, format == null ? 0 : format.bytesPerPixel);
            if (byteBuffer2 == null) {
                throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            }
            NativeImage nativeImage = new NativeImage(format == null ? Format.method_4336(intBuffer3.get(0)) : format, intBuffer.get(0), intBuffer2.get(0), true, MemoryUtil.memAddress(byteBuffer2));
            return nativeImage;
        }
    }

    private static void setTextureClamp(boolean bl) {
        if (bl) {
            GlStateManager.texParameter(3553, 10242, 10496);
            GlStateManager.texParameter(3553, 10243, 10496);
        } else {
            GlStateManager.texParameter(3553, 10242, 10497);
            GlStateManager.texParameter(3553, 10243, 10497);
        }
    }

    private static void setTextureFilter(boolean bl, boolean bl2) {
        if (bl) {
            GlStateManager.texParameter(3553, 10241, bl2 ? 9987 : 9729);
            GlStateManager.texParameter(3553, 10240, 9729);
        } else {
            GlStateManager.texParameter(3553, 10241, bl2 ? 9986 : 9728);
            GlStateManager.texParameter(3553, 10240, 9728);
        }
    }

    private void checkAllocated() {
        if (this.pointer == 0L) {
            throw new IllegalStateException("Image is not allocated.");
        }
    }

    @Override
    public void close() {
        if (this.pointer != 0L) {
            if (this.isStbImage) {
                STBImage.nstbi_image_free(this.pointer);
            } else {
                MemoryUtil.nmemFree(this.pointer);
            }
        }
        this.pointer = 0L;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Format getFormat() {
        return this.format;
    }

    public int getPixelRGBA(int i, int j) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", new Object[]{this.format}));
        }
        if (i > this.width || j > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.width, this.height));
        }
        this.checkAllocated();
        return MemoryUtil.memIntBuffer(this.pointer, this.sizeBytes).get(i + j * this.width);
    }

    public void setPixelRGBA(int i, int j, int k) {
        if (this.format != Format.RGBA) {
            throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", new Object[]{this.format}));
        }
        if (i > this.width || j > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.width, this.height));
        }
        this.checkAllocated();
        MemoryUtil.memIntBuffer(this.pointer, this.sizeBytes).put(i + j * this.width, k);
    }

    public byte getAlphaOrLuminance(int i, int j) {
        if (!this.format.hasLuminanceOrAlpha()) {
            throw new IllegalArgumentException(String.format("no luminance or alpha in %s", new Object[]{this.format}));
        }
        if (i > this.width || j > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.width, this.height));
        }
        return MemoryUtil.memByteBuffer(this.pointer, this.sizeBytes).get((i + j * this.width) * this.format.getBytesPerPixel() + this.format.method_4330() / 8);
    }

    public void blendPixel(int i, int j, int k) {
        if (this.format != Format.RGBA) {
            throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
        }
        int l = this.getPixelRGBA(i, j);
        float f = (float)(k >> 24 & 0xFF) / 255.0f;
        float g = (float)(k >> 16 & 0xFF) / 255.0f;
        float h = (float)(k >> 8 & 0xFF) / 255.0f;
        float m = (float)(k >> 0 & 0xFF) / 255.0f;
        float n = (float)(l >> 24 & 0xFF) / 255.0f;
        float o = (float)(l >> 16 & 0xFF) / 255.0f;
        float p = (float)(l >> 8 & 0xFF) / 255.0f;
        float q = (float)(l >> 0 & 0xFF) / 255.0f;
        float r = f;
        float s = 1.0f - f;
        float t = f * r + n * s;
        float u = g * r + o * s;
        float v = h * r + p * s;
        float w = m * r + q * s;
        if (t > 1.0f) {
            t = 1.0f;
        }
        if (u > 1.0f) {
            u = 1.0f;
        }
        if (v > 1.0f) {
            v = 1.0f;
        }
        if (w > 1.0f) {
            w = 1.0f;
        }
        int x = (int)(t * 255.0f);
        int y = (int)(u * 255.0f);
        int z = (int)(v * 255.0f);
        int aa = (int)(w * 255.0f);
        this.setPixelRGBA(i, j, x << 24 | y << 16 | z << 8 | aa << 0);
    }

    @Deprecated
    public int[] makePixelArray() {
        if (this.format != Format.RGBA) {
            throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
        }
        this.checkAllocated();
        int[] is = new int[this.getWidth() * this.getHeight()];
        for (int i = 0; i < this.getHeight(); ++i) {
            for (int j = 0; j < this.getWidth(); ++j) {
                int p;
                int k = this.getPixelRGBA(j, i);
                int l = k >> 24 & 0xFF;
                int m = k >> 16 & 0xFF;
                int n = k >> 8 & 0xFF;
                int o = k >> 0 & 0xFF;
                is[j + i * this.getWidth()] = p = l << 24 | o << 16 | n << 8 | m;
            }
        }
        return is;
    }

    public void upload(int i, int j, int k, boolean bl) {
        this.upload(i, j, k, 0, 0, this.width, this.height, bl);
    }

    public void upload(int i, int j, int k, int l, int m, int n, int o, boolean bl) {
        this.upload(i, j, k, l, m, n, o, false, false, bl);
    }

    public void upload(int i, int j, int k, int l, int m, int n, int o, boolean bl, boolean bl2, boolean bl3) {
        this.checkAllocated();
        NativeImage.setTextureFilter(bl, bl3);
        NativeImage.setTextureClamp(bl2);
        if (n == this.getWidth()) {
            GlStateManager.pixelStore(3314, 0);
        } else {
            GlStateManager.pixelStore(3314, this.getWidth());
        }
        GlStateManager.pixelStore(3316, l);
        GlStateManager.pixelStore(3315, m);
        this.format.setUnpackAlignment();
        GlStateManager.texSubImage2D(3553, i, j, k, n, o, this.format.getPixelDataFormat(), 5121, this.pointer);
    }

    public void loadFromTextureImage(int i, boolean bl) {
        this.checkAllocated();
        this.format.setPackAlignment();
        GlStateManager.getTexImage(3553, i, this.format.getPixelDataFormat(), 5121, this.pointer);
        if (bl && this.format.method_4329()) {
            for (int j = 0; j < this.getHeight(); ++j) {
                for (int k = 0; k < this.getWidth(); ++k) {
                    this.setPixelRGBA(k, j, this.getPixelRGBA(k, j) | 255 << this.format.method_4332());
                }
            }
        }
    }

    public void method_4306(boolean bl) {
        this.checkAllocated();
        this.format.setPackAlignment();
        if (bl) {
            GlStateManager.pixelTransfer(3357, Float.MAX_VALUE);
        }
        GlStateManager.readPixels(0, 0, this.width, this.height, this.format.getPixelDataFormat(), 5121, this.pointer);
        if (bl) {
            GlStateManager.pixelTransfer(3357, 0.0f);
        }
    }

    public void writeFile(String string) throws IOException {
        this.writeFile(FileSystems.getDefault().getPath(string, new String[0]));
    }

    public void writeFile(File file) throws IOException {
        this.writeFile(file.toPath());
    }

    public void makeGlyphBitmapSubpixel(STBTTFontinfo sTBTTFontinfo, int i, int j, int k, float f, float g, float h, float l, int m, int n) {
        if (m < 0 || m + j > this.getWidth() || n < 0 || n + k > this.getHeight()) {
            throw new IllegalArgumentException(String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", m, n, j, k, this.getWidth(), this.getHeight()));
        }
        if (this.format.getBytesPerPixel() != 1) {
            throw new IllegalArgumentException("Can only write fonts into 1-component images.");
        }
        STBTruetype.nstbtt_MakeGlyphBitmapSubpixel(sTBTTFontinfo.address(), this.pointer + (long)m + (long)(n * this.getWidth()), j, k, this.getWidth(), f, g, h, l, i);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void writeFile(Path path) throws IOException {
        if (!this.format.method_4338()) {
            throw new UnsupportedOperationException("Don't know how to write format " + (Object)((Object)this.format));
        }
        this.checkAllocated();
        try (SeekableByteChannel writableByteChannel = Files.newByteChannel(path, WRITE_TO_FILE_OPEN_OPTIONS, new FileAttribute[0]);){
            WriteCallback writeCallback = new WriteCallback(writableByteChannel);
            try {
                if (!STBImageWrite.stbi_write_png_to_func(writeCallback, 0L, this.getWidth(), this.getHeight(), this.format.getBytesPerPixel(), MemoryUtil.memByteBuffer(this.pointer, this.sizeBytes), 0)) {
                    throw new IOException("Could not write image to the PNG file \"" + path.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
                }
            } finally {
                writeCallback.free();
            }
            writeCallback.throwStoredException();
        }
    }

    public void copyFrom(NativeImage nativeImage) {
        if (nativeImage.getFormat() != this.format) {
            throw new UnsupportedOperationException("Image formats don't match.");
        }
        int i = this.format.getBytesPerPixel();
        this.checkAllocated();
        nativeImage.checkAllocated();
        if (this.width == nativeImage.width) {
            MemoryUtil.memCopy(nativeImage.pointer, this.pointer, Math.min(this.sizeBytes, nativeImage.sizeBytes));
        } else {
            int j = Math.min(this.getWidth(), nativeImage.getWidth());
            int k = Math.min(this.getHeight(), nativeImage.getHeight());
            for (int l = 0; l < k; ++l) {
                int m = l * nativeImage.getWidth() * i;
                int n = l * this.getWidth() * i;
                MemoryUtil.memCopy(nativeImage.pointer + (long)m, this.pointer + (long)n, j);
            }
        }
    }

    public void fillRGBA(int i, int j, int k, int l, int m) {
        for (int n = j; n < j + l; ++n) {
            for (int o = i; o < i + k; ++o) {
                this.setPixelRGBA(o, n, m);
            }
        }
    }

    public void method_4304(int i, int j, int k, int l, int m, int n, boolean bl, boolean bl2) {
        for (int o = 0; o < n; ++o) {
            for (int p = 0; p < m; ++p) {
                int q = bl ? m - 1 - p : p;
                int r = bl2 ? n - 1 - o : o;
                int s = this.getPixelRGBA(i + p, j + o);
                this.setPixelRGBA(i + k + q, j + l + r, s);
            }
        }
    }

    public void method_4319() {
        this.checkAllocated();
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            int i = this.format.getBytesPerPixel();
            int j = this.getWidth() * i;
            long l = memoryStack.nmalloc(j);
            for (int k = 0; k < this.getHeight() / 2; ++k) {
                int m = k * this.getWidth() * i;
                int n = (this.getHeight() - 1 - k) * this.getWidth() * i;
                MemoryUtil.memCopy(this.pointer + (long)m, l, j);
                MemoryUtil.memCopy(this.pointer + (long)n, this.pointer + (long)m, j);
                MemoryUtil.memCopy(l, this.pointer + (long)n, j);
            }
        }
    }

    public void resizeSubRectTo(int i, int j, int k, int l, NativeImage nativeImage) {
        this.checkAllocated();
        if (nativeImage.getFormat() != this.format) {
            throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
        }
        int m = this.format.getBytesPerPixel();
        STBImageResize.nstbir_resize_uint8(this.pointer + (long)((i + j * this.getWidth()) * m), k, l, this.getWidth() * m, nativeImage.pointer, nativeImage.getWidth(), nativeImage.getHeight(), 0, m);
    }

    public void untrack() {
        UntrackMemoryUtil.untrack(this.pointer);
    }

    public static NativeImage fromBase64(String string) throws IOException {
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = memoryStack.UTF8(string.replaceAll("\n", ""), false);
            ByteBuffer byteBuffer2 = Base64.getDecoder().decode(byteBuffer);
            ByteBuffer byteBuffer3 = memoryStack.malloc(byteBuffer2.remaining());
            byteBuffer3.put(byteBuffer2);
            byteBuffer3.rewind();
            NativeImage nativeImage = NativeImage.fromByteBuffer(byteBuffer3);
            return nativeImage;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Format {
        RGBA(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
        RGB(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
        LUMINANCE_ALPHA(2, 6410, false, false, false, true, true, 255, 255, 255, 0, 8, true),
        LUMINANCE(1, 6409, false, false, false, true, false, 0, 0, 0, 0, 255, true);

        private final int bytesPerPixel;
        private final int pixelDataFormat;
        private final boolean field_5005;
        private final boolean field_5004;
        private final boolean field_5003;
        private final boolean field_5000;
        private final boolean field_4999;
        private final int field_5010;
        private final int field_5009;
        private final int field_5008;
        private final int field_5007;
        private final int field_5006;
        private final boolean field_4996;

        private Format(int j, int k, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, int l, int m, int n, int o, int p, boolean bl6) {
            this.bytesPerPixel = j;
            this.pixelDataFormat = k;
            this.field_5005 = bl;
            this.field_5004 = bl2;
            this.field_5003 = bl3;
            this.field_5000 = bl4;
            this.field_4999 = bl5;
            this.field_5010 = l;
            this.field_5009 = m;
            this.field_5008 = n;
            this.field_5007 = o;
            this.field_5006 = p;
            this.field_4996 = bl6;
        }

        public int getBytesPerPixel() {
            return this.bytesPerPixel;
        }

        public void setPackAlignment() {
            GlStateManager.pixelStore(3333, this.getBytesPerPixel());
        }

        public void setUnpackAlignment() {
            GlStateManager.pixelStore(3317, this.getBytesPerPixel());
        }

        public int getPixelDataFormat() {
            return this.pixelDataFormat;
        }

        public boolean method_4329() {
            return this.field_4999;
        }

        public int method_4332() {
            return this.field_5006;
        }

        public boolean hasLuminanceOrAlpha() {
            return this.field_5000 || this.field_4999;
        }

        public int method_4330() {
            return this.field_5000 ? this.field_5007 : this.field_5006;
        }

        public boolean method_4338() {
            return this.field_4996;
        }

        private static Format method_4336(int i) {
            switch (i) {
                case 1: {
                    return LUMINANCE;
                }
                case 2: {
                    return LUMINANCE_ALPHA;
                }
                case 3: {
                    return RGB;
                }
            }
            return RGBA;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum class_1013 {
        RGBA(6408),
        RGB(6407),
        LUMINANCE_ALPHA(6410),
        LUMINANCE(6409),
        INTENSITY(32841);

        private final int field_5015;

        private class_1013(int j) {
            this.field_5015 = j;
        }

        public int method_4341() {
            return this.field_5015;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class WriteCallback
    extends STBIWriteCallback {
        private final WritableByteChannel channel;
        private IOException exception;

        private WriteCallback(WritableByteChannel writableByteChannel) {
            this.channel = writableByteChannel;
        }

        @Override
        public void invoke(long l, long m, int i) {
            ByteBuffer byteBuffer = WriteCallback.getData(m, i);
            try {
                this.channel.write(byteBuffer);
            } catch (IOException iOException) {
                this.exception = iOException;
            }
        }

        public void throwStoredException() throws IOException {
            if (this.exception != null) {
                throw this.exception;
            }
        }
    }
}

