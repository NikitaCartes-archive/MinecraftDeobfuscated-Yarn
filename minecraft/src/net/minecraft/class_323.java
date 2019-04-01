package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;

@Environment(EnvType.CLIENT)
public class class_323 {
	private final Map<Long, class_313> field_1993 = Maps.<Long, class_313>newHashMap();
	private final Map<Long, class_1041> field_1994 = Maps.<Long, class_1041>newHashMap();
	private final Map<class_1041, class_313> field_1992 = Maps.<class_1041, class_313>newHashMap();
	private final class_3676 field_1991;

	public class_323(class_3676 arg) {
		this.field_1991 = arg;
		GLFW.glfwSetMonitorCallback(this::method_1683);
		PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();

		for (int i = 0; i < pointerBuffer.limit(); i++) {
			long l = pointerBuffer.get(i);
			this.field_1993.put(l, arg.createMonitor(l));
		}
	}

	private void method_1683(long l, int i) {
		if (i == 262145) {
			this.field_1993.put(l, this.field_1991.createMonitor(l));
		} else if (i == 262146) {
			this.field_1993.remove(l);
		}
	}

	public class_313 method_1680(long l) {
		return (class_313)this.field_1993.get(l);
	}

	public class_313 method_1681(class_1041 arg) {
		long l = GLFW.glfwGetWindowMonitor(arg.method_4490());
		if (l != 0L) {
			return this.method_1680(l);
		} else {
			class_313 lv = (class_313)this.field_1993.values().iterator().next();
			int i = -1;
			int j = arg.method_4477();
			int k = j + arg.method_4480();
			int m = arg.method_4499();
			int n = m + arg.method_4507();

			for (class_313 lv2 : this.field_1993.values()) {
				int o = lv2.method_1616();
				int p = o + lv2.method_1617().method_1668();
				int q = lv2.method_1618();
				int r = q + lv2.method_1617().method_1669();
				int s = method_15991(j, o, p);
				int t = method_15991(k, o, p);
				int u = method_15991(m, q, r);
				int v = method_15991(n, q, r);
				int w = Math.max(0, t - s);
				int x = Math.max(0, v - u);
				int y = w * x;
				if (y > i) {
					lv = lv2;
					i = y;
				}
			}

			if (lv != this.field_1992.get(arg)) {
				this.field_1992.put(arg, lv);
			}

			return lv;
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
