/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class TextureUtil {
    private static final Logger LOGGER = LogManager.getLogger();

    public static int generateId() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GlStateManager.genTextures();
    }

    public static void deleteId(int id) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.deleteTexture(id);
    }

    public static void allocate(int id, int width, int height) {
        TextureUtil.allocate(NativeImage.GLFormat.ABGR, id, 0, width, height);
    }

    public static void allocate(NativeImage.GLFormat internalFormat, int id, int width, int height) {
        TextureUtil.allocate(internalFormat, id, 0, width, height);
    }

    public static void allocate(int id, int maxLevel, int width, int height) {
        TextureUtil.allocate(NativeImage.GLFormat.ABGR, id, maxLevel, width, height);
    }

    /**
     * Allocate uninitialized backing memory for {@code maxLevel+1}
     * miplevels to texture {@code id}.
     */
    public static void allocate(NativeImage.GLFormat internalFormat, int id, int maxLevel, int width, int height) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        TextureUtil.bind(id);
        if (maxLevel >= 0) {
            GlStateManager.texParameter(3553, 33085, maxLevel);
            GlStateManager.texParameter(3553, 33082, 0);
            GlStateManager.texParameter(3553, 33083, maxLevel);
            GlStateManager.texParameter(3553, 34049, 0.0f);
        }
        for (int i = 0; i <= maxLevel; ++i) {
            GlStateManager.texImage2D(3553, i, internalFormat.getGlConstant(), width >> i, height >> i, 0, 6408, 5121, null);
        }
    }

    private static void bind(int id) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.bindTexture(id);
    }

    public static ByteBuffer method_24962(InputStream inputStream) throws IOException {
        ByteBuffer byteBuffer;
        if (inputStream instanceof FileInputStream) {
            FileInputStream fileInputStream = (FileInputStream)inputStream;
            FileChannel fileChannel = fileInputStream.getChannel();
            byteBuffer = MemoryUtil.memAlloc((int)fileChannel.size() + 1);
            while (fileChannel.read(byteBuffer) != -1) {
            }
        } else {
            byteBuffer = MemoryUtil.memAlloc(8192);
            ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
            while (readableByteChannel.read(byteBuffer) != -1) {
                if (byteBuffer.remaining() != 0) continue;
                byteBuffer = MemoryUtil.memRealloc(byteBuffer, byteBuffer.capacity() * 2);
            }
        }
        return byteBuffer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String method_24965(InputStream inputStream) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = TextureUtil.method_24962(inputStream);
            int i = byteBuffer.position();
            byteBuffer.rewind();
            String string = MemoryUtil.memASCII(byteBuffer, i);
            return string;
        } catch (IOException iOException) {
        } finally {
            if (byteBuffer != null) {
                MemoryUtil.memFree(byteBuffer);
            }
        }
        return null;
    }

    /**
     * Uploads {@code imageData} to the bound texture.
     * Each integer is interpreted as 0xAARRGGBB.
     */
    public static void uploadImage(IntBuffer imageData, int width, int height) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPixelStorei(3312, 0);
        GL11.glPixelStorei(3313, 0);
        GL11.glPixelStorei(3314, 0);
        GL11.glPixelStorei(3315, 0);
        GL11.glPixelStorei(3316, 0);
        GL11.glPixelStorei(3317, 4);
        GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 32993, 33639, imageData);
        GL11.glTexParameteri(3553, 10242, 10497);
        GL11.glTexParameteri(3553, 10243, 10497);
        GL11.glTexParameteri(3553, 10240, 9728);
        GL11.glTexParameteri(3553, 10241, 9729);
    }
}

