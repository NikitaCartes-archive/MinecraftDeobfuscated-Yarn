package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class class_3673 {
	public static void method_15973() {
		MemoryUtil.memSet(0L, 0, 1L);
	}

	public static double method_15974() {
		return GLFW.glfwGetTime();
	}
}
