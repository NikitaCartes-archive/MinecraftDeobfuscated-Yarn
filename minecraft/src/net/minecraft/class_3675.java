package net.minecraft;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

@Environment(EnvType.CLIENT)
public class class_3675 {
	public static final class_3675.class_306 field_16237 = class_3675.class_307.field_1668.method_1447(-1);

	public static class_3675.class_306 method_15985(int i, int j) {
		return i == -1 ? class_3675.class_307.field_1671.method_1447(j) : class_3675.class_307.field_1668.method_1447(i);
	}

	public static class_3675.class_306 method_15981(String string) {
		if (class_3675.class_306.field_1664.containsKey(string)) {
			return (class_3675.class_306)class_3675.class_306.field_1664.get(string);
		} else {
			for (class_3675.class_307 lv : class_3675.class_307.values()) {
				if (string.startsWith(lv.field_1673)) {
					String string2 = string.substring(lv.field_1673.length() + 1);
					return lv.method_1447(Integer.parseInt(string2));
				}
			}

			throw new IllegalArgumentException("Unknown key name: " + string);
		}
	}

	public static boolean method_15987(long l, int i) {
		return GLFW.glfwGetKey(l, i) == 1;
	}

	public static void method_15986(long l, GLFWKeyCallbackI gLFWKeyCallbackI, GLFWCharModsCallbackI gLFWCharModsCallbackI) {
		GLFW.glfwSetKeyCallback(l, gLFWKeyCallbackI);
		GLFW.glfwSetCharModsCallback(l, gLFWCharModsCallbackI);
	}

	public static void method_15983(
		long l, GLFWCursorPosCallbackI gLFWCursorPosCallbackI, GLFWMouseButtonCallbackI gLFWMouseButtonCallbackI, GLFWScrollCallbackI gLFWScrollCallbackI
	) {
		GLFW.glfwSetCursorPosCallback(l, gLFWCursorPosCallbackI);
		GLFW.glfwSetMouseButtonCallback(l, gLFWMouseButtonCallbackI);
		GLFW.glfwSetScrollCallback(l, gLFWScrollCallbackI);
	}

	public static void method_15984(long l, int i, double d, double e) {
		GLFW.glfwSetCursorPos(l, d, e);
		GLFW.glfwSetInputMode(l, 208897, i);
	}

	@Nullable
	public static String method_15988(int i) {
		return GLFW.glfwGetKeyName(i, -1);
	}

	@Nullable
	public static String method_15982(int i) {
		return GLFW.glfwGetKeyName(-1, i);
	}

	@Environment(EnvType.CLIENT)
	public static final class class_306 {
		private final String field_1663;
		private final class_3675.class_307 field_1666;
		private final int field_1665;
		private static final Map<String, class_3675.class_306> field_1664 = Maps.<String, class_3675.class_306>newHashMap();

		private class_306(String string, class_3675.class_307 arg, int i) {
			this.field_1663 = string;
			this.field_1666 = arg;
			this.field_1665 = i;
			field_1664.put(string, this);
		}

		public class_3675.class_307 method_1442() {
			return this.field_1666;
		}

		public int method_1444() {
			return this.field_1665;
		}

