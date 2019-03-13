package net.minecraft.client.util;

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
public class InputUtil {
	public static final InputUtil.KeyCode UNKNOWN_KEYCODE = InputUtil.Type.field_1668.createFromCode(-1);

	public static InputUtil.KeyCode getKeyCode(int i, int j) {
		return i == -1 ? InputUtil.Type.field_1671.createFromCode(j) : InputUtil.Type.field_1668.createFromCode(i);
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

	@Nullable
	public static String getKeycodeName(int i) {
		return GLFW.glfwGetKeyName(i, -1);
	}

	@Nullable
	public static String getScancodeName(int i) {
		return GLFW.glfwGetKeyName(-1, i);
	}

	@Environment(EnvType.CLIENT)
	public static final class KeyCode {
		private final String name;
		private final InputUtil.Type field_1666;
		private final int keyCode;
		private static final Map<String, InputUtil.KeyCode> NAMES = Maps.<String, InputUtil.KeyCode>newHashMap();

		private KeyCode(String string, InputUtil.Type type, int i) {
			this.name = string;
			this.field_1666 = type;
			this.keyCode = i;
			NAMES.put(string, this);
		}

		public InputUtil.Type method_1442() {
			return this.field_1666;
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
				return this.keyCode == keyCode.keyCode && this.field_1666 == keyCode.field_1666;
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.field_1666, this.keyCode});
		}

