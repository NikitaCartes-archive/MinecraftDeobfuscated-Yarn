package com.mojang.blaze3d.platform;

import java.io.File;
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

@Environment(EnvType.CLIENT)
public class TextureUtil {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final int MIN_MIPMAP_LEVEL = 0;
	private static final int DEFAULT_IMAGE_BUFFER_SIZE = 8192;

	public static int generateTextureId() {
		return GlStateManager.genTexture();
	}

	public static void releaseTextureId(int i) {
		GlStateManager.deleteTexture(i);
	}

	public static void prepareImage(int i, int j, int k) {
		prepareImage(NativeImage.GLFormat.RGBA, i, 0, j, k);
	}

	public static void prepareImage(NativeImage.GLFormat gLFormat, int i, int j, int k) {
		prepareImage(gLFormat, i, 0, j, k);
	}

	public static void prepareImage(int i, int j, int k, int l) {
		prepareImage(NativeImage.GLFormat.RGBA, i, j, k, l);
	}

	public static void prepareImage(NativeImage.GLFormat gLFormat, int i, int j, int k, int l) {
		bind(i);
		if (j >= 0) {
			GlStateManager.texParameter(3553, 33085, j);
			GlStateManager.texParameter(3553, 33082, 0);
			GlStateManager.texParameter(3553, 33083, j);
			GlStateManager.texParameter(3553, 34049, 0.0F);
		}

		for (int m = 0; m <= j; m++) {
			GlStateManager.texImage2D(3553, m, gLFormat.getGlConstant(), k >> m, l >> m, 0, 6408, 5121, null);
		}
	}

	private static void bind(int i) {
		GlStateManager.bindTexture(i);
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

	public static String readResourceAsString(InputStream inputStream) {
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
		bind(i);

		for (int m = 0; m <= j; m++) {
			String string2 = string + "_" + m + ".png";
			int n = k >> m;
			int o = l >> m;

			try (NativeImage nativeImage = new NativeImage(n, o, false)) {
				nativeImage.loadFromTextureImage(m, false);
				nativeImage.writeFile(string2);
				LOGGER.debug("Exported png to: {}", new File(string2).getAbsolutePath());
			} catch (IOException var22) {
				LOGGER.debug("Unable to write: ", (Throwable)var22);
			}
		}
	}

	public static void initTexture(IntBuffer intBuffer, int i, int j) {
		GL11.glPixelStorei(3312, 0);
		GL11.glPixelStorei(3313, 0);
		GL11.glPixelStorei(3314, 0);
		GL11.glPixelStorei(3315, 0);
		GL11.glPixelStorei(3316, 0);
		GL11.glPixelStorei(3317, 4);
		GL11.glTexImage2D(3553, 0, 6408, i, j, 0, 32993, 33639, intBuffer);
		GL11.glTexParameteri(3553, 10242, 10497);
		GL11.glTexParameteri(3553, 10243, 10497);
		GL11.glTexParameteri(3553, 10240, 9728);
		GL11.glTexParameteri(3553, 10241, 9729);
	}
}
