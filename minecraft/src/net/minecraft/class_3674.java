package net.minecraft;

import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class class_3674 {
	private final ByteBuffer field_16236 = ByteBuffer.allocateDirect(1024);

	public String method_15977(long l, GLFWErrorCallbackI gLFWErrorCallbackI) {
		GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback(gLFWErrorCallbackI);
		String string = GLFW.glfwGetClipboardString(l);
		string = string != null ? class_155.method_16885(string) : "";
		GLFWErrorCallback gLFWErrorCallback2 = GLFW.glfwSetErrorCallback(gLFWErrorCallback);
		if (gLFWErrorCallback2 != null) {
			gLFWErrorCallback2.free();
		}

		return string;
	}

	private void method_15978(long l, ByteBuffer byteBuffer, String string) {
		MemoryUtil.memUTF8(string, true, byteBuffer);
		GLFW.glfwSetClipboardString(l, byteBuffer);
	}

	public void method_15979(long l, String string) {
		int i = MemoryUtil.memLengthUTF8(string, true);
		if (i < this.field_16236.capacity()) {
			this.method_15978(l, this.field_16236, string);
			this.field_16236.clear();
		} else {
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(i);
			this.method_15978(l, byteBuffer, string);
		}
	}
}
