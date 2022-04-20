package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
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
		} else {
			return GlStateManager._genTexture();
		}
	}

	public static void releaseTextureId(int id) {
		RenderSystem.assertOnRenderThreadOrInit();
		GlStateManager._deleteTexture(id);
	}

	public static void prepareImage(int id, int width, int height) {
		prepareImage(NativeImage.InternalFormat.RGBA, id, 0, width, height);
	}

	public static void prepareImage(NativeImage.InternalFormat internalFormat, int id, int width, int height) {
		prepareImage(internalFormat, id, 0, width, height);
	}

	public static void prepareImage(int id, int maxLevel, int width, int height) {
		prepareImage(NativeImage.InternalFormat.RGBA, id, maxLevel, width, height);
	}

	public static void prepareImage(NativeImage.InternalFormat internalFormat, int id, int maxLevel, int width, int height) {
		RenderSystem.assertOnRenderThreadOrInit();
		bind(id);
		if (maxLevel >= 0) {
			GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, maxLevel);
			GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GL12.GL_TEXTURE_MIN_LOD, 0);
			GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LOD, maxLevel);
			GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0.0F);
		}

		for (int i = 0; i <= maxLevel; i++) {
			GlStateManager._texImage2D(GlConst.GL_TEXTURE_2D, i, internalFormat.getValue(), width >> i, height >> i, 0, GlConst.GL_RGBA, GlConst.GL_UNSIGNED_BYTE, null);
		}
	}

	private static void bind(int id) {
		RenderSystem.assertOnRenderThreadOrInit();
		GlStateManager._bindTexture(id);
	}

	public static ByteBuffer readResource(InputStream inputStream) throws IOException {
		ByteBuffer byteBuffer;
		if (inputStream instanceof FileInputStream fileInputStream) {
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

	public static void writeAsPNG(String filename, int id, int scales, int width, int height) {
		RenderSystem.assertOnRenderThread();
		bind(id);

		for (int i = 0; i <= scales; i++) {
			String string = filename + "_" + i + ".png";
			int j = width >> i;
			int k = height >> i;

			try (NativeImage nativeImage = new NativeImage(j, k, false)) {
				nativeImage.loadFromTextureImage(i, false);
				nativeImage.writeTo(string);
				LOGGER.debug("Exported png to: {}", new File(string).getAbsolutePath());
			} catch (IOException var14) {
				LOGGER.debug("Unable to write: ", (Throwable)var14);
			}
		}
	}

	public static void initTexture(IntBuffer imageData, int width, int height) {
		RenderSystem.assertOnRenderThread();
		GL11.glPixelStorei(GlConst.GL_UNPACK_SWAP_BYTES, 0);
		GL11.glPixelStorei(GlConst.GL_UNPACK_LSB_FIRST, 0);
		GL11.glPixelStorei(GlConst.GL_UNPACK_ROW_LENGTH, 0);
		GL11.glPixelStorei(GlConst.GL_UNPACK_SKIP_ROWS, 0);
		GL11.glPixelStorei(GlConst.GL_UNPACK_SKIP_PIXELS, 0);
		GL11.glPixelStorei(GlConst.GL_UNPACK_ALIGNMENT, 4);
		GL11.glTexImage2D(GlConst.GL_TEXTURE_2D, 0, GlConst.GL_RGBA, width, height, 0, GL12.GL_BGRA, 33639, imageData);
		GL11.glTexParameteri(GlConst.GL_TEXTURE_2D, 10240, 9728);
		GL11.glTexParameteri(GlConst.GL_TEXTURE_2D, 10241, 9729);
	}
}
