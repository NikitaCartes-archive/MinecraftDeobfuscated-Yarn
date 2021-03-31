package net.minecraft.client.util;

import com.mojang.blaze3d.systems.RenderCall;
import com.mojang.blaze3d.systems.RenderCallStorage;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class GlfwUtil {
	public static void method_35596(RenderCallStorage renderCallStorage, float f) {
		ConcurrentLinkedQueue<RenderCall> concurrentLinkedQueue = renderCallStorage.method_35608();
	}

	public static void method_35597(RenderCallStorage renderCallStorage, float f) {
		ConcurrentLinkedQueue<RenderCall> concurrentLinkedQueue = renderCallStorage.method_35609();
	}

	public static void makeJvmCrash() {
		MemoryUtil.memSet(0L, 0, 1L);
	}

	public static double getTime() {
		return GLFW.glfwGetTime();
	}
}
