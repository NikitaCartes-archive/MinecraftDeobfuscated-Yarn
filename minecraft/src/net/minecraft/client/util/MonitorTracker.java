package net.minecraft.client.util;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;

@Environment(EnvType.CLIENT)
public class MonitorTracker {
	private final Map<Long, Monitor> pointerToMonitorMap = Maps.<Long, Monitor>newHashMap();
	private final Map<Long, Window> pointerToWindowMap = Maps.<Long, Window>newHashMap();
	private final Map<Window, Monitor> windowToMonitorMap = Maps.<Window, Monitor>newHashMap();
	private final MonitorFactory monitorFactory;

	public MonitorTracker(MonitorFactory monitorFactory) {
		this.monitorFactory = monitorFactory;
		GLFW.glfwSetMonitorCallback(this::handleMonitorEvent);
		PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();

		for (int i = 0; i < pointerBuffer.limit(); i++) {
			long l = pointerBuffer.get(i);
			this.pointerToMonitorMap.put(l, monitorFactory.createMonitor(l));
		}
	}

	private void handleMonitorEvent(long l, int i) {
		if (i == 262145) {
			this.pointerToMonitorMap.put(l, this.monitorFactory.createMonitor(l));
		} else if (i == 262146) {
			this.pointerToMonitorMap.remove(l);
		}
	}

	public Monitor getMonitor(long l) {
		return (Monitor)this.pointerToMonitorMap.get(l);
	}

	public Monitor method_1681(Window window) {
		long l = GLFW.glfwGetWindowMonitor(window.getHandle());
		if (l != 0L) {
			return this.getMonitor(l);
		} else {
			Monitor monitor = (Monitor)this.pointerToMonitorMap.values().iterator().next();
			int i = -1;
			int j = window.getPositionX();
			int k = j + window.getWidth();
			int m = window.getPositionY();
			int n = m + window.getHeight();

			for (Monitor monitor2 : this.pointerToMonitorMap.values()) {
				int o = monitor2.getViewportX();
				int p = o + monitor2.method_1617().getWidth();
				int q = monitor2.getViewportY();
				int r = q + monitor2.method_1617().getHeight();
				int s = clamp(j, o, p);
				int t = clamp(k, o, p);
				int u = clamp(m, q, r);
				int v = clamp(n, q, r);
				int w = Math.max(0, t - s);
				int x = Math.max(0, v - u);
				int y = w * x;
				if (y > i) {
					monitor = monitor2;
					i = y;
				}
			}

			if (monitor != this.windowToMonitorMap.get(window)) {
				this.windowToMonitorMap.put(window, monitor);
			}

			return monitor;
		}
	}

	public static int clamp(int i, int j, int k) {
		if (i < j) {
			return j;
		} else {
			return i > k ? k : i;
		}
	}

	public void stop() {
		GLFWMonitorCallback gLFWMonitorCallback = GLFW.glfwSetMonitorCallback(null);
		if (gLFWMonitorCallback != null) {
			gLFWMonitorCallback.free();
		}
	}
}
