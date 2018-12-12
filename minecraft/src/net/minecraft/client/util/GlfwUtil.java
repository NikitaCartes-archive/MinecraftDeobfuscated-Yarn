package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class GlfwUtil {
	public static void method_15973() {
		MemoryUtil.memSet(0L, 0, 1L);
	}

	public static double getTime() {
		return GLFW.glfwGetTime();
	}
}
