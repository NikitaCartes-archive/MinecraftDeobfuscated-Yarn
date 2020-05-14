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
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Lazy;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

@Environment(EnvType.CLIENT)
public class InputUtil {
	@Nullable
	private static final MethodHandle GLFW_RAW_MOUSE_MOTION_SUPPORTED_HANDLE;
	private static final int GLFW_RAW_MOUSE_MOTION;
	public static final InputUtil.Key UNKNOWN_KEY;

	public static InputUtil.Key fromKeyCode(int keyCode, int scanCode) {
		return keyCode == -1 ? InputUtil.Type.SCANCODE.createFromCode(scanCode) : InputUtil.Type.KEYSYM.createFromCode(keyCode);
	}

	public static InputUtil.Key fromTranslationKey(String translationKey) {
		if (InputUtil.Key.KEYS.containsKey(translationKey)) {
			return (InputUtil.Key)InputUtil.Key.KEYS.get(translationKey);
		} else {
			for (InputUtil.Type type : InputUtil.Type.values()) {
				if (translationKey.startsWith(type.name)) {
					String string = translationKey.substring(type.name.length() + 1);
					return type.createFromCode(Integer.parseInt(string));
				}
			}

			throw new IllegalArgumentException("Unknown key name: " + translationKey);
		}
	}

	public static boolean isKeyPressed(long handle, int code) {
		return GLFW.glfwGetKey(handle, code) == 1;
	}

	public static void setKeyboardCallbacks(long handle, GLFWKeyCallbackI keyCallback, GLFWCharModsCallbackI charModsCallback) {
		GLFW.glfwSetKeyCallback(handle, keyCallback);
		GLFW.glfwSetCharModsCallback(handle, charModsCallback);
	}

	public static void setMouseCallbacks(
		long handle, GLFWCursorPosCallbackI cursorPosCallback, GLFWMouseButtonCallbackI mouseButtonCallback, GLFWScrollCallbackI scrollCallback
	) {
		GLFW.glfwSetCursorPosCallback(handle, cursorPosCallback);
		GLFW.glfwSetMouseButtonCallback(handle, mouseButtonCallback);
		GLFW.glfwSetScrollCallback(handle, scrollCallback);
	}

	public static void setCursorParameters(long handler, int i, double d, double e) {
		GLFW.glfwSetCursorPos(handler, d, e);
		GLFW.glfwSetInputMode(handler, 208897, i);
	}

	public static boolean isRawMouseMotionSupported() {
		try {
			return GLFW_RAW_MOUSE_MOTION_SUPPORTED_HANDLE != null && (boolean)GLFW_RAW_MOUSE_MOTION_SUPPORTED_HANDLE.invokeExact();
		} catch (Throwable var1) {
			throw new RuntimeException(var1);
		}
	}

	public static void setRawMouseMotionMode(long window, boolean value) {
		if (isRawMouseMotionSupported()) {
			GLFW.glfwSetInputMode(window, GLFW_RAW_MOUSE_MOTION, value ? 1 : 0);
		}
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

		GLFW_RAW_MOUSE_MOTION_SUPPORTED_HANDLE = methodHandle;
		GLFW_RAW_MOUSE_MOTION = i;
		UNKNOWN_KEY = InputUtil.Type.KEYSYM.createFromCode(-1);
	}

	@Environment(EnvType.CLIENT)
	public static final class Key {
		private final String translationKey;
		private final InputUtil.Type type;
		private final int code;
		private final Lazy<Text> localizedText;
		private static final Map<String, InputUtil.Key> KEYS = Maps.<String, InputUtil.Key>newHashMap();

		private Key(String translationKey, InputUtil.Type type, int code) {
			this.translationKey = translationKey;
			this.type = type;
			this.code = code;
			this.localizedText = new Lazy<>(() -> (Text)type.textTranslator.apply(code, translationKey));
			KEYS.put(translationKey, this);
		}

		public InputUtil.Type getCategory() {
			return this.type;
		}

		public int getCode() {
			return this.code;
		}

		public String getTranslationKey() {
			return this.translationKey;
		}

		public Text getLocalizedText() {
			return this.localizedText.get();
		}

		public boolean equals(Object other) {
			if (this == other) {
				return true;
			} else if (other != null && this.getClass() == other.getClass()) {
				InputUtil.Key key = (InputUtil.Key)other;
				return this.code == key.code && this.type == key.type;
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.type, this.code});
		}

		public String toString() {
			return this.translationKey;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		KEYSYM("key.keyboard", (integer, string) -> {
			String string2 = GLFW.glfwGetKeyName(integer, -1);
			return (Text)(string2 != null ? new LiteralText(string2) : new TranslatableText(string));
		}),
		SCANCODE("scancode", (integer, string) -> {
			String string2 = GLFW.glfwGetKeyName(-1, integer);
			return (Text)(string2 != null ? new LiteralText(string2) : new TranslatableText(string));
		}),
		MOUSE("key.mouse", (integer, string) -> new TranslatableText(string));

		private final Int2ObjectMap<InputUtil.Key> map = new Int2ObjectOpenHashMap<>();
		private final String name;
		private final BiFunction<Integer, String, Text> textTranslator;

		private static void mapKey(InputUtil.Type type, String translationKey, int keyCode) {
			InputUtil.Key key = new InputUtil.Key(translationKey, type, keyCode);
			type.map.put(keyCode, key);
		}

		private Type(String name, BiFunction<Integer, String, Text> textTranslator) {
			this.name = name;
			this.textTranslator = textTranslator;
		}

		public InputUtil.Key createFromCode(int code) {
			return this.map.computeIfAbsent(code, codex -> {
				int i = codex;
				if (this == MOUSE) {
					i = codex + 1;
				}

				String string = this.name + "." + i;
				return new InputUtil.Key(string, this, codex);
			});
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
