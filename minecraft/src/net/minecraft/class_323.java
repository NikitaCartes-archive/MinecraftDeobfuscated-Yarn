package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.MonitorFactory;
import net.minecraft.client.util.Window;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;

@Environment(EnvType.CLIENT)
public class class_323 {
	private final Map<Long, Monitor> field_1993 = Maps.<Long, Monitor>newHashMap();
	private final Map<Long, Window> field_1994 = Maps.<Long, Window>newHashMap();
	private final Map<Window, Monitor> field_1992 = Maps.<Window, Monitor>newHashMap();
	private final MonitorFactory field_1991;

	public class_323(MonitorFactory monitorFactory) {
		this.field_1991 = monitorFactory;
		GLFW.glfwSetMonitorCallback(this::method_1683);
		PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();

		for (int i = 0; i < pointerBuffer.limit(); i++) {
			long l = pointerBuffer.get(i);
			this.field_1993.put(l, monitorFactory.createMonitor(l));
		}
	}

	private void method_1683(long l, int i) {
		if (i == 262145) {
			this.field_1993.put(l, this.field_1991.createMonitor(l));
		} else if (i == 262146) {
			this.field_1993.remove(l);
		}
	}

	public Monitor method_1680(long l) {
		return (Monitor)this.field_1993.get(l);
	}

	public Monitor method_1681(Window window) {
		long l = GLFW.glfwGetWindowMonitor(window.getHandle());
		if (l != 0L) {
			return this.method_1680(l);
		} else {
			Monitor monitor = (Monitor)this.field_1993.values().iterator().next();
			int i = -1;
			int j = window.method_4477();
			int k = j + window.method_4480();
			int m = window.method_4499();
			int n = m + window.method_4507();

			for (Monitor monitor2 : this.field_1993.values()) {
				int o = monitor2.getViewportX();
				int p = o + monitor2.getCurrentVideoMode().getWidth();
				int q = monitor2.getViewportY();
				int r = q + monitor2.getCurrentVideoMode().getHeight();
				int s = method_15991(j, o, p);
				int t = method_15991(k, o, p);
				int u = method_15991(m, q, r);
				int v = method_15991(n, q, r);
				int w = Math.max(0, t - s);
				int x = Math.max(0, v - u);
				int y = w * x;
				if (y > i) {
					monitor = monitor2;
					i = y;
				}
			}

			if (monitor != this.field_1992.get(window)) {
				this.field_1992.put(window, monitor);
			}

			return monitor;
		}
	}

	public static int method_15991(int i, int j, int k) {
		if (i < j) {
			return j;
		} else {
			return i > k ? k : i;
		}
	}

	public void method_15992() {
		GLFWMonitorCallback gLFWMonitorCallback = GLFW.glfwSetMonitorCallback(null);
		if (gLFWMonitorCallback != null) {
			gLFWMonitorCallback.free();
		}
	}
}
