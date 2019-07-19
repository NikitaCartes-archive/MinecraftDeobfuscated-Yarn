package net.minecraft.client.util;

import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class Clipboard {
	private final ByteBuffer clipboardBuffer = ByteBuffer.allocateDirect(1024);

	public String getClipboard(long window, GLFWErrorCallbackI gLFWErrorCallbackI) {
		GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback(gLFWErrorCallbackI);
		String string = GLFW.glfwGetClipboardString(window);
		string = string != null ? SharedConstants.stripSupplementaryChars(string) : "";
		GLFWErrorCallback gLFWErrorCallback2 = GLFW.glfwSetErrorCallback(gLFWErrorCallback);
		if (gLFWErrorCallback2 != null) {
			gLFWErrorCallback2.free();
		}

		return string;
	}

	private void setClipboard(long window, ByteBuffer byteBuffer, String string) {
		MemoryUtil.memUTF8(string, true, byteBuffer);
		GLFW.glfwSetClipboardString(window, byteBuffer);
	}

	public void setClipboard(long window, String string) {
		int i = MemoryUtil.memLengthUTF8(string, true);
		if (i < this.clipboardBuffer.capacity()) {
			this.setClipboard(window, this.clipboardBuffer, string);
			this.clipboardBuffer.clear();
		} else {
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(i);
			this.setClipboard(window, byteBuffer, string);
		}
	}
}
