/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public class TextureUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int MIN_MIPMAP_LEVEL = 0;
    private static final int DEFAULT_IMAGE_BUFFER_SIZE = 8192;

    public static int generateTextureId() {
        RenderSystem.assertOnRenderThreadOrInit();
        if (SharedConstants.isDevelopment) {
            int[] is = new int[ThreadLocalRandom.current().nextInt(15) + 1];
            GlStateManager._genTextures(is);
            int i = GlStateManager._genTexture();
            GlStateManager._deleteTextures(is);
            return i;
        }
        return GlStateManager._genTexture();
    }

    public static void releaseTextureId(int id) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._deleteTexture(id);
    }

    public static void prepareImage(int id, int width, int height) {
        TextureUtil.prepareImage(NativeImage.InternalFormat.RGBA, id, 0, width, height);
    }

    public static void prepareImage(NativeImage.InternalFormat internalFormat, int id, int width, int height) {
        TextureUtil.prepareImage(internalFormat, id, 0, width, height);
    }

    public static void prepareImage(int id, int maxLevel, int width, int height) {
        TextureUtil.prepareImage(NativeImage.InternalFormat.RGBA, id, maxLevel, width, height);
    }

    public static void prepareImage(NativeImage.InternalFormat internalFormat, int id, int maxLevel, int width, int height) {
        RenderSystem.assertOnRenderThreadOrInit();
        TextureUtil.bind(id);
        if (maxLevel >= 0) {
            GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, maxLevel);
            GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GL12.GL_TEXTURE_MIN_LOD, 0);
            GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LOD, maxLevel);
            GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0.0f);
        }
        for (int i = 0; i <= maxLevel; ++i) {
            GlStateManager._texImage2D(GlConst.GL_TEXTURE_2D, i, internalFormat.getValue(), width >> i, height >> i, 0, GlConst.GL_RGBA, GlConst.GL_UNSIGNED_BYTE, null);
        }
    }

    private static void bind(int id) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._bindTexture(id);
    }

    public static ByteBuffer readResource(InputStream inputStream) throws IOException {
        ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
        if (readableByteChannel instanceof SeekableByteChannel) {
            SeekableByteChannel seekableByteChannel = (SeekableByteChannel)readableByteChannel;
            return TextureUtil.readResource(readableByteChannel, (int)seekableByteChannel.size() + 1);
        }
        return TextureUtil.readResource(readableByteChannel, 8192);
    }

    private static ByteBuffer readResource(ReadableByteChannel channel, int bufSize) throws IOException {
        ByteBuffer byteBuffer = MemoryUtil.memAlloc(bufSize);
        try {
            while (channel.read(byteBuffer) != -1) {
                if (byteBuffer.hasRemaining()) continue;
                byteBuffer = MemoryUtil.memRealloc(byteBuffer, byteBuffer.capacity() * 2);
            }
            return byteBuffer;
        } catch (IOException iOException) {
            MemoryUtil.memFree(byteBuffer);
            throw iOException;
        }
    }

    public static void writeAsPNG(Path directory, String prefix, int textureId, int scales, int width, int height) {
        RenderSystem.assertOnRenderThread();
        TextureUtil.bind(textureId);
        for (int i = 0; i <= scales; ++i) {
            int j = width >> i;
            int k = height >> i;
            try (NativeImage nativeImage = new NativeImage(j, k, false);){
                nativeImage.loadFromTextureImage(i, false);
                Path path = directory.resolve(prefix + "_" + i + ".png");
                nativeImage.writeTo(path);
                LOGGER.debug("Exported png to: {}", (Object)path.toAbsolutePath());
                continue;
            } catch (IOException iOException) {
                LOGGER.debug("Unable to write: ", iOException);
            }
        }
    }

    public static Path getDebugTexturePath(Path path) {
        return path.resolve("screenshots").resolve("debug");
    }

    public static Path getDebugTexturePath() {
        return TextureUtil.getDebugTexturePath(Path.of(".", new String[0]));
    }
}

