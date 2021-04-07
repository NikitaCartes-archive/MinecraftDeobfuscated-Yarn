/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.google.common.base.Charsets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.util.Untracker;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int field_32031 = 24;
    private static final int field_32032 = 16;
    private static final int field_32033 = 8;
    private static final int field_32034 = 0;
    private static final Set<StandardOpenOption> WRITE_TO_FILE_OPEN_OPTIONS = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    private final Format format;
    private final int width;
    private final int height;
    private final boolean isStbImage;
    private long pointer;
    private final long sizeBytes;

    public NativeImage(int width, int height, boolean useStb) {
        this(Format.ABGR, width, height, useStb);
    }

    public NativeImage(Format format, int width, int height, boolean useStb) {
        this.format = format;
        this.width = width;
        this.height = height;
        this.sizeBytes = (long)width * (long)height * (long)format.getChannelCount();
        this.isStbImage = false;
        this.pointer = useStb ? MemoryUtil.nmemCalloc(1L, this.sizeBytes) : MemoryUtil.nmemAlloc(this.sizeBytes);
    }

    private NativeImage(Format format, int width, int height, boolean useStb, long pointer) {
        this.format = format;
        this.width = width;
        this.height = height;
        this.isStbImage = useStb;
        this.pointer = pointer;
        this.sizeBytes = width * height * format.getChannelCount();
    }

    public String toString() {
        return "NativeImage[" + (Object)((Object)this.format) + " " + this.width + "x" + this.height + "@" + this.pointer + (this.isStbImage ? "S" : "N") + "]";
    }

    public static NativeImage read(InputStream inputStream) throws IOException {
        return NativeImage.read(Format.ABGR, inputStream);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static NativeImage read(@Nullable Format format, InputStream inputStream) throws IOException {
        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = TextureUtil.readResource(inputStream);
            byteBuffer.rewind();
            NativeImage nativeImage = NativeImage.read(format, byteBuffer);
            return nativeImage;
        } finally {
            MemoryUtil.memFree(byteBuffer);
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static NativeImage read(ByteBuffer byteBuffer) throws IOException {
        return NativeImage.read(Format.ABGR, byteBuffer);
    }

    public static NativeImage read(@Nullable Format format, ByteBuffer byteBuffer) throws IOException {
        if (format != null && !format.isWriteable()) {
            throw new UnsupportedOperationException("Don't know how to read format " + (Object)((Object)format));
        }
        if (MemoryUtil.memAddress(byteBuffer) == 0L) {
            throw new IllegalArgumentException("Invalid buffer");
        }
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
            ByteBuffer byteBuffer2 = STBImage.stbi_load_from_memory(byteBuffer, intBuffer, intBuffer2, intBuffer3, format == null ? 0 : format.channelCount);
            if (byteBuffer2 == null) {
                throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            }
            NativeImage nativeImage = new NativeImage(format == null ? Format.getFormat(intBuffer3.get(0)) : format, intBuffer.get(0), intBuffer2.get(0), true, MemoryUtil.memAddress(byteBuffer2));
            return nativeImage;
        }
    }

    private static void setTextureFilter(boolean blur, boolean mipmap) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (blur) {
            GlStateManager._texParameter(3553, 10241, mipmap ? 9987 : 9729);
            GlStateManager._texParameter(3553, 10240, 9729);
        } else {
            GlStateManager._texParameter(3553, 10241, mipmap ? 9986 : 9728);
            GlStateManager._texParameter(3553, 10240, 9728);
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

    /**
     * Gets the color of a pixel on this native image.
     * The color returned by this method will be in a ABGR format.
     * 
     * <p>This is only supported when this native image's format is {@link NativeImage.Format#ABGR ABGR}.
     */
    public int getPixelColor(int x, int y) {
        if (this.format != Format.ABGR) {
            throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", new Object[]{this.format}));
        }
        if (x > this.width || y > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
        }
        this.checkAllocated();
        long l = (x + y * this.width) * 4;
        return MemoryUtil.memGetInt(this.pointer + l);
    }

    /**
     * Sets the color of a pixel on this native image.
     * The color to be set using this method should be in a ABGR format.
     * 
     * <p>This is only supported when this native image's format is {@link NativeImage.Format#ABGR ABGR}
     */
    public void setPixelColor(int x, int y, int color) {
        if (this.format != Format.ABGR) {
            throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", new Object[]{this.format}));
        }
        if (x > this.width || y > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
        }
        this.checkAllocated();
        long l = (x + y * this.width) * 4;
        MemoryUtil.memPutInt(this.pointer + l, color);
    }

    public void method_35621(int i, int j, byte b) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (!this.format.hasLuminance()) {
            throw new IllegalArgumentException(String.format("setPixelLuminance only works on image with luminance; have %s", new Object[]{this.format}));
        }
        if (i > this.width || j > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.width, this.height));
        }
        this.checkAllocated();
        long l = (i + j * this.width) * this.format.getChannelCount() + this.format.getLuminanceChannelOffset() / 8;
        MemoryUtil.memPutByte(this.pointer + l, b);
    }

    public byte method_35623(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (!this.format.hasRedChannel()) {
            throw new IllegalArgumentException(String.format("no red or luminance in %s", new Object[]{this.format}));
        }
        if (i > this.width || j > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.width, this.height));
        }
        int k = (i + j * this.width) * this.format.getChannelCount() + this.format.getRedOrLuminanceOffset() / 8;
        return MemoryUtil.memGetByte(this.pointer + (long)k);
    }

    public byte method_35625(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (!this.format.hasGreenChannel()) {
            throw new IllegalArgumentException(String.format("no green or luminance in %s", new Object[]{this.format}));
        }
        if (i > this.width || j > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.width, this.height));
        }
        int k = (i + j * this.width) * this.format.getChannelCount() + this.format.getGreenOrLuminanceOffset() / 8;
        return MemoryUtil.memGetByte(this.pointer + (long)k);
    }

    public byte method_35626(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (!this.format.hasBlueChannel()) {
            throw new IllegalArgumentException(String.format("no blue or luminance in %s", new Object[]{this.format}));
        }
        if (i > this.width || j > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", i, j, this.width, this.height));
        }
        int k = (i + j * this.width) * this.format.getChannelCount() + this.format.getBlueOrLuminanceOffset() / 8;
        return MemoryUtil.memGetByte(this.pointer + (long)k);
    }

    public byte getPixelOpacity(int x, int y) {
        if (!this.format.hasOpacityChannel()) {
            throw new IllegalArgumentException(String.format("no luminance or alpha in %s", new Object[]{this.format}));
        }
        if (x > this.width || y > this.height) {
            throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
        }
        int i = (x + y * this.width) * this.format.getChannelCount() + this.format.getOpacityOffset() / 8;
        return MemoryUtil.memGetByte(this.pointer + (long)i);
    }

    public void method_35624(int i, int j, int k) {
        if (this.format != Format.ABGR) {
            throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
        }
        int l = this.getPixelColor(i, j);
        float f = (float)NativeImage.getAlpha(k) / 255.0f;
        float g = (float)NativeImage.getBlue(k) / 255.0f;
        float h = (float)NativeImage.getGreen(k) / 255.0f;
        float m = (float)NativeImage.getRed(k) / 255.0f;
        float n = (float)NativeImage.getAlpha(l) / 255.0f;
        float o = (float)NativeImage.getBlue(l) / 255.0f;
        float p = (float)NativeImage.getGreen(l) / 255.0f;
        float q = (float)NativeImage.getRed(l) / 255.0f;
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
        this.setPixelColor(i, j, NativeImage.getAbgrColor(x, y, z, aa));
    }

    @Deprecated
    public int[] makePixelArray() {
        if (this.format != Format.ABGR) {
            throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
        }
        this.checkAllocated();
        int[] is = new int[this.getWidth() * this.getHeight()];
        for (int i = 0; i < this.getHeight(); ++i) {
            for (int j = 0; j < this.getWidth(); ++j) {
                int p;
                int k = this.getPixelColor(j, i);
                int l = NativeImage.getAlpha(k);
                int m = NativeImage.getBlue(k);
                int n = NativeImage.getGreen(k);
                int o = NativeImage.getRed(k);
                is[j + i * this.getWidth()] = p = l << 24 | o << 16 | n << 8 | m;
            }
        }
        return is;
    }

    public void upload(int level, int offsetX, int offsetY, boolean close) {
        this.upload(level, offsetX, offsetY, 0, 0, this.width, this.height, false, close);
    }

    public void upload(int level, int offsetX, int offsetY, int unpackSkipPixels, int unpackSkipRows, int width, int height, boolean mipmap, boolean close) {
        this.upload(level, offsetX, offsetY, unpackSkipPixels, unpackSkipRows, width, height, false, false, mipmap, close);
    }

    public void upload(int level, int offsetX, int offsetY, int unpackSkipPixels, int unpackSkipRows, int width, int height, boolean blur, boolean clamp, boolean mipmap, boolean close) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> this.uploadInternal(level, offsetX, offsetY, unpackSkipPixels, unpackSkipRows, width, height, blur, clamp, mipmap, close));
        } else {
            this.uploadInternal(level, offsetX, offsetY, unpackSkipPixels, unpackSkipRows, width, height, blur, clamp, mipmap, close);
        }
    }

    private void uploadInternal(int level, int xOffset, int yOffset, int unpackSkipPixels, int unpackSkipRows, int width, int height, boolean blur, boolean clamp, boolean mipmap, boolean close) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.checkAllocated();
        NativeImage.setTextureFilter(blur, mipmap);
        if (width == this.getWidth()) {
            GlStateManager._pixelStore(3314, 0);
        } else {
            GlStateManager._pixelStore(3314, this.getWidth());
        }
        GlStateManager._pixelStore(3316, unpackSkipPixels);
        GlStateManager._pixelStore(3315, unpackSkipRows);
        this.format.setUnpackAlignment();
        GlStateManager._texSubImage2D(3553, level, xOffset, yOffset, width, height, this.format.getPixelDataFormat(), 5121, this.pointer);
        if (clamp) {
            GlStateManager._texParameter(3553, 10242, 33071);
            GlStateManager._texParameter(3553, 10243, 33071);
        }
        if (close) {
            this.close();
        }
    }

    public void loadFromTextureImage(int level, boolean removeAlpha) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        this.checkAllocated();
        this.format.setPackAlignment();
        GlStateManager._getTexImage(3553, level, this.format.getPixelDataFormat(), 5121, this.pointer);
        if (removeAlpha && this.format.hasAlphaChannel()) {
            for (int i = 0; i < this.getHeight(); ++i) {
                for (int j = 0; j < this.getWidth(); ++j) {
                    this.setPixelColor(j, i, this.getPixelColor(j, i) | 255 << this.format.getAlphaChannelOffset());
                }
            }
        }
    }

    public void method_35620(float f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (this.format.getChannelCount() != 1) {
            throw new IllegalStateException("Depth buffer must be stored in NativeImage with 1 component.");
        }
        this.checkAllocated();
        this.format.setPackAlignment();
        GlStateManager._readPixels(0, 0, this.width, this.height, 6402, 5121, this.pointer);
    }

    public void method_35627() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        this.format.setUnpackAlignment();
        GlStateManager._glDrawPixels(this.width, this.height, this.format.getPixelDataFormat(), 5121, this.pointer);
    }

    public void method_35622(String string) throws IOException {
        this.writeFile(FileSystems.getDefault().getPath(string, new String[0]));
    }

    public void writeFile(File file) throws IOException {
        this.writeFile(file.toPath());
    }

    public void makeGlyphBitmapSubpixel(STBTTFontinfo fontInfo, int glyphIndex, int width, int height, float scaleX, float scaleY, float shiftX, float shiftY, int startX, int startY) {
        if (startX < 0 || startX + width > this.getWidth() || startY < 0 || startY + height > this.getHeight()) {
            throw new IllegalArgumentException(String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", startX, startY, width, height, this.getWidth(), this.getHeight()));
        }
        if (this.format.getChannelCount() != 1) {
            throw new IllegalArgumentException("Can only write fonts into 1-component images.");
        }
        STBTruetype.nstbtt_MakeGlyphBitmapSubpixel(fontInfo.address(), this.pointer + (long)startX + (long)(startY * this.getWidth()), width, height, this.getWidth(), scaleX, scaleY, shiftX, shiftY, glyphIndex);
    }

    public void writeFile(Path path) throws IOException {
        if (!this.format.isWriteable()) {
            throw new UnsupportedOperationException("Don't know how to write format " + (Object)((Object)this.format));
        }
        this.checkAllocated();
        try (SeekableByteChannel writableByteChannel = Files.newByteChannel(path, WRITE_TO_FILE_OPEN_OPTIONS, new FileAttribute[0]);){
            if (!this.write(writableByteChannel)) {
                throw new IOException("Could not write image to the PNG file \"" + path.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
            }
        }
    }

    /*
     * Exception decompiling
     */
    public byte[] getBytes() throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:538)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:261)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:143)
         *     at net.fabricmc.loom.decompilers.cfr.LoomCFRDecompiler.decompile(LoomCFRDecompiler.java:89)
         *     at net.fabricmc.loom.task.GenerateSourcesTask$DecompileAction.doDecompile(GenerateSourcesTask.java:269)
         *     at net.fabricmc.loom.task.GenerateSourcesTask$DecompileAction.execute(GenerateSourcesTask.java:234)
         *     at org.gradle.workers.internal.DefaultWorkerServer.execute(DefaultWorkerServer.java:63)
         *     at org.gradle.workers.internal.AbstractClassLoaderWorker$1.create(AbstractClassLoaderWorker.java:49)
         *     at org.gradle.workers.internal.AbstractClassLoaderWorker$1.create(AbstractClassLoaderWorker.java:43)
         *     at org.gradle.internal.classloader.ClassLoaderUtils.executeInClassloader(ClassLoaderUtils.java:100)
         *     at org.gradle.workers.internal.AbstractClassLoaderWorker.executeInClassLoader(AbstractClassLoaderWorker.java:43)
         *     at org.gradle.workers.internal.IsolatedClassloaderWorker.run(IsolatedClassloaderWorker.java:49)
         *     at org.gradle.workers.internal.IsolatedClassloaderWorker.run(IsolatedClassloaderWorker.java:30)
         *     at org.gradle.workers.internal.WorkerDaemonServer.run(WorkerDaemonServer.java:87)
         *     at org.gradle.workers.internal.WorkerDaemonServer.run(WorkerDaemonServer.java:56)
         *     at org.gradle.process.internal.worker.request.WorkerAction$1.call(WorkerAction.java:138)
         *     at org.gradle.process.internal.worker.child.WorkerLogEventListener.withWorkerLoggingProtocol(WorkerLogEventListener.java:41)
         *     at org.gradle.process.internal.worker.request.WorkerAction.run(WorkerAction.java:135)
         *     at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
         *     at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
         *     at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
         *     at java.base/java.lang.reflect.Method.invoke(Method.java:568)
         *     at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:36)
         *     at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
         *     at org.gradle.internal.remote.internal.hub.MessageHubBackedObjectConnection$DispatchWrapper.dispatch(MessageHubBackedObjectConnection.java:182)
         *     at org.gradle.internal.remote.internal.hub.MessageHubBackedObjectConnection$DispatchWrapper.dispatch(MessageHubBackedObjectConnection.java:164)
         *     at org.gradle.internal.remote.internal.hub.MessageHub$Handler.run(MessageHub.java:414)
         *     at org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:64)
         *     at org.gradle.internal.concurrent.ManagedExecutorImpl$1.run(ManagedExecutorImpl.java:49)
         *     at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
         *     at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
         *     at java.base/java.lang.Thread.run(Thread.java:833)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean write(WritableByteChannel writableByteChannel) throws IOException {
        WriteCallback writeCallback = new WriteCallback(writableByteChannel);
        try {
            int i = Math.min(this.getHeight(), Integer.MAX_VALUE / this.getWidth() / this.format.getChannelCount());
            if (i < this.getHeight()) {
                LOGGER.warn("Dropping image height from {} to {} to fit the size into 32-bit signed int", (Object)this.getHeight(), (Object)i);
            }
            if (STBImageWrite.nstbi_write_png_to_func(writeCallback.address(), 0L, this.getWidth(), i, this.format.getChannelCount(), this.pointer, 0) == 0) {
                boolean bl = false;
                return bl;
            }
            writeCallback.throwStoredException();
            boolean bl = true;
            return bl;
        } finally {
            writeCallback.free();
        }
    }

    public void copyFrom(NativeImage image) {
        if (image.getFormat() != this.format) {
            throw new UnsupportedOperationException("Image formats don't match.");
        }
        int i = this.format.getChannelCount();
        this.checkAllocated();
        image.checkAllocated();
        if (this.width == image.width) {
            MemoryUtil.memCopy(image.pointer, this.pointer, Math.min(this.sizeBytes, image.sizeBytes));
        } else {
            int j = Math.min(this.getWidth(), image.getWidth());
            int k = Math.min(this.getHeight(), image.getHeight());
            for (int l = 0; l < k; ++l) {
                int m = l * image.getWidth() * i;
                int n = l * this.getWidth() * i;
                MemoryUtil.memCopy(image.pointer + (long)m, this.pointer + (long)n, j);
            }
        }
    }

    public void fillRect(int x, int y, int width, int height, int color) {
        for (int i = y; i < y + height; ++i) {
            for (int j = x; j < x + width; ++j) {
                this.setPixelColor(j, i, color);
            }
        }
    }

    public void copyRect(int x, int y, int translateX, int translateY, int width, int height, boolean flipX, boolean flipY) {
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int k = flipX ? width - 1 - j : j;
                int l = flipY ? height - 1 - i : i;
                int m = this.getPixelColor(x + j, y + i);
                this.setPixelColor(x + translateX + k, y + translateY + l, m);
            }
        }
    }

    public void mirrorVertically() {
        this.checkAllocated();
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            int i = this.format.getChannelCount();
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

    public void resizeSubRectTo(int x, int y, int width, int height, NativeImage targetImage) {
        this.checkAllocated();
        if (targetImage.getFormat() != this.format) {
            throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
        }
        int i = this.format.getChannelCount();
        STBImageResize.nstbir_resize_uint8(this.pointer + (long)((x + y * this.getWidth()) * i), width, height, this.getWidth() * i, targetImage.pointer, targetImage.getWidth(), targetImage.getHeight(), 0, i);
    }

    public void untrack() {
        Untracker.untrack(this.pointer);
    }

    public static NativeImage read(String dataUri) throws IOException {
        byte[] bs = Base64.getDecoder().decode(dataUri.replaceAll("\n", "").getBytes(Charsets.UTF_8));
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = memoryStack.malloc(bs.length);
            byteBuffer.put(bs);
            byteBuffer.rewind();
            NativeImage nativeImage = NativeImage.read(byteBuffer);
            return nativeImage;
        }
    }

    public static int getAlpha(int color) {
        return color >> 24 & 0xFF;
    }

    public static int getRed(int color) {
        return color >> 0 & 0xFF;
    }

    public static int getGreen(int color) {
        return color >> 8 & 0xFF;
    }

    public static int getBlue(int color) {
        return color >> 16 & 0xFF;
    }

    /**
     * The resulting color of this operation is stored as least to most significant bits.
     */
    public static int getAbgrColor(int alpha, int blue, int green, int red) {
        return (alpha & 0xFF) << 24 | (blue & 0xFF) << 16 | (green & 0xFF) << 8 | (red & 0xFF) << 0;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Format {
        ABGR(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
        BGR(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
        LUMINANCE_ALPHA(2, 6410, false, false, false, true, true, 255, 255, 255, 0, 8, true),
        LUMINANCE(1, 6409, false, false, false, true, false, 0, 0, 0, 0, 255, true);

        private final int channelCount;
        private final int pixelDataFormat;
        private final boolean hasRed;
        private final boolean hasGreen;
        private final boolean hasBlue;
        private final boolean hasLuminance;
        private final boolean hasAlpha;
        private final int redOffset;
        private final int greenOffset;
        private final int blueOffset;
        private final int luminanceChannelOffset;
        private final int alphaChannelOffset;
        private final boolean writeable;

        private Format(int channels, int glFormat, boolean hasRed, boolean hasGreen, boolean hasBlue, boolean hasLuminance, boolean hasAlpha, int redOffset, int greenOffset, int blueOffset, int luminanceOffset, int alphaOffset, boolean writeable) {
            this.channelCount = channels;
            this.pixelDataFormat = glFormat;
            this.hasRed = hasRed;
            this.hasGreen = hasGreen;
            this.hasBlue = hasBlue;
            this.hasLuminance = hasLuminance;
            this.hasAlpha = hasAlpha;
            this.redOffset = redOffset;
            this.greenOffset = greenOffset;
            this.blueOffset = blueOffset;
            this.luminanceChannelOffset = luminanceOffset;
            this.alphaChannelOffset = alphaOffset;
            this.writeable = writeable;
        }

        public int getChannelCount() {
            return this.channelCount;
        }

        public void setPackAlignment() {
            RenderSystem.assertThread(RenderSystem::isOnRenderThread);
            GlStateManager._pixelStore(3333, this.getChannelCount());
        }

        public void setUnpackAlignment() {
            RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
            GlStateManager._pixelStore(3317, this.getChannelCount());
        }

        public int getPixelDataFormat() {
            return this.pixelDataFormat;
        }

        public boolean hasRed() {
            return this.hasRed;
        }

        public boolean hasGreen() {
            return this.hasGreen;
        }

        public boolean hasBlue() {
            return this.hasBlue;
        }

        public boolean hasLuminance() {
            return this.hasLuminance;
        }

        public boolean hasAlphaChannel() {
            return this.hasAlpha;
        }

        public int getRedOffset() {
            return this.redOffset;
        }

        public int getGreenOffset() {
            return this.greenOffset;
        }

        public int getBlueOffset() {
            return this.blueOffset;
        }

        public int getLuminanceChannelOffset() {
            return this.luminanceChannelOffset;
        }

        public int getAlphaChannelOffset() {
            return this.alphaChannelOffset;
        }

        public boolean hasRedChannel() {
            return this.hasLuminance || this.hasRed;
        }

        public boolean hasGreenChannel() {
            return this.hasLuminance || this.hasGreen;
        }

        public boolean hasBlueChannel() {
            return this.hasLuminance || this.hasBlue;
        }

        public boolean hasOpacityChannel() {
            return this.hasLuminance || this.hasAlpha;
        }

        public int getRedOrLuminanceOffset() {
            return this.hasLuminance ? this.luminanceChannelOffset : this.redOffset;
        }

        public int getGreenOrLuminanceOffset() {
            return this.hasLuminance ? this.luminanceChannelOffset : this.greenOffset;
        }

        public int getBlueOrLuminanceOffset() {
            return this.hasLuminance ? this.luminanceChannelOffset : this.blueOffset;
        }

        public int getOpacityOffset() {
            return this.hasLuminance ? this.luminanceChannelOffset : this.alphaChannelOffset;
        }

        public boolean isWriteable() {
            return this.writeable;
        }

        private static Format getFormat(int glFormat) {
            switch (glFormat) {
                case 1: {
                    return LUMINANCE;
                }
                case 2: {
                    return LUMINANCE_ALPHA;
                }
                case 3: {
                    return BGR;
                }
            }
            return ABGR;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum GLFormat {
        ABGR(6408),
        BGR(6407),
        LUMINANCE_ALPHA(6410),
        LUMINANCE(6409),
        INTENSITY(32841);

        private final int glConstant;

        private GLFormat(int glConstant) {
            this.glConstant = glConstant;
        }

        public int getGlConstant() {
            return this.glConstant;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class WriteCallback
    extends STBIWriteCallback {
        private final WritableByteChannel channel;
        @Nullable
        private IOException exception;

        private WriteCallback(WritableByteChannel channel) {
            this.channel = channel;
        }

        @Override
        public void invoke(long context, long data, int size) {
            ByteBuffer byteBuffer = WriteCallback.getData(data, size);
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

