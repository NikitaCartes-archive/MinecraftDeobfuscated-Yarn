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
import java.util.OptionalInt;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Language;
import net.minecraft.util.Lazy;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWDropCallbackI;
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
		return keyCode == -1 ? InputUtil.Type.field_1671.createFromCode(scanCode) : InputUtil.Type.field_1668.createFromCode(keyCode);
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
		long handle,
		GLFWCursorPosCallbackI cursorPosCallback,
		GLFWMouseButtonCallbackI mouseButtonCallback,
		GLFWScrollCallbackI scrollCallback,
		GLFWDropCallbackI gLFWDropCallbackI
	) {
		GLFW.glfwSetCursorPosCallback(handle, cursorPosCallback);
		GLFW.glfwSetMouseButtonCallback(handle, mouseButtonCallback);
		GLFW.glfwSetScrollCallback(handle, scrollCallback);
		GLFW.glfwSetDropCallback(handle, gLFWDropCallbackI);
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
		UNKNOWN_KEY = InputUtil.Type.field_1668.createFromCode(-1);
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

		public OptionalInt method_30103() {
			if (this.code >= 48 && this.code <= 57) {
				return OptionalInt.of(this.code - 48);
			} else {
				return this.code >= 320 && this.code <= 329 ? OptionalInt.of(this.code - 320) : OptionalInt.empty();
			}
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
		field_1668("key.keyboard", (integer, string) -> {
			String string2 = GLFW.glfwGetKeyName(integer, -1);
			return (Text)(string2 != null ? new LiteralText(string2) : new TranslatableText(string));
		}),
		field_1671("scancode", (integer, string) -> {
			String string2 = GLFW.glfwGetKeyName(-1, integer);
			return (Text)(string2 != null ? new LiteralText(string2) : new TranslatableText(string));
		}),
		field_1672(
			"key.mouse",
			(integer, string) -> Language.getInstance().hasTranslation(string) ? new TranslatableText(string) : new TranslatableText("key.mouse", integer + 1)
		);

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
				if (this == field_1672) {
					i = codex + 1;
				}

				String string = this.name + "." + i;
				return new InputUtil.Key(string, this, codex);
			});
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