		public String toString() {
			return this.name;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		field_1668("key.keyboard"),
		field_1671("scancode"),
		field_1672("key.mouse");

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
				if (this == field_1672) {
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
			mapKey(field_1668, "key.keyboard.unknown", -1);
			mapKey(field_1672, "key.mouse.left", 0);
			mapKey(field_1672, "key.mouse.right", 1);
			mapKey(field_1672, "key.mouse.middle", 2);
			mapKey(field_1672, "key.mouse.4", 3);
			mapKey(field_1672, "key.mouse.5", 4);
			mapKey(field_1672, "key.mouse.6", 5);
			mapKey(field_1672, "key.mouse.7", 6);
			mapKey(field_1672, "key.mouse.8", 7);
			mapKey(field_1668, "key.keyboard.0", 48);
			mapKey(field_1668, "key.keyboard.1", 49);
			mapKey(field_1668, "key.keyboard.2", 50);
			mapKey(field_1668, "key.keyboard.3", 51);
			mapKey(field_1668, "key.keyboard.4", 52);
			mapKey(field_1668, "key.keyboard.5", 53);
			mapKey(field_1668, "key.keyboard.6", 54);
			mapKey(field_1668, "key.keyboard.7", 55);
			mapKey(field_1668, "key.keyboard.8", 56);
			mapKey(field_1668, "key.keyboard.9", 57);
			mapKey(field_1668, "key.keyboard.a", 65);
			mapKey(field_1668, "key.keyboard.b", 66);
			mapKey(field_1668, "key.keyboard.c", 67);
			mapKey(field_1668, "key.keyboard.d", 68);
			mapKey(field_1668, "key.keyboard.e", 69);
			mapKey(field_1668, "key.keyboard.f", 70);
			mapKey(field_1668, "key.keyboard.g", 71);
			mapKey(field_1668, "key.keyboard.h", 72);
			mapKey(field_1668, "key.keyboard.i", 73);
			mapKey(field_1668, "key.keyboard.j", 74);
			mapKey(field_1668, "key.keyboard.k", 75);
			mapKey(field_1668, "key.keyboard.l", 76);
			mapKey(field_1668, "key.keyboard.m", 77);
			mapKey(field_1668, "key.keyboard.n", 78);
			mapKey(field_1668, "key.keyboard.o", 79);
			mapKey(field_1668, "key.keyboard.p", 80);
			mapKey(field_1668, "key.keyboard.q", 81);
			mapKey(field_1668, "key.keyboard.r", 82);
			mapKey(field_1668, "key.keyboard.s", 83);
			mapKey(field_1668, "key.keyboard.t", 84);
			mapKey(field_1668, "key.keyboard.u", 85);
			mapKey(field_1668, "key.keyboard.v", 86);
			mapKey(field_1668, "key.keyboard.w", 87);
			mapKey(field_1668, "key.keyboard.x", 88);
			mapKey(field_1668, "key.keyboard.y", 89);
			mapKey(field_1668, "key.keyboard.z", 90);
			mapKey(field_1668, "key.keyboard.f1", 290);
			mapKey(field_1668, "key.keyboard.f2", 291);
			mapKey(field_1668, "key.keyboard.f3", 292);
			mapKey(field_1668, "key.keyboard.f4", 293);
			mapKey(field_1668, "key.keyboard.f5", 294);
			mapKey(field_1668, "key.keyboard.f6", 295);
			mapKey(field_1668, "key.keyboard.f7", 296);
			mapKey(field_1668, "key.keyboard.f8", 297);
			mapKey(field_1668, "key.keyboard.f9", 298);
			mapKey(field_1668, "key.keyboard.f10", 299);
			mapKey(field_1668, "key.keyboard.f11", 300);
			mapKey(field_1668, "key.keyboard.f12", 301);
			mapKey(field_1668, "key.keyboard.f13", 302);
			mapKey(field_1668, "key.keyboard.f14", 303);
			mapKey(field_1668, "key.keyboard.f15", 304);
			mapKey(field_1668, "key.keyboard.f16", 305);
			mapKey(field_1668, "key.keyboard.f17", 306);
			mapKey(field_1668, "key.keyboard.f18", 307);
			mapKey(field_1668, "key.keyboard.f19", 308);
			mapKey(field_1668, "key.keyboard.f20", 309);
			mapKey(field_1668, "key.keyboard.f21", 310);
			mapKey(field_1668, "key.keyboard.f22", 311);
			mapKey(field_1668, "key.keyboard.f23", 312);
			mapKey(field_1668, "key.keyboard.f24", 313);
			mapKey(field_1668, "key.keyboard.f25", 314);
			mapKey(field_1668, "key.keyboard.num.lock", 282);
			mapKey(field_1668, "key.keyboard.keypad.0", 320);
			mapKey(field_1668, "key.keyboard.keypad.1", 321);
			mapKey(field_1668, "key.keyboard.keypad.2", 322);
			mapKey(field_1668, "key.keyboard.keypad.3", 323);
			mapKey(field_1668, "key.keyboard.keypad.4", 324);
			mapKey(field_1668, "key.keyboard.keypad.5", 325);
			mapKey(field_1668, "key.keyboard.keypad.6", 326);
			mapKey(field_1668, "key.keyboard.keypad.7", 327);
			mapKey(field_1668, "key.keyboard.keypad.8", 328);
			mapKey(field_1668, "key.keyboard.keypad.9", 329);
			mapKey(field_1668, "key.keyboard.keypad.add", 334);
			mapKey(field_1668, "key.keyboard.keypad.decimal", 330);
			mapKey(field_1668, "key.keyboard.keypad.enter", 335);
			mapKey(field_1668, "key.keyboard.keypad.equal", 336);
			mapKey(field_1668, "key.keyboard.keypad.multiply", 332);
			mapKey(field_1668, "key.keyboard.keypad.divide", 331);
			mapKey(field_1668, "key.keyboard.keypad.subtract", 333);
			mapKey(field_1668, "key.keyboard.down", 264);
			mapKey(field_1668, "key.keyboard.left", 263);
			mapKey(field_1668, "key.keyboard.right", 262);
			mapKey(field_1668, "key.keyboard.up", 265);
			mapKey(field_1668, "key.keyboard.apostrophe", 39);
			mapKey(field_1668, "key.keyboard.backslash", 92);
			mapKey(field_1668, "key.keyboard.comma", 44);
			mapKey(field_1668, "key.keyboard.equal", 61);
			mapKey(field_1668, "key.keyboard.grave.accent", 96);
			mapKey(field_1668, "key.keyboard.left.bracket", 91);
			mapKey(field_1668, "key.keyboard.minus", 45);
			mapKey(field_1668, "key.keyboard.period", 46);
			mapKey(field_1668, "key.keyboard.right.bracket", 93);
			mapKey(field_1668, "key.keyboard.semicolon", 59);
			mapKey(field_1668, "key.keyboard.slash", 47);
			mapKey(field_1668, "key.keyboard.space", 32);
			mapKey(field_1668, "key.keyboard.tab", 258);
			mapKey(field_1668, "key.keyboard.left.alt", 342);
			mapKey(field_1668, "key.keyboard.left.control", 341);
			mapKey(field_1668, "key.keyboard.left.shift", 340);
			mapKey(field_1668, "key.keyboard.left.win", 343);
			mapKey(field_1668, "key.keyboard.right.alt", 346);
			mapKey(field_1668, "key.keyboard.right.control", 345);
			mapKey(field_1668, "key.keyboard.right.shift", 344);
			mapKey(field_1668, "key.keyboard.right.win", 347);
			mapKey(field_1668, "key.keyboard.enter", 257);
			mapKey(field_1668, "key.keyboard.escape", 256);
			mapKey(field_1668, "key.keyboard.backspace", 259);
			mapKey(field_1668, "key.keyboard.delete", 261);
			mapKey(field_1668, "key.keyboard.end", 269);
			mapKey(field_1668, "key.keyboard.home", 268);
			mapKey(field_1668, "key.keyboard.insert", 260);
			mapKey(field_1668, "key.keyboard.page.down", 267);
			mapKey(field_1668, "key.keyboard.page.up", 266);
			mapKey(field_1668, "key.keyboard.caps.lock", 280);
			mapKey(field_1668, "key.keyboard.pause", 284);
			mapKey(field_1668, "key.keyboard.scroll.lock", 281);
			mapKey(field_1668, "key.keyboard.menu", 348);
			mapKey(field_1668, "key.keyboard.print.screen", 283);
			mapKey(field_1668, "key.keyboard.world.1", 161);
			mapKey(field_1668, "key.keyboard.world.2", 162);
		}
	}
}