		public String method_1441() {
			return this.field_1663;
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				class_3675.class_306 lv = (class_3675.class_306)object;
				return this.field_1665 == lv.field_1665 && this.field_1666 == lv.field_1666;
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.field_1666, this.field_1665});
		}

		public String toString() {
			return this.field_1663;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_307 {
		field_1668("key.keyboard"),
		field_1671("scancode"),
		field_1672("key.mouse");

		private static final String[] field_1669 = new String[]{"left", "middle", "right"};
		private final Int2ObjectMap<class_3675.class_306> field_1674 = new Int2ObjectOpenHashMap<>();
		private final String field_1673;

		private static void method_1446(class_3675.class_307 arg, String string, int i) {
			class_3675.class_306 lv = new class_3675.class_306(string, arg, i);
			arg.field_1674.put(i, lv);
		}

		private class_307(String string2) {
			this.field_1673 = string2;
		}

		public class_3675.class_306 method_1447(int i) {
			if (this.field_1674.containsKey(i)) {
				return this.field_1674.get(i);
			} else {
				String string;
				if (this == field_1672) {
					if (i <= 2) {
						string = "." + field_1669[i];
					} else {
						string = "." + (i + 1);
					}
				} else {
					string = "." + i;
				}

				class_3675.class_306 lv = new class_3675.class_306(this.field_1673 + string, this, i);
				this.field_1674.put(i, lv);
				return lv;
			}
		}

		public String method_15989() {
			return this.field_1673;
		}

		static {
			method_1446(field_1668, "key.keyboard.unknown", -1);
			method_1446(field_1672, "key.mouse.left", 0);
			method_1446(field_1672, "key.mouse.right", 1);
			method_1446(field_1672, "key.mouse.middle", 2);
			method_1446(field_1672, "key.mouse.4", 3);
			method_1446(field_1672, "key.mouse.5", 4);
			method_1446(field_1672, "key.mouse.6", 5);
			method_1446(field_1672, "key.mouse.7", 6);
			method_1446(field_1672, "key.mouse.8", 7);
			method_1446(field_1668, "key.keyboard.0", 48);
			method_1446(field_1668, "key.keyboard.1", 49);
			method_1446(field_1668, "key.keyboard.2", 50);
			method_1446(field_1668, "key.keyboard.3", 51);
			method_1446(field_1668, "key.keyboard.4", 52);
			method_1446(field_1668, "key.keyboard.5", 53);
			method_1446(field_1668, "key.keyboard.6", 54);
			method_1446(field_1668, "key.keyboard.7", 55);
			method_1446(field_1668, "key.keyboard.8", 56);
			method_1446(field_1668, "key.keyboard.9", 57);
			method_1446(field_1668, "key.keyboard.a", 65);
			method_1446(field_1668, "key.keyboard.b", 66);
			method_1446(field_1668, "key.keyboard.c", 67);
			method_1446(field_1668, "key.keyboard.d", 68);
			method_1446(field_1668, "key.keyboard.e", 69);
			method_1446(field_1668, "key.keyboard.f", 70);
			method_1446(field_1668, "key.keyboard.g", 71);
			method_1446(field_1668, "key.keyboard.h", 72);
			method_1446(field_1668, "key.keyboard.i", 73);
			method_1446(field_1668, "key.keyboard.j", 74);
			method_1446(field_1668, "key.keyboard.k", 75);
			method_1446(field_1668, "key.keyboard.l", 76);
			method_1446(field_1668, "key.keyboard.m", 77);
			method_1446(field_1668, "key.keyboard.n", 78);
			method_1446(field_1668, "key.keyboard.o", 79);
			method_1446(field_1668, "key.keyboard.p", 80);
			method_1446(field_1668, "key.keyboard.q", 81);
			method_1446(field_1668, "key.keyboard.r", 82);
			method_1446(field_1668, "key.keyboard.s", 83);
			method_1446(field_1668, "key.keyboard.t", 84);
			method_1446(field_1668, "key.keyboard.u", 85);
			method_1446(field_1668, "key.keyboard.v", 86);
			method_1446(field_1668, "key.keyboard.w", 87);
			method_1446(field_1668, "key.keyboard.x", 88);
			method_1446(field_1668, "key.keyboard.y", 89);
			method_1446(field_1668, "key.keyboard.z", 90);
			method_1446(field_1668, "key.keyboard.f1", 290);
			method_1446(field_1668, "key.keyboard.f2", 291);
			method_1446(field_1668, "key.keyboard.f3", 292);
			method_1446(field_1668, "key.keyboard.f4", 293);
			method_1446(field_1668, "key.keyboard.f5", 294);
			method_1446(field_1668, "key.keyboard.f6", 295);
			method_1446(field_1668, "key.keyboard.f7", 296);
			method_1446(field_1668, "key.keyboard.f8", 297);
			method_1446(field_1668, "key.keyboard.f9", 298);
			method_1446(field_1668, "key.keyboard.f10", 299);
			method_1446(field_1668, "key.keyboard.f11", 300);
			method_1446(field_1668, "key.keyboard.f12", 301);
			method_1446(field_1668, "key.keyboard.f13", 302);
			method_1446(field_1668, "key.keyboard.f14", 303);
			method_1446(field_1668, "key.keyboard.f15", 304);
			method_1446(field_1668, "key.keyboard.f16", 305);
			method_1446(field_1668, "key.keyboard.f17", 306);
			method_1446(field_1668, "key.keyboard.f18", 307);
			method_1446(field_1668, "key.keyboard.f19", 308);
			method_1446(field_1668, "key.keyboard.f20", 309);
			method_1446(field_1668, "key.keyboard.f21", 310);
			method_1446(field_1668, "key.keyboard.f22", 311);
			method_1446(field_1668, "key.keyboard.f23", 312);
			method_1446(field_1668, "key.keyboard.f24", 313);
			method_1446(field_1668, "key.keyboard.f25", 314);
			method_1446(field_1668, "key.keyboard.num.lock", 282);
			method_1446(field_1668, "key.keyboard.keypad.0", 320);
			method_1446(field_1668, "key.keyboard.keypad.1", 321);
			method_1446(field_1668, "key.keyboard.keypad.2", 322);
			method_1446(field_1668, "key.keyboard.keypad.3", 323);
			method_1446(field_1668, "key.keyboard.keypad.4", 324);
			method_1446(field_1668, "key.keyboard.keypad.5", 325);
			method_1446(field_1668, "key.keyboard.keypad.6", 326);
			method_1446(field_1668, "key.keyboard.keypad.7", 327);
			method_1446(field_1668, "key.keyboard.keypad.8", 328);
			method_1446(field_1668, "key.keyboard.keypad.9", 329);
			method_1446(field_1668, "key.keyboard.keypad.add", 334);
			method_1446(field_1668, "key.keyboard.keypad.decimal", 330);
			method_1446(field_1668, "key.keyboard.keypad.enter", 335);
			method_1446(field_1668, "key.keyboard.keypad.equal", 336);
			method_1446(field_1668, "key.keyboard.keypad.multiply", 332);
			method_1446(field_1668, "key.keyboard.keypad.divide", 331);
			method_1446(field_1668, "key.keyboard.keypad.subtract", 333);
			method_1446(field_1668, "key.keyboard.down", 264);
			method_1446(field_1668, "key.keyboard.left", 263);
			method_1446(field_1668, "key.keyboard.right", 262);
			method_1446(field_1668, "key.keyboard.up", 265);
			method_1446(field_1668, "key.keyboard.apostrophe", 39);
			method_1446(field_1668, "key.keyboard.backslash", 92);
			method_1446(field_1668, "key.keyboard.comma", 44);
			method_1446(field_1668, "key.keyboard.equal", 61);
			method_1446(field_1668, "key.keyboard.grave.accent", 96);
			method_1446(field_1668, "key.keyboard.left.bracket", 91);
			method_1446(field_1668, "key.keyboard.minus", 45);
			method_1446(field_1668, "key.keyboard.period", 46);
			method_1446(field_1668, "key.keyboard.right.bracket", 93);
			method_1446(field_1668, "key.keyboard.semicolon", 59);
			method_1446(field_1668, "key.keyboard.slash", 47);
			method_1446(field_1668, "key.keyboard.space", 32);
			method_1446(field_1668, "key.keyboard.tab", 258);
			method_1446(field_1668, "key.keyboard.left.alt", 342);
			method_1446(field_1668, "key.keyboard.left.control", 341);
			method_1446(field_1668, "key.keyboard.left.shift", 340);
			method_1446(field_1668, "key.keyboard.left.win", 343);
			method_1446(field_1668, "key.keyboard.right.alt", 346);
			method_1446(field_1668, "key.keyboard.right.control", 345);
			method_1446(field_1668, "key.keyboard.right.shift", 344);
			method_1446(field_1668, "key.keyboard.right.win", 347);
			method_1446(field_1668, "key.keyboard.enter", 257);
			method_1446(field_1668, "key.keyboard.escape", 256);
			method_1446(field_1668, "key.keyboard.backspace", 259);
			method_1446(field_1668, "key.keyboard.delete", 261);
			method_1446(field_1668, "key.keyboard.end", 269);
			method_1446(field_1668, "key.keyboard.home", 268);
			method_1446(field_1668, "key.keyboard.insert", 260);
			method_1446(field_1668, "key.keyboard.page.down", 267);
			method_1446(field_1668, "key.keyboard.page.up", 266);
			method_1446(field_1668, "key.keyboard.caps.lock", 280);
			method_1446(field_1668, "key.keyboard.pause", 284);
			method_1446(field_1668, "key.keyboard.scroll.lock", 281);
			method_1446(field_1668, "key.keyboard.menu", 348);
			method_1446(field_1668, "key.keyboard.print.screen", 283);
			method_1446(field_1668, "key.keyboard.world.1", 161);
			method_1446(field_1668, "key.keyboard.world.2", 162);
		}
	}
}
