package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

@Environment(EnvType.CLIENT)
public final class class_313 {
	private final class_323 field_1801;
	private final long field_1800;
	private final List<class_319> field_1797;
	private class_319 field_1802;
	private int field_1799;
	private int field_1798;

	public class_313(class_323 arg, long l) {
		this.field_1801 = arg;
		this.field_1800 = l;
		this.field_1797 = Lists.<class_319>newArrayList();
		this.method_1615();
	}

	public void method_1615() {
		this.field_1797.clear();
		Buffer buffer = GLFW.glfwGetVideoModes(this.field_1800);

		for (int i = 0; i < buffer.limit(); i++) {
			buffer.position(i);
			class_319 lv = new class_319(buffer);
			if (lv.method_1666() >= 8 && lv.method_1667() >= 8 && lv.method_1672() >= 8) {
				this.field_1797.add(lv);
			}
		}

		int[] is = new int[1];
		int[] js = new int[1];
		GLFW.glfwGetMonitorPos(this.field_1800, is, js);
		this.field_1799 = is[0];
		this.field_1798 = js[0];
		GLFWVidMode gLFWVidMode = GLFW.glfwGetVideoMode(this.field_1800);
		this.field_1802 = new class_319(gLFWVidMode);
	}

	public class_319 method_1614(Optional<class_319> optional) {
		if (optional.isPresent()) {
			class_319 lv = (class_319)optional.get();

			for (class_319 lv2 : Lists.reverse(this.field_1797)) {
				if (lv2.equals(lv)) {
					return lv2;
				}
			}
		}

		return this.method_1617();
	}

	public int method_1619(Optional<class_319> optional) {
		if (optional.isPresent()) {
			class_319 lv = (class_319)optional.get();

			for (int i = this.field_1797.size() - 1; i >= 0; i--) {
				if (lv.equals(this.field_1797.get(i))) {
					return i;
				}
			}
		}

		return this.field_1797.indexOf(this.method_1617());
	}

	public class_319 method_1617() {
		return this.field_1802;
	}

	public int method_1616() {
		return this.field_1799;
	}

	public int method_1618() {
		return this.field_1798;
	}

	public class_319 method_1620(int i) {
		return (class_319)this.field_1797.get(i);
	}

	public int method_1621() {
		return this.field_1797.size();
	}

	public long method_1622() {
		return this.field_1800;
	}

	public String toString() {
		return String.format("Monitor[%s %sx%s %s]", this.field_1800, this.field_1799, this.field_1798, this.field_1802);
	}
}
