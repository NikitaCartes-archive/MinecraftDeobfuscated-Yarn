package com.mojang.blaze3d.platform;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class GlDebugInfo {
	public static ByteBuffer allocateMemory(int size) {
		return MemoryUtil.memAlloc(size);
	}

	public static void freeMemory(Buffer buffer) {
		MemoryUtil.memFree(buffer);
	}

	public static String getVendor() {
		return GlStateManager._getString(GL11.GL_VENDOR);
	}

	public static String getCpuInfo() {
		return GLX._getCpuInfo();
	}

	public static String getRenderer() {
		return GlStateManager._getString(GL11.GL_RENDERER);
	}

	public static String getVersion() {
		return GlStateManager._getString(GL11.GL_VERSION);
	}
}
