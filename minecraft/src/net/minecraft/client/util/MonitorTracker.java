package net.minecraft.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class MonitorTracker {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Long2ObjectMap<Monitor> pointerToMonitorMap = new Long2ObjectOpenHashMap<>();
	private final MonitorFactory monitorFactory;

	public MonitorTracker(MonitorFactory monitorFactory) {
		this.monitorFactory = monitorFactory;
		GLFW.glfwSetMonitorCallback(this::handleMonitorEvent);
		PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();
		if (pointerBuffer != null) {
			for (int i = 0; i < pointerBuffer.limit(); i++) {
				long l = pointerBuffer.get(i);
				this.pointerToMonitorMap.put(l, monitorFactory.createMonitor(l));
			}
		}
	}

	private void handleMonitorEvent(long monitor, int event) {
		RenderSystem.assertOnRenderThread();
		if (event == GLFW.GLFW_CONNECTED) {
			this.pointerToMonitorMap.put(monitor, this.monitorFactory.createMonitor(monitor));
			LOGGER.debug("Monitor {} connected. Current monitors: {}", monitor, this.pointerToMonitorMap);
		} else if (event == GLFW.GLFW_DISCONNECTED) {
			this.pointerToMonitorMap.remove(monitor);
			LOGGER.debug("Monitor {} disconnected. Current monitors: {}", monitor, this.pointerToMonitorMap);
		}
	}

	@Nullable
	public Monitor getMonitor(long pointer) {
		return this.pointerToMonitorMap.get(pointer);
	}

	@Nullable
	public Monitor getMonitor(Window window) {
		long l = GLFW.glfwGetWindowMonitor(window.getHandle());
		if (l != 0L) {
			return this.getMonitor(l);
		} else {
			int i = window.getX();
			int j = i + window.getWidth();
			int k = window.getY();
			int m = k + window.getHeight();
			int n = -1;
			Monitor monitor = null;
			long o = GLFW.glfwGetPrimaryMonitor();
			LOGGER.debug("Selecting monitor - primary: {}, current monitors: {}", o, this.pointerToMonitorMap);

			for (Monitor monitor2 : this.pointerToMonitorMap.values()) {
				int p = monitor2.getViewportX();
				int q = p + monitor2.getCurrentVideoMode().getWidth();
				int r = monitor2.getViewportY();
				int s = r + monitor2.getCurrentVideoMode().getHeight();
				int t = clamp(i, p, q);
				int u = clamp(j, p, q);
				int v = clamp(k, r, s);
				int w = clamp(m, r, s);
				int x = Math.max(0, u - t);
				int y = Math.max(0, w - v);
				int z = x * y;
				if (z > n) {
					monitor = monitor2;
					n = z;
				} else if (z == n && o == monitor2.getHandle()) {
					LOGGER.debug("Primary monitor {} is preferred to monitor {}", monitor2, monitor);
					monitor = monitor2;
				}
			}

			LOGGER.debug("Selected monitor: {}", monitor);
			return monitor;
		}
	}

	public static int clamp(int value, int min, int max) {
		if (value < min) {
			return min;
		} else {
			return value > max ? max : value;
		}
	}

	public void stop() {
		RenderSystem.assertOnRenderThread();
		GLFWMonitorCallback gLFWMonitorCallback = GLFW.glfwSetMonitorCallback(null);
		if (gLFWMonitorCallback != null) {
			gLFWMonitorCallback.free();
		}
	}
}
