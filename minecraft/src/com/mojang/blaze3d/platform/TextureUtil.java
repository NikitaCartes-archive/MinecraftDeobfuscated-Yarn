package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_6177;
import net.minecraft.client.texture.NativeImage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
@class_6177
public class TextureUtil {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final int MIN_MIPMAP_LEVEL = 0;
	private static final int DEFAULT_IMAGE_BUFFER_SIZE = 8192;

	public static int generateTextureId() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		if (SharedConstants.isDevelopment) {
			int[] is = new int[ThreadLocalRandom.current().nextInt(15) + 1];
			GlStateManager._genTextures(is);
			int i = GlStateManager._genTexture();
			GlStateManager._deleteTextures(is);
			return i;
		} else {
			return GlStateManager._genTexture();
		}
	}

	public static void releaseTextureId(int id) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GlStateManager._deleteTexture(id);
	}

	public static void prepareImage(int id, int width, int height) {
		prepareImage(NativeImage.GLFormat.ABGR, id, 0, width, height);
	}

	public static void prepareImage(NativeImage.GLFormat internalFormat, int id, int width, int height) {
		prepareImage(internalFormat, id, 0, width, height);
	}

	public static void prepareImage(int id, int maxLevel, int width, int height) {
		prepareImage(NativeImage.GLFormat.ABGR, id, maxLevel, width, height);
	}

	public static void prepareImage(NativeImage.GLFormat internalFormat, int id, int maxLevel, int width, int height) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		bind(id);
		if (maxLevel >= 0) {
			GlStateManager._texParameter(3553, 33085, maxLevel);
			GlStateManager._texParameter(3553, 33082, 0);
			GlStateManager._texParameter(3553, 33083, maxLevel);
			GlStateManager._texParameter(3553, 34049, 0.0F);
		}

		for (int i = 0; i <= maxLevel; i++) {
			GlStateManager._texImage2D(3553, i, internalFormat.getGlConstant(), width >> i, height >> i, 0, 6408, 5121, null);
		}
	}

	private static void bind(int id) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GlStateManager._bindTexture(id);
	}

	public static ByteBuffer readResource(InputStream inputStream) throws IOException {
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
				if (byteBuffer.remaining() == 0) {
					byteBuffer = MemoryUtil.memRealloc(byteBuffer, byteBuffer.capacity() * 2);
				}
			}
		}

		return byteBuffer;
	}

	@Nullable
	public static String readResourceAsString(InputStream inputStream) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		ByteBuffer byteBuffer = null;

		try {
			byteBuffer = readResource(inputStream);
			int i = byteBuffer.position();
			byteBuffer.rewind();
			return MemoryUtil.memASCII(byteBuffer, i);
		} catch (IOException var7) {
		} finally {
			if (byteBuffer != null) {
				MemoryUtil.memFree(byteBuffer);
			}
		}

		return null;
	}

	public static void writeAsPNG(String string, int i, int j, int k, int l) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		bind(i);

		for (int m = 0; m <= j; m++) {
			String string2 = string + "_" + m + ".png";
			int n = k >> m;
			int o = l >> m;

			try (NativeImage nativeImage = new NativeImage(n, o, false)) {
				nativeImage.loadFromTextureImage(m, false);
				nativeImage.method_35622(string2);
				LOGGER.debug("Exported png to: {}", new File(string2).getAbsolutePath());
			} catch (IOException var22) {
				LOGGER.debug("Unable to write: ", (Throwable)var22);
			}
		}
	}

	public static void initTexture(IntBuffer imageData, int width, int height) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GL11.glPixelStorei(3312, 0);
		GL11.glPixelStorei(3313, 0);
		GL11.glPixelStorei(3314, 0);
		GL11.glPixelStorei(3315, 0);
		GL11.glPixelStorei(3316, 0);
		GL11.glPixelStorei(3317, 4);
		GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 32993, 33639, imageData);
		GL11.glTexParameteri(3553, 10240, 9728);
		GL11.glTexParameteri(3553, 10241, 9729);
	}
}
