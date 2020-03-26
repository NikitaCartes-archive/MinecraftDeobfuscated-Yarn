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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class TextureUtil {
	private static final Logger field_22547 = LogManager.getLogger();

	public static int method_24956() {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		return GlStateManager.genTextures();
	}

	public static void method_24957(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GlStateManager.deleteTexture(i);
	}

	public static void method_24958(int i, int j, int k) {
		method_24961(NativeImage.GLFormat.RGBA, i, 0, j, k);
	}

	public static void method_24960(NativeImage.GLFormat gLFormat, int i, int j, int k) {
		method_24961(gLFormat, i, 0, j, k);
	}

	public static void method_24959(int i, int j, int k, int l) {
		method_24961(NativeImage.GLFormat.RGBA, i, j, k, l);
	}

	public static void method_24961(NativeImage.GLFormat gLFormat, int i, int j, int k, int l) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		method_24964(i);
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

	private static void method_24964(int i) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		GlStateManager.bindTexture(i);
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
				if (byteBuffer.remaining() == 0) {
					byteBuffer = MemoryUtil.memRealloc(byteBuffer, byteBuffer.capacity() * 2);
				}
			}
		}

		return byteBuffer;
	}

	public static String method_24965(InputStream inputStream) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		ByteBuffer byteBuffer = null;

		try {
			byteBuffer = method_24962(inputStream);
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

	public static void method_24963(IntBuffer intBuffer, int i, int j) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
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
