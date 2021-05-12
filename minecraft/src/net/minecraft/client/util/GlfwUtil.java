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
	public static void accessRecordingQueue(RenderCallStorage storage, float f) {
		ConcurrentLinkedQueue<RenderCall> concurrentLinkedQueue = storage.getRecordingQueue();
	}

	public static void accessProcessingQueue(RenderCallStorage storage, float f) {
		ConcurrentLinkedQueue<RenderCall> concurrentLinkedQueue = storage.getProcessingQueue();
	}

	public static void makeJvmCrash() {
		MemoryUtil.memSet(0L, 0, 1L);
	}

	public static double getTime() {
		return GLFW.glfwGetTime();
	}
}
