package net.minecraft.client.util;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
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
public class InputUtil {
	@Nullable
	private static final MethodHandle field_20333;
	private static final int field_20334;
	public static final InputUtil.KeyCode UNKNOWN_KEYCODE;

	public static InputUtil.KeyCode getKeyCode(int i, int j) {
		return i == -1 ? InputUtil.Type.SCANCODE.createFromCode(j) : InputUtil.Type.KEYSYM.createFromCode(i);
	}

	public static InputUtil.KeyCode fromName(String string) {
		if (InputUtil.KeyCode.NAMES.containsKey(string)) {
			return (InputUtil.KeyCode)InputUtil.KeyCode.NAMES.get(string);
		} else {
			for (InputUtil.Type type : InputUtil.Type.values()) {
				if (string.startsWith(type.name)) {
					String string2 = string.substring(type.name.length() + 1);
					return type.createFromCode(Integer.parseInt(string2));
				}
			}

			throw new IllegalArgumentException("Unknown key name: " + string);
		}
	}

	public static boolean isKeyPressed(long l, int i) {
		return GLFW.glfwGetKey(l, i) == 1;
	}

	public static void setKeyboardCallbacks(long l, GLFWKeyCallbackI gLFWKeyCallbackI, GLFWCharModsCallbackI gLFWCharModsCallbackI) {
		GLFW.glfwSetKeyCallback(l, gLFWKeyCallbackI);
		GLFW.glfwSetCharModsCallback(l, gLFWCharModsCallbackI);
	}

	public static void setMouseCallbacks(
		long l, GLFWCursorPosCallbackI gLFWCursorPosCallbackI, GLFWMouseButtonCallbackI gLFWMouseButtonCallbackI, GLFWScrollCallbackI gLFWScrollCallbackI
	) {
		GLFW.glfwSetCursorPosCallback(l, gLFWCursorPosCallbackI);
		GLFW.glfwSetMouseButtonCallback(l, gLFWMouseButtonCallbackI);
		GLFW.glfwSetScrollCallback(l, gLFWScrollCallbackI);
	}

	public static void setCursorParameters(long l, int i, double d, double e) {
		GLFW.glfwSetCursorPos(l, d, e);
		GLFW.glfwSetInputMode(l, 208897, i);
	}

	public static boolean method_21735() {
		try {
			return field_20333 != null && (boolean)field_20333.invokeExact();
		} catch (Throwable var1) {
			throw new RuntimeException(var1);
		}
	}

	public static void method_21736(long l, boolean bl) {
		if (method_21735()) {
			GLFW.glfwSetInputMode(l, field_20334, bl ? 1 : 0);
		}
	}

	@Nullable
	public static String getKeycodeName(int i) {
		return GLFW.glfwGetKeyName(i, -1);
	}

	@Nullable
	public static String getScancodeName(int i) {
		return GLFW.glfwGetKeyName(-1, i);
	}

	static {
		Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(boolean.class);
		MethodHandle methodHandle = null;
		int i = 0;

		try {
			methodHandle = lookup.findStatic(GLFW.class, "glfwRawMouseMotionSupported", methodType);
			MethodHandle methodHandle2 = lookup.findStaticGetter(GLFW.class, "GLFW_RAW_MOUSE_MOTION", int.class);
			i = (int)methodHandle2.invokeExact();
		} catch (NoSuchFieldException | NoSuchMethodException var5) {
		} catch (Throwable var6) {
			throw new RuntimeException(var6);
		}

		field_20333 = methodHandle;
		field_20334 = i;
		UNKNOWN_KEYCODE = InputUtil.Type.KEYSYM.createFromCode(-1);
	}

	@Environment(EnvType.CLIENT)
	public static final class KeyCode {
		private final String name;
		private final InputUtil.Type type;
		private final int keyCode;
		private static final Map<String, InputUtil.KeyCode> NAMES = Maps.<String, InputUtil.KeyCode>newHashMap();

		private KeyCode(String string, InputUtil.Type type, int i) {
			this.name = string;
			this.type = type;
			this.keyCode = i;
			NAMES.put(string, this);
		}

		public InputUtil.Type getCategory() {
			return this.type;
		}

		public int getKeyCode() {
			return this.keyCode;
		}

