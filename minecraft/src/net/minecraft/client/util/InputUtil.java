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
	public static final int field_31940 = 48;
	public static final int field_31985 = 49;
	public static final int field_32007 = 50;
	public static final int field_32008 = 51;
	public static final int field_32009 = 52;
	public static final int field_32010 = 53;
	public static final int field_32011 = 54;
	public static final int field_32012 = 55;
	public static final int field_32013 = 56;
	public static final int field_32014 = 57;
	public static final int field_32015 = 65;
	public static final int field_32016 = 66;
	public static final int field_32017 = 67;
	public static final int field_32018 = 68;
	public static final int field_32019 = 69;
	public static final int field_32020 = 70;
	public static final int field_32021 = 71;
	public static final int field_32022 = 72;
	public static final int field_32023 = 73;
	public static final int field_32024 = 74;
	public static final int field_32025 = 75;
	public static final int field_32026 = 76;
	public static final int field_32027 = 77;
	public static final int field_32028 = 78;
	public static final int field_32029 = 79;
	public static final int field_32030 = 80;
	public static final int field_31906 = 81;
	public static final int field_31907 = 82;
	public static final int field_31908 = 83;
	public static final int field_31909 = 84;
	public static final int field_31910 = 85;
	public static final int field_31911 = 86;
	public static final int field_31912 = 87;
	public static final int field_31913 = 88;
	public static final int field_31914 = 89;
	public static final int field_31915 = 90;
	public static final int field_31916 = 290;
	public static final int field_31917 = 291;
	public static final int field_31918 = 292;
	public static final int field_31919 = 293;
	public static final int field_31920 = 294;
	public static final int field_31921 = 295;
	public static final int field_31922 = 296;
	public static final int field_31923 = 297;
	public static final int field_31924 = 298;
	public static final int field_31925 = 299;
	public static final int field_31926 = 300;
	public static final int field_31927 = 301;
	public static final int field_31928 = 302;
	public static final int field_31929 = 303;
	public static final int field_31930 = 304;
	public static final int field_31931 = 305;
	public static final int field_31959 = 306;
	public static final int field_31960 = 307;
	public static final int field_31961 = 308;
	public static final int field_31962 = 309;
	public static final int field_31963 = 310;
	public static final int field_31964 = 311;
	public static final int field_31965 = 312;
	public static final int field_31966 = 313;
	public static final int field_31967 = 314;
	public static final int field_31968 = 282;
	public static final int field_31969 = 320;
	public static final int field_31970 = 321;
	public static final int field_31971 = 322;
	public static final int field_31972 = 323;
	public static final int field_31973 = 324;
	public static final int field_31974 = 325;
	public static final int field_31975 = 326;
	public static final int field_31976 = 327;
	public static final int field_31977 = 328;
	public static final int field_31978 = 329;
	public static final int field_31979 = 330;
	public static final int field_31980 = 335;
	public static final int field_31981 = 336;
	public static final int field_31982 = 264;
	public static final int field_31983 = 263;
	public static final int field_31984 = 262;
	public static final int field_31932 = 265;
	public static final int field_31933 = 334;
	public static final int field_31934 = 39;
	public static final int field_31935 = 92;
	public static final int field_31936 = 44;
	public static final int field_31937 = 61;
	public static final int field_31938 = 96;
	public static final int field_31939 = 91;
	public static final int field_31941 = 45;
	public static final int field_31942 = 332;
	public static final int field_31943 = 46;
	public static final int field_31944 = 93;
	public static final int field_31945 = 59;
	public static final int field_31946 = 47;
	public static final int field_31947 = 32;
	public static final int field_31948 = 258;
	public static final int field_31949 = 342;
	public static final int field_31950 = 341;
	public static final int field_31951 = 340;
	public static final int field_31952 = 343;
	public static final int field_31953 = 346;
	public static final int field_31954 = 345;
	public static final int field_31955 = 344;
	public static final int field_31956 = 347;
	public static final int field_31957 = 257;
	public static final int field_31958 = 256;
	public static final int field_31986 = 259;
	public static final int field_31987 = 261;
	public static final int field_31988 = 269;
	public static final int field_31989 = 268;
	public static final int field_31990 = 260;
	public static final int field_31991 = 267;
	public static final int field_31992 = 266;
	public static final int field_31993 = 280;
	public static final int field_31994 = 284;
	public static final int field_31995 = 281;
	public static final int field_31996 = 283;
	public static final int field_31997 = 1;
	public static final int field_31998 = 0;
	public static final int field_31999 = 2;
	public static final int field_32000 = 0;
	public static final int field_32001 = 2;
	public static final int field_32002 = 1;
	public static final int field_32003 = 2;
	public static final int field_32004 = 208897;
	public static final int field_32005 = 212995;
	public static final int field_32006 = 212993;
	public static final InputUtil.Key UNKNOWN_KEY;

	public static InputUtil.Key fromKeyCode(int keyCode, int scanCode) {
		return keyCode == GLFW.GLFW_KEY_UNKNOWN ? InputUtil.Type.SCANCODE.createFromCode(scanCode) : InputUtil.Type.KEYSYM.createFromCode(keyCode);
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
		UNKNOWN_KEY = InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_UNKNOWN);
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

		public OptionalInt toInt() {
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
		KEYSYM("key.keyboard", (integer, string) -> {
			String string2 = GLFW.glfwGetKeyName(integer, -1);
			return (Text)(string2 != null ? new LiteralText(string2) : new TranslatableText(string));
		}),
		SCANCODE("scancode", (integer, string) -> {
			String string2 = GLFW.glfwGetKeyName(-1, integer);
			return (Text)(string2 != null ? new LiteralText(string2) : new TranslatableText(string));
		}),
		MOUSE(
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
				if (this == MOUSE) {
					i = codex + 1;
				}

				String string = this.name + "." + i;
				return new InputUtil.Key(string, this, codex);
			});
		}

		static {
			mapKey(KEYSYM, "key.keyboard.unknown", GLFW.GLFW_KEY_UNKNOWN);
			mapKey(MOUSE, "key.mouse.left", GLFW.GLFW_MOUSE_BUTTON_LEFT);
			mapKey(MOUSE, "key.mouse.right", GLFW.GLFW_MOUSE_BUTTON_RIGHT);
			mapKey(MOUSE, "key.mouse.middle", GLFW.GLFW_MOUSE_BUTTON_MIDDLE);
			mapKey(MOUSE, "key.mouse.4", GLFW.GLFW_MOUSE_BUTTON_4);
			mapKey(MOUSE, "key.mouse.5", GLFW.GLFW_MOUSE_BUTTON_5);
			mapKey(MOUSE, "key.mouse.6", GLFW.GLFW_MOUSE_BUTTON_6);
			mapKey(MOUSE, "key.mouse.7", GLFW.GLFW_MOUSE_BUTTON_7);
			mapKey(MOUSE, "key.mouse.8", GLFW.GLFW_MOUSE_BUTTON_8);
			mapKey(KEYSYM, "key.keyboard.0", GLFW.GLFW_KEY_0);
			mapKey(KEYSYM, "key.keyboard.1", GLFW.GLFW_KEY_1);
			mapKey(KEYSYM, "key.keyboard.2", GLFW.GLFW_KEY_2);
			mapKey(KEYSYM, "key.keyboard.3", GLFW.GLFW_KEY_3);
			mapKey(KEYSYM, "key.keyboard.4", GLFW.GLFW_KEY_4);
			mapKey(KEYSYM, "key.keyboard.5", GLFW.GLFW_KEY_5);
			mapKey(KEYSYM, "key.keyboard.6", GLFW.GLFW_KEY_6);
			mapKey(KEYSYM, "key.keyboard.7", GLFW.GLFW_KEY_7);
			mapKey(KEYSYM, "key.keyboard.8", GLFW.GLFW_KEY_8);
			mapKey(KEYSYM, "key.keyboard.9", GLFW.GLFW_KEY_9);
			mapKey(KEYSYM, "key.keyboard.a", GLFW.GLFW_KEY_A);
			mapKey(KEYSYM, "key.keyboard.b", GLFW.GLFW_KEY_B);
			mapKey(KEYSYM, "key.keyboard.c", GLFW.GLFW_KEY_C);
			mapKey(KEYSYM, "key.keyboard.d", GLFW.GLFW_KEY_D);
			mapKey(KEYSYM, "key.keyboard.e", GLFW.GLFW_KEY_E);
			mapKey(KEYSYM, "key.keyboard.f", GLFW.GLFW_KEY_F);
			mapKey(KEYSYM, "key.keyboard.g", GLFW.GLFW_KEY_G);
			mapKey(KEYSYM, "key.keyboard.h", GLFW.GLFW_KEY_H);
			mapKey(KEYSYM, "key.keyboard.i", GLFW.GLFW_KEY_I);
			mapKey(KEYSYM, "key.keyboard.j", GLFW.GLFW_KEY_J);
			mapKey(KEYSYM, "key.keyboard.k", GLFW.GLFW_KEY_K);
			mapKey(KEYSYM, "key.keyboard.l", GLFW.GLFW_KEY_L);
			mapKey(KEYSYM, "key.keyboard.m", GLFW.GLFW_KEY_M);
			mapKey(KEYSYM, "key.keyboard.n", GLFW.GLFW_KEY_N);
			mapKey(KEYSYM, "key.keyboard.o", GLFW.GLFW_KEY_O);
			mapKey(KEYSYM, "key.keyboard.p", GLFW.GLFW_KEY_P);
			mapKey(KEYSYM, "key.keyboard.q", GLFW.GLFW_KEY_Q);
			mapKey(KEYSYM, "key.keyboard.r", GLFW.GLFW_KEY_R);
			mapKey(KEYSYM, "key.keyboard.s", GLFW.GLFW_KEY_S);
			mapKey(KEYSYM, "key.keyboard.t", GLFW.GLFW_KEY_T);
			mapKey(KEYSYM, "key.keyboard.u", GLFW.GLFW_KEY_U);
			mapKey(KEYSYM, "key.keyboard.v", GLFW.GLFW_KEY_V);
			mapKey(KEYSYM, "key.keyboard.w", GLFW.GLFW_KEY_W);
			mapKey(KEYSYM, "key.keyboard.x", GLFW.GLFW_KEY_X);
			mapKey(KEYSYM, "key.keyboard.y", GLFW.GLFW_KEY_Y);
			mapKey(KEYSYM, "key.keyboard.z", GLFW.GLFW_KEY_Z);
			mapKey(KEYSYM, "key.keyboard.f1", GLFW.GLFW_KEY_F1);
			mapKey(KEYSYM, "key.keyboard.f2", GLFW.GLFW_KEY_F2);
			mapKey(KEYSYM, "key.keyboard.f3", GLFW.GLFW_KEY_F3);
			mapKey(KEYSYM, "key.keyboard.f4", GLFW.GLFW_KEY_F4);
			mapKey(KEYSYM, "key.keyboard.f5", GLFW.GLFW_KEY_F5);
			mapKey(KEYSYM, "key.keyboard.f6", GLFW.GLFW_KEY_F6);
			mapKey(KEYSYM, "key.keyboard.f7", GLFW.GLFW_KEY_F7);
			mapKey(KEYSYM, "key.keyboard.f8", GLFW.GLFW_KEY_F8);
			mapKey(KEYSYM, "key.keyboard.f9", GLFW.GLFW_KEY_F9);
			mapKey(KEYSYM, "key.keyboard.f10", GLFW.GLFW_KEY_F10);
			mapKey(KEYSYM, "key.keyboard.f11", GLFW.GLFW_KEY_F11);
			mapKey(KEYSYM, "key.keyboard.f12", GLFW.GLFW_KEY_F12);
			mapKey(KEYSYM, "key.keyboard.f13", GLFW.GLFW_KEY_F13);
			mapKey(KEYSYM, "key.keyboard.f14", GLFW.GLFW_KEY_F14);
			mapKey(KEYSYM, "key.keyboard.f15", GLFW.GLFW_KEY_F15);
			mapKey(KEYSYM, "key.keyboard.f16", GLFW.GLFW_KEY_F16);
			mapKey(KEYSYM, "key.keyboard.f17", GLFW.GLFW_KEY_F17);
			mapKey(KEYSYM, "key.keyboard.f18", GLFW.GLFW_KEY_F18);
			mapKey(KEYSYM, "key.keyboard.f19", GLFW.GLFW_KEY_F19);
			mapKey(KEYSYM, "key.keyboard.f20", GLFW.GLFW_KEY_F20);
			mapKey(KEYSYM, "key.keyboard.f21", GLFW.GLFW_KEY_F21);
			mapKey(KEYSYM, "key.keyboard.f22", GLFW.GLFW_KEY_F22);
			mapKey(KEYSYM, "key.keyboard.f23", GLFW.GLFW_KEY_F23);
			mapKey(KEYSYM, "key.keyboard.f24", GLFW.GLFW_KEY_F24);
			mapKey(KEYSYM, "key.keyboard.f25", GLFW.GLFW_KEY_F25);
			mapKey(KEYSYM, "key.keyboard.num.lock", GLFW.GLFW_KEY_NUM_LOCK);
			mapKey(KEYSYM, "key.keyboard.keypad.0", GLFW.GLFW_KEY_KP_0);
			mapKey(KEYSYM, "key.keyboard.keypad.1", GLFW.GLFW_KEY_KP_1);
			mapKey(KEYSYM, "key.keyboard.keypad.2", GLFW.GLFW_KEY_KP_2);
			mapKey(KEYSYM, "key.keyboard.keypad.3", GLFW.GLFW_KEY_KP_3);
			mapKey(KEYSYM, "key.keyboard.keypad.4", GLFW.GLFW_KEY_KP_4);
			mapKey(KEYSYM, "key.keyboard.keypad.5", GLFW.GLFW_KEY_KP_5);
			mapKey(KEYSYM, "key.keyboard.keypad.6", GLFW.GLFW_KEY_KP_6);
			mapKey(KEYSYM, "key.keyboard.keypad.7", GLFW.GLFW_KEY_KP_7);
			mapKey(KEYSYM, "key.keyboard.keypad.8", GLFW.GLFW_KEY_KP_8);
			mapKey(KEYSYM, "key.keyboard.keypad.9", GLFW.GLFW_KEY_KP_9);
			mapKey(KEYSYM, "key.keyboard.keypad.add", GLFW.GLFW_KEY_KP_ADD);
			mapKey(KEYSYM, "key.keyboard.keypad.decimal", GLFW.GLFW_KEY_KP_DECIMAL);
			mapKey(KEYSYM, "key.keyboard.keypad.enter", GLFW.GLFW_KEY_KP_ENTER);
			mapKey(KEYSYM, "key.keyboard.keypad.equal", GLFW.GLFW_KEY_KP_EQUAL);
			mapKey(KEYSYM, "key.keyboard.keypad.multiply", GLFW.GLFW_KEY_KP_MULTIPLY);
			mapKey(KEYSYM, "key.keyboard.keypad.divide", GLFW.GLFW_KEY_KP_DIVIDE);
			mapKey(KEYSYM, "key.keyboard.keypad.subtract", GLFW.GLFW_KEY_KP_SUBTRACT);
			mapKey(KEYSYM, "key.keyboard.down", GLFW.GLFW_KEY_DOWN);
			mapKey(KEYSYM, "key.keyboard.left", GLFW.GLFW_KEY_LEFT);
			mapKey(KEYSYM, "key.keyboard.right", GLFW.GLFW_KEY_RIGHT);
			mapKey(KEYSYM, "key.keyboard.up", GLFW.GLFW_KEY_UP);
			mapKey(KEYSYM, "key.keyboard.apostrophe", GLFW.GLFW_KEY_APOSTROPHE);
			mapKey(KEYSYM, "key.keyboard.backslash", GLFW.GLFW_KEY_BACKSLASH);
			mapKey(KEYSYM, "key.keyboard.comma", GLFW.GLFW_KEY_COMMA);
			mapKey(KEYSYM, "key.keyboard.equal", GLFW.GLFW_KEY_EQUAL);
			mapKey(KEYSYM, "key.keyboard.grave.accent", GLFW.GLFW_KEY_GRAVE_ACCENT);
			mapKey(KEYSYM, "key.keyboard.left.bracket", GLFW.GLFW_KEY_LEFT_BRACKET);
			mapKey(KEYSYM, "key.keyboard.minus", GLFW.GLFW_KEY_MINUS);
			mapKey(KEYSYM, "key.keyboard.period", GLFW.GLFW_KEY_PERIOD);
			mapKey(KEYSYM, "key.keyboard.right.bracket", GLFW.GLFW_KEY_RIGHT_BRACKET);
			mapKey(KEYSYM, "key.keyboard.semicolon", GLFW.GLFW_KEY_SEMICOLON);
			mapKey(KEYSYM, "key.keyboard.slash", GLFW.GLFW_KEY_SLASH);
			mapKey(KEYSYM, "key.keyboard.space", GLFW.GLFW_KEY_SPACE);
			mapKey(KEYSYM, "key.keyboard.tab", GLFW.GLFW_KEY_TAB);
			mapKey(KEYSYM, "key.keyboard.left.alt", GLFW.GLFW_KEY_LEFT_ALT);
			mapKey(KEYSYM, "key.keyboard.left.control", GLFW.GLFW_KEY_LEFT_CONTROL);
			mapKey(KEYSYM, "key.keyboard.left.shift", GLFW.GLFW_KEY_LEFT_SHIFT);
			mapKey(KEYSYM, "key.keyboard.left.win", 343);
			mapKey(KEYSYM, "key.keyboard.right.alt", GLFW.GLFW_KEY_RIGHT_ALT);
			mapKey(KEYSYM, "key.keyboard.right.control", GLFW.GLFW_KEY_RIGHT_CONTROL);
			mapKey(KEYSYM, "key.keyboard.right.shift", GLFW.GLFW_KEY_RIGHT_SHIFT);
			mapKey(KEYSYM, "key.keyboard.right.win", 347);
			mapKey(KEYSYM, "key.keyboard.enter", GLFW.GLFW_KEY_ENTER);
			mapKey(KEYSYM, "key.keyboard.escape", GLFW.GLFW_KEY_ESCAPE);
			mapKey(KEYSYM, "key.keyboard.backspace", GLFW.GLFW_KEY_BACKSPACE);
			mapKey(KEYSYM, "key.keyboard.delete", GLFW.GLFW_KEY_DELETE);
			mapKey(KEYSYM, "key.keyboard.end", GLFW.GLFW_KEY_END);
			mapKey(KEYSYM, "key.keyboard.home", GLFW.GLFW_KEY_HOME);
			mapKey(KEYSYM, "key.keyboard.insert", GLFW.GLFW_KEY_INSERT);
			mapKey(KEYSYM, "key.keyboard.page.down", GLFW.GLFW_KEY_PAGE_DOWN);
			mapKey(KEYSYM, "key.keyboard.page.up", GLFW.GLFW_KEY_PAGE_UP);
			mapKey(KEYSYM, "key.keyboard.caps.lock", GLFW.GLFW_KEY_CAPS_LOCK);
			mapKey(KEYSYM, "key.keyboard.pause", GLFW.GLFW_KEY_PAUSE);
			mapKey(KEYSYM, "key.keyboard.scroll.lock", GLFW.GLFW_KEY_SCROLL_LOCK);
			mapKey(KEYSYM, "key.keyboard.menu", GLFW.GLFW_KEY_MENU);
			mapKey(KEYSYM, "key.keyboard.print.screen", GLFW.GLFW_KEY_PRINT_SCREEN);
			mapKey(KEYSYM, "key.keyboard.world.1", GLFW.GLFW_KEY_WORLD_1);
			mapKey(KEYSYM, "key.keyboard.world.2", GLFW.GLFW_KEY_WORLD_2);
		}
	}
}
