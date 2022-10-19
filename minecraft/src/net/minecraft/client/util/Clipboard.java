package net.minecraft.client.util;

import com.google.common.base.Charsets;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextVisitFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class Clipboard {
	public static final int GLFW_FORMAT_UNAVAILABLE = 65545;
	private final ByteBuffer clipboardBuffer = BufferUtils.createByteBuffer(8192);

	public String getClipboard(long window, GLFWErrorCallbackI errorCallback) {
		GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback(errorCallback);
		String string = GLFW.glfwGetClipboardString(window);
		string = string != null ? TextVisitFactory.validateSurrogates(string) : "";
		GLFWErrorCallback gLFWErrorCallback2 = GLFW.glfwSetErrorCallback(gLFWErrorCallback);
		if (gLFWErrorCallback2 != null) {
			gLFWErrorCallback2.free();
		}

		return string;
	}

	private static void setClipboard(long window, ByteBuffer clipboardBuffer, byte[] content) {
		clipboardBuffer.clear();
		clipboardBuffer.put(content);
		clipboardBuffer.put((byte)0);
		clipboardBuffer.flip();
		GLFW.glfwSetClipboardString(window, clipboardBuffer);
	}

	public void setClipboard(long window, String string) {
		byte[] bs = string.getBytes(Charsets.UTF_8);
		int i = bs.length + 1;
		if (i < this.clipboardBuffer.capacity()) {
			setClipboard(window, this.clipboardBuffer, bs);
		} else {
			ByteBuffer byteBuffer = MemoryUtil.memAlloc(i);

			try {
				setClipboard(window, byteBuffer, bs);
			} finally {
				MemoryUtil.memFree(byteBuffer);
			}
		}
	}
}