		public String getName() {
			return this.name;
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				InputUtil.KeyCode keyCode = (InputUtil.KeyCode)object;
				return this.keyCode == keyCode.keyCode && this.type == keyCode.type;
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.type, this.keyCode});
		}

		public String toString() {
			return this.name;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		KEYSYM("key.keyboard"),
		SCANCODE("scancode"),
		MOUSE("key.mouse");

		private static final String[] mouseButtons = new String[]{"left", "middle", "right"};
		private final Int2ObjectMap<InputUtil.KeyCode> map = new Int2ObjectOpenHashMap<>();
		private final String name;

		private static void mapKey(InputUtil.Type type, String string, int i) {
			InputUtil.KeyCode keyCode = new InputUtil.KeyCode(string, type, i);
			type.map.put(i, keyCode);
		}

		private Type(String string2) {
			this.name = string2;
		}

		public InputUtil.KeyCode createFromCode(int i) {
			if (this.map.containsKey(i)) {
				return this.map.get(i);
			} else {
				String string;
				if (this == MOUSE) {
					if (i <= 2) {
						string = "." + mouseButtons[i];
					} else {
						string = "." + (i + 1);
					}
				} else {
					string = "." + i;
				}

				InputUtil.KeyCode keyCode = new InputUtil.KeyCode(this.name + string, this, i);
				this.map.put(i, keyCode);
				return keyCode;
			}
		}

		public String getName() {
			return this.name;
		}

		static {
			mapKey(KEYSYM, "key.keyboard.unknown", -1);
			mapKey(MOUSE, "key.mouse.left", 0);
			mapKey(MOUSE, "key.mouse.right", 1);
			mapKey(MOUSE, "key.mouse.middle", 2);
			mapKey(MOUSE, "key.mouse.4", 3);
			mapKey(MOUSE, "key.mouse.5", 4);
			mapKey(MOUSE, "key.mouse.6", 5);
			mapKey(MOUSE, "key.mouse.7", 6);
			mapKey(MOUSE, "key.mouse.8", 7);
			mapKey(KEYSYM, "key.keyboard.0", 48);
			mapKey(KEYSYM, "key.keyboard.1", 49);
			mapKey(KEYSYM, "key.keyboard.2", 50);
			mapKey(KEYSYM, "key.keyboard.3", 51);
			mapKey(KEYSYM, "key.keyboard.4", 52);
			mapKey(KEYSYM, "key.keyboard.5", 53);
			mapKey(KEYSYM, "key.keyboard.6", 54);
			mapKey(KEYSYM, "key.keyboard.7", 55);
			mapKey(KEYSYM, "key.keyboard.8", 56);
			mapKey(KEYSYM, "key.keyboard.9", 57);
			mapKey(KEYSYM, "key.keyboard.a", 65);
			mapKey(KEYSYM, "key.keyboard.b", 66);
			mapKey(KEYSYM, "key.keyboard.c", 67);
			mapKey(KEYSYM, "key.keyboard.d", 68);
			mapKey(KEYSYM, "key.keyboard.e", 69);
			mapKey(KEYSYM, "key.keyboard.f", 70);
			mapKey(KEYSYM, "key.keyboard.g", 71);
			mapKey(KEYSYM, "key.keyboard.h", 72);
			mapKey(KEYSYM, "key.keyboard.i", 73);
			mapKey(KEYSYM, "key.keyboard.j", 74);
			mapKey(KEYSYM, "key.keyboard.k", 75);
			mapKey(KEYSYM, "key.keyboard.l", 76);
			mapKey(KEYSYM, "key.keyboard.m", 77);
			mapKey(KEYSYM, "key.keyboard.n", 78);
			mapKey(KEYSYM, "key.keyboard.o", 79);
			mapKey(KEYSYM, "key.keyboard.p", 80);
			mapKey(KEYSYM, "key.keyboard.q", 81);
			mapKey(KEYSYM, "key.keyboard.r", 82);
			mapKey(KEYSYM, "key.keyboard.s", 83);
			mapKey(KEYSYM, "key.keyboard.t", 84);
			mapKey(KEYSYM, "key.keyboard.u", 85);
			mapKey(KEYSYM, "key.keyboard.v", 86);
			mapKey(KEYSYM, "key.keyboard.w", 87);
			mapKey(KEYSYM, "key.keyboard.x", 88);
			mapKey(KEYSYM, "key.keyboard.y", 89);
			mapKey(KEYSYM, "key.keyboard.z", 90);
			mapKey(KEYSYM, "key.keyboard.f1", 290);
			mapKey(KEYSYM, "key.keyboard.f2", 291);
			mapKey(KEYSYM, "key.keyboard.f3", 292);
			mapKey(KEYSYM, "key.keyboard.f4", 293);
			mapKey(KEYSYM, "key.keyboard.f5", 294);
			mapKey(KEYSYM, "key.keyboard.f6", 295);
			mapKey(KEYSYM, "key.keyboard.f7", 296);
			mapKey(KEYSYM, "key.keyboard.f8", 297);
			mapKey(KEYSYM, "key.keyboard.f9", 298);
			mapKey(KEYSYM, "key.keyboard.f10", 299);
			mapKey(KEYSYM, "key.keyboard.f11", 300);
			mapKey(KEYSYM, "key.keyboard.f12", 301);
			mapKey(KEYSYM, "key.keyboard.f13", 302);
			mapKey(KEYSYM, "key.keyboard.f14", 303);
			mapKey(KEYSYM, "key.keyboard.f15", 304);
			mapKey(KEYSYM, "key.keyboard.f16", 305);
			mapKey(KEYSYM, "key.keyboard.f17", 306);
			mapKey(KEYSYM, "key.keyboard.f18", 307);
			mapKey(KEYSYM, "key.keyboard.f19", 308);
			mapKey(KEYSYM, "key.keyboard.f20", 309);
			mapKey(KEYSYM, "key.keyboard.f21", 310);
			mapKey(KEYSYM, "key.keyboard.f22", 311);
			mapKey(KEYSYM, "key.keyboard.f23", 312);
			mapKey(KEYSYM, "key.keyboard.f24", 313);
			mapKey(KEYSYM, "key.keyboard.f25", 314);
			mapKey(KEYSYM, "key.keyboard.num.lock", 282);
			mapKey(KEYSYM, "key.keyboard.keypad.0", 320);
			mapKey(KEYSYM, "key.keyboard.keypad.1", 321);
			mapKey(KEYSYM, "key.keyboard.keypad.2", 322);
			mapKey(KEYSYM, "key.keyboard.keypad.3", 323);
			mapKey(KEYSYM, "key.keyboard.keypad.4", 324);
			mapKey(KEYSYM, "key.keyboard.keypad.5", 325);
			mapKey(KEYSYM, "key.keyboard.keypad.6", 326);
			mapKey(KEYSYM, "key.keyboard.keypad.7", 327);
			mapKey(KEYSYM, "key.keyboard.keypad.8", 328);
			mapKey(KEYSYM, "key.keyboard.keypad.9", 329);
			mapKey(KEYSYM, "key.keyboard.keypad.add", 334);
			mapKey(KEYSYM, "key.keyboard.keypad.decimal", 330);
			mapKey(KEYSYM, "key.keyboard.keypad.enter", 335);
			mapKey(KEYSYM, "key.keyboard.keypad.equal", 336);
			mapKey(KEYSYM, "key.keyboard.keypad.multiply", 332);
			mapKey(KEYSYM, "key.keyboard.keypad.divide", 331);
			mapKey(KEYSYM, "key.keyboard.keypad.subtract", 333);
			mapKey(KEYSYM, "key.keyboard.down", 264);
			mapKey(KEYSYM, "key.keyboard.left", 263);
			mapKey(KEYSYM, "key.keyboard.right", 262);
			mapKey(KEYSYM, "key.keyboard.up", 265);
			mapKey(KEYSYM, "key.keyboard.apostrophe", 39);
			mapKey(KEYSYM, "key.keyboard.backslash", 92);
			mapKey(KEYSYM, "key.keyboard.comma", 44);
			mapKey(KEYSYM, "key.keyboard.equal", 61);
			mapKey(KEYSYM, "key.keyboard.grave.accent", 96);
			mapKey(KEYSYM, "key.keyboard.left.bracket", 91);
			mapKey(KEYSYM, "key.keyboard.minus", 45);
			mapKey(KEYSYM, "key.keyboard.period", 46);
			mapKey(KEYSYM, "key.keyboard.right.bracket", 93);
			mapKey(KEYSYM, "key.keyboard.semicolon", 59);
			mapKey(KEYSYM, "key.keyboard.slash", 47);
			mapKey(KEYSYM, "key.keyboard.space", 32);
			mapKey(KEYSYM, "key.keyboard.tab", 258);
			mapKey(KEYSYM, "key.keyboard.left.alt", 342);
			mapKey(KEYSYM, "key.keyboard.left.control", 341);
			mapKey(KEYSYM, "key.keyboard.left.shift", 340);
			mapKey(KEYSYM, "key.keyboard.left.win", 343);
			mapKey(KEYSYM, "key.keyboard.right.alt", 346);
			mapKey(KEYSYM, "key.keyboard.right.control", 345);
			mapKey(KEYSYM, "key.keyboard.right.shift", 344);
			mapKey(KEYSYM, "key.keyboard.right.win", 347);
			mapKey(KEYSYM, "key.keyboard.enter", 257);
			mapKey(KEYSYM, "key.keyboard.escape", 256);
			mapKey(KEYSYM, "key.keyboard.backspace", 259);
			mapKey(KEYSYM, "key.keyboard.delete", 261);
			mapKey(KEYSYM, "key.keyboard.end", 269);
			mapKey(KEYSYM, "key.keyboard.home", 268);
			mapKey(KEYSYM, "key.keyboard.insert", 260);
			mapKey(KEYSYM, "key.keyboard.page.down", 267);
			mapKey(KEYSYM, "key.keyboard.page.up", 266);
			mapKey(KEYSYM, "key.keyboard.caps.lock", 280);
			mapKey(KEYSYM, "key.keyboard.pause", 284);
			mapKey(KEYSYM, "key.keyboard.scroll.lock", 281);
			mapKey(KEYSYM, "key.keyboard.menu", 348);
			mapKey(KEYSYM, "key.keyboard.print.screen", 283);
			mapKey(KEYSYM, "key.keyboard.world.1", 161);
			mapKey(KEYSYM, "key.keyboard.world.2", 162);
		}
	}
}
