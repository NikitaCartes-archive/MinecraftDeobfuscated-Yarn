/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Language;
import net.minecraft.util.Lazy;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWDropCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

@Environment(value=EnvType.CLIENT)
public class InputUtil {
    @Nullable
    private static final MethodHandle GLFW_RAW_MOUSE_MOTION_SUPPORTED_HANDLE;
    private static final int GLFW_RAW_MOUSE_MOTION;
    public static final Key UNKNOWN_KEY;

    public static Key fromKeyCode(int keyCode, int scanCode) {
        if (keyCode == GLFW.GLFW_KEY_UNKNOWN) {
            return Type.SCANCODE.createFromCode(scanCode);
        }
        return Type.KEYSYM.createFromCode(keyCode);
    }

    public static Key fromTranslationKey(String translationKey) {
        if (Key.KEYS.containsKey(translationKey)) {
            return (Key)Key.KEYS.get(translationKey);
        }
        for (Type type : Type.values()) {
            if (!translationKey.startsWith(type.name)) continue;
            String string = translationKey.substring(type.name.length() + 1);
            return type.createFromCode(Integer.parseInt(string));
        }
        throw new IllegalArgumentException("Unknown key name: " + translationKey);
    }

    public static boolean isKeyPressed(long handle, int code) {
        return GLFW.glfwGetKey(handle, code) == 1;
    }

    public static void setKeyboardCallbacks(long handle, GLFWKeyCallbackI keyCallback, GLFWCharModsCallbackI charModsCallback) {
        GLFW.glfwSetKeyCallback(handle, keyCallback);
        GLFW.glfwSetCharModsCallback(handle, charModsCallback);
    }

    public static void setMouseCallbacks(long handle, GLFWCursorPosCallbackI cursorPosCallback, GLFWMouseButtonCallbackI mouseButtonCallback, GLFWScrollCallbackI scrollCallback, GLFWDropCallbackI gLFWDropCallbackI) {
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
            return GLFW_RAW_MOUSE_MOTION_SUPPORTED_HANDLE != null && GLFW_RAW_MOUSE_MOTION_SUPPORTED_HANDLE.invokeExact();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void setRawMouseMotionMode(long window, boolean value) {
        if (InputUtil.isRawMouseMotionSupported()) {
            GLFW.glfwSetInputMode(window, GLFW_RAW_MOUSE_MOTION, value ? 1 : 0);
        }
    }

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodType methodType = MethodType.methodType(Boolean.TYPE);
        MethodHandle methodHandle = null;
        int i = 0;
        try {
            methodHandle = lookup.findStatic(GLFW.class, "glfwRawMouseMotionSupported", methodType);
            MethodHandle methodHandle2 = lookup.findStaticGetter(GLFW.class, "GLFW_RAW_MOUSE_MOTION", Integer.TYPE);
            i = methodHandle2.invokeExact();
        } catch (NoSuchFieldException | NoSuchMethodException methodHandle2) {
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
        GLFW_RAW_MOUSE_MOTION_SUPPORTED_HANDLE = methodHandle;
        GLFW_RAW_MOUSE_MOTION = i;
        UNKNOWN_KEY = Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_UNKNOWN);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Key {
        private final String translationKey;
        private final Type type;
        private final int code;
        private final Lazy<Text> localizedText;
        private static final Map<String, Key> KEYS = Maps.newHashMap();

        private Key(String translationKey, Type type, int code) {
            this.translationKey = translationKey;
            this.type = type;
            this.code = code;
            this.localizedText = new Lazy<Text>(() -> (Text)type.textTranslator.apply(code, translationKey));
            KEYS.put(translationKey, this);
        }

        public Type getCategory() {
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
            }
            if (this.code >= 320 && this.code <= 329) {
                return OptionalInt.of(this.code - 320);
            }
            return OptionalInt.empty();
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            Key key = (Key)other;
            return this.code == key.code && this.type == key.type;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.type, this.code});
        }

        public String toString() {
            return this.translationKey;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Type {
        KEYSYM("key.keyboard", (integer, string) -> {
            String string2 = GLFW.glfwGetKeyName(integer, -1);
            return string2 != null ? new LiteralText(string2) : new TranslatableText((String)string);
        }),
        SCANCODE("scancode", (integer, string) -> {
            String string2 = GLFW.glfwGetKeyName(-1, integer);
            return string2 != null ? new LiteralText(string2) : new TranslatableText((String)string);
        }),
        MOUSE("key.mouse", (integer, string) -> Language.getInstance().hasTranslation((String)string) ? new TranslatableText((String)string) : new TranslatableText("key.mouse", integer + 1));

        private final Int2ObjectMap<Key> map = new Int2ObjectOpenHashMap<Key>();
        private final String name;
        private final BiFunction<Integer, String, Text> textTranslator;

        private static void mapKey(Type type, String translationKey, int keyCode) {
            Key key = new Key(translationKey, type, keyCode);
            type.map.put(keyCode, key);
        }

        private Type(String name, BiFunction<Integer, String, Text> textTranslator) {
            this.name = name;
            this.textTranslator = textTranslator;
        }

        public Key createFromCode(int code2) {
            return this.map.computeIfAbsent(code2, code -> {
                int i = code;
                if (this == MOUSE) {
                    ++i;
                }
                String string = this.name + "." + i;
                return new Key(string, this, code);
            });
        }

        static {
            Type.mapKey(KEYSYM, "key.keyboard.unknown", GLFW.GLFW_KEY_UNKNOWN);
            Type.mapKey(MOUSE, "key.mouse.left", GLFW.GLFW_MOUSE_BUTTON_LEFT);
            Type.mapKey(MOUSE, "key.mouse.right", GLFW.GLFW_MOUSE_BUTTON_RIGHT);
            Type.mapKey(MOUSE, "key.mouse.middle", GLFW.GLFW_MOUSE_BUTTON_MIDDLE);
            Type.mapKey(MOUSE, "key.mouse.4", GLFW.GLFW_MOUSE_BUTTON_4);
            Type.mapKey(MOUSE, "key.mouse.5", GLFW.GLFW_MOUSE_BUTTON_5);
            Type.mapKey(MOUSE, "key.mouse.6", GLFW.GLFW_MOUSE_BUTTON_6);
            Type.mapKey(MOUSE, "key.mouse.7", GLFW.GLFW_MOUSE_BUTTON_7);
            Type.mapKey(MOUSE, "key.mouse.8", GLFW.GLFW_MOUSE_BUTTON_8);
            Type.mapKey(KEYSYM, "key.keyboard.0", GLFW.GLFW_KEY_0);
            Type.mapKey(KEYSYM, "key.keyboard.1", GLFW.GLFW_KEY_1);
            Type.mapKey(KEYSYM, "key.keyboard.2", GLFW.GLFW_KEY_2);
            Type.mapKey(KEYSYM, "key.keyboard.3", GLFW.GLFW_KEY_3);
            Type.mapKey(KEYSYM, "key.keyboard.4", GLFW.GLFW_KEY_4);
            Type.mapKey(KEYSYM, "key.keyboard.5", GLFW.GLFW_KEY_5);
            Type.mapKey(KEYSYM, "key.keyboard.6", GLFW.GLFW_KEY_6);
            Type.mapKey(KEYSYM, "key.keyboard.7", GLFW.GLFW_KEY_7);
            Type.mapKey(KEYSYM, "key.keyboard.8", GLFW.GLFW_KEY_8);
            Type.mapKey(KEYSYM, "key.keyboard.9", GLFW.GLFW_KEY_9);
            Type.mapKey(KEYSYM, "key.keyboard.a", GLFW.GLFW_KEY_A);
            Type.mapKey(KEYSYM, "key.keyboard.b", GLFW.GLFW_KEY_B);
            Type.mapKey(KEYSYM, "key.keyboard.c", GLFW.GLFW_KEY_C);
            Type.mapKey(KEYSYM, "key.keyboard.d", GLFW.GLFW_KEY_D);
            Type.mapKey(KEYSYM, "key.keyboard.e", GLFW.GLFW_KEY_E);
            Type.mapKey(KEYSYM, "key.keyboard.f", GLFW.GLFW_KEY_F);
            Type.mapKey(KEYSYM, "key.keyboard.g", GLFW.GLFW_KEY_G);
            Type.mapKey(KEYSYM, "key.keyboard.h", GLFW.GLFW_KEY_H);
            Type.mapKey(KEYSYM, "key.keyboard.i", GLFW.GLFW_KEY_I);
            Type.mapKey(KEYSYM, "key.keyboard.j", GLFW.GLFW_KEY_J);
            Type.mapKey(KEYSYM, "key.keyboard.k", GLFW.GLFW_KEY_K);
            Type.mapKey(KEYSYM, "key.keyboard.l", GLFW.GLFW_KEY_L);
            Type.mapKey(KEYSYM, "key.keyboard.m", GLFW.GLFW_KEY_M);
            Type.mapKey(KEYSYM, "key.keyboard.n", GLFW.GLFW_KEY_N);
            Type.mapKey(KEYSYM, "key.keyboard.o", GLFW.GLFW_KEY_O);
            Type.mapKey(KEYSYM, "key.keyboard.p", GLFW.GLFW_KEY_P);
            Type.mapKey(KEYSYM, "key.keyboard.q", GLFW.GLFW_KEY_Q);
            Type.mapKey(KEYSYM, "key.keyboard.r", GLFW.GLFW_KEY_R);
            Type.mapKey(KEYSYM, "key.keyboard.s", GLFW.GLFW_KEY_S);
            Type.mapKey(KEYSYM, "key.keyboard.t", GLFW.GLFW_KEY_T);
            Type.mapKey(KEYSYM, "key.keyboard.u", GLFW.GLFW_KEY_U);
            Type.mapKey(KEYSYM, "key.keyboard.v", GLFW.GLFW_KEY_V);
            Type.mapKey(KEYSYM, "key.keyboard.w", GLFW.GLFW_KEY_W);
            Type.mapKey(KEYSYM, "key.keyboard.x", GLFW.GLFW_KEY_X);
            Type.mapKey(KEYSYM, "key.keyboard.y", GLFW.GLFW_KEY_Y);
            Type.mapKey(KEYSYM, "key.keyboard.z", GLFW.GLFW_KEY_Z);
            Type.mapKey(KEYSYM, "key.keyboard.f1", GLFW.GLFW_KEY_F1);
            Type.mapKey(KEYSYM, "key.keyboard.f2", GLFW.GLFW_KEY_F2);
            Type.mapKey(KEYSYM, "key.keyboard.f3", GLFW.GLFW_KEY_F3);
            Type.mapKey(KEYSYM, "key.keyboard.f4", GLFW.GLFW_KEY_F4);
            Type.mapKey(KEYSYM, "key.keyboard.f5", GLFW.GLFW_KEY_F5);
            Type.mapKey(KEYSYM, "key.keyboard.f6", GLFW.GLFW_KEY_F6);
            Type.mapKey(KEYSYM, "key.keyboard.f7", GLFW.GLFW_KEY_F7);
            Type.mapKey(KEYSYM, "key.keyboard.f8", GLFW.GLFW_KEY_F8);
            Type.mapKey(KEYSYM, "key.keyboard.f9", GLFW.GLFW_KEY_F9);
            Type.mapKey(KEYSYM, "key.keyboard.f10", GLFW.GLFW_KEY_F10);
            Type.mapKey(KEYSYM, "key.keyboard.f11", GLFW.GLFW_KEY_F11);
            Type.mapKey(KEYSYM, "key.keyboard.f12", GLFW.GLFW_KEY_F12);
            Type.mapKey(KEYSYM, "key.keyboard.f13", GLFW.GLFW_KEY_F13);
            Type.mapKey(KEYSYM, "key.keyboard.f14", GLFW.GLFW_KEY_F14);
            Type.mapKey(KEYSYM, "key.keyboard.f15", GLFW.GLFW_KEY_F15);
            Type.mapKey(KEYSYM, "key.keyboard.f16", GLFW.GLFW_KEY_F16);
            Type.mapKey(KEYSYM, "key.keyboard.f17", GLFW.GLFW_KEY_F17);
            Type.mapKey(KEYSYM, "key.keyboard.f18", GLFW.GLFW_KEY_F18);
            Type.mapKey(KEYSYM, "key.keyboard.f19", GLFW.GLFW_KEY_F19);
            Type.mapKey(KEYSYM, "key.keyboard.f20", GLFW.GLFW_KEY_F20);
            Type.mapKey(KEYSYM, "key.keyboard.f21", GLFW.GLFW_KEY_F21);
            Type.mapKey(KEYSYM, "key.keyboard.f22", GLFW.GLFW_KEY_F22);
            Type.mapKey(KEYSYM, "key.keyboard.f23", GLFW.GLFW_KEY_F23);
            Type.mapKey(KEYSYM, "key.keyboard.f24", GLFW.GLFW_KEY_F24);
            Type.mapKey(KEYSYM, "key.keyboard.f25", GLFW.GLFW_KEY_F25);
            Type.mapKey(KEYSYM, "key.keyboard.num.lock", GLFW.GLFW_KEY_NUM_LOCK);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.0", GLFW.GLFW_KEY_KP_0);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.1", GLFW.GLFW_KEY_KP_1);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.2", GLFW.GLFW_KEY_KP_2);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.3", GLFW.GLFW_KEY_KP_3);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.4", GLFW.GLFW_KEY_KP_4);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.5", GLFW.GLFW_KEY_KP_5);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.6", GLFW.GLFW_KEY_KP_6);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.7", GLFW.GLFW_KEY_KP_7);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.8", GLFW.GLFW_KEY_KP_8);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.9", GLFW.GLFW_KEY_KP_9);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.add", GLFW.GLFW_KEY_KP_ADD);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.decimal", GLFW.GLFW_KEY_KP_DECIMAL);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.enter", GLFW.GLFW_KEY_KP_ENTER);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.equal", GLFW.GLFW_KEY_KP_EQUAL);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.multiply", GLFW.GLFW_KEY_KP_MULTIPLY);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.divide", GLFW.GLFW_KEY_KP_DIVIDE);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.subtract", GLFW.GLFW_KEY_KP_SUBTRACT);
            Type.mapKey(KEYSYM, "key.keyboard.down", GLFW.GLFW_KEY_DOWN);
            Type.mapKey(KEYSYM, "key.keyboard.left", GLFW.GLFW_KEY_LEFT);
            Type.mapKey(KEYSYM, "key.keyboard.right", GLFW.GLFW_KEY_RIGHT);
            Type.mapKey(KEYSYM, "key.keyboard.up", GLFW.GLFW_KEY_UP);
            Type.mapKey(KEYSYM, "key.keyboard.apostrophe", GLFW.GLFW_KEY_APOSTROPHE);
            Type.mapKey(KEYSYM, "key.keyboard.backslash", GLFW.GLFW_KEY_BACKSLASH);
            Type.mapKey(KEYSYM, "key.keyboard.comma", GLFW.GLFW_KEY_COMMA);
            Type.mapKey(KEYSYM, "key.keyboard.equal", GLFW.GLFW_KEY_EQUAL);
            Type.mapKey(KEYSYM, "key.keyboard.grave.accent", GLFW.GLFW_KEY_GRAVE_ACCENT);
            Type.mapKey(KEYSYM, "key.keyboard.left.bracket", GLFW.GLFW_KEY_LEFT_BRACKET);
            Type.mapKey(KEYSYM, "key.keyboard.minus", GLFW.GLFW_KEY_MINUS);
            Type.mapKey(KEYSYM, "key.keyboard.period", GLFW.GLFW_KEY_PERIOD);
            Type.mapKey(KEYSYM, "key.keyboard.right.bracket", GLFW.GLFW_KEY_RIGHT_BRACKET);
            Type.mapKey(KEYSYM, "key.keyboard.semicolon", GLFW.GLFW_KEY_SEMICOLON);
            Type.mapKey(KEYSYM, "key.keyboard.slash", GLFW.GLFW_KEY_SLASH);
            Type.mapKey(KEYSYM, "key.keyboard.space", GLFW.GLFW_KEY_SPACE);
            Type.mapKey(KEYSYM, "key.keyboard.tab", GLFW.GLFW_KEY_TAB);
            Type.mapKey(KEYSYM, "key.keyboard.left.alt", GLFW.GLFW_KEY_LEFT_ALT);
            Type.mapKey(KEYSYM, "key.keyboard.left.control", GLFW.GLFW_KEY_LEFT_CONTROL);
            Type.mapKey(KEYSYM, "key.keyboard.left.shift", GLFW.GLFW_KEY_LEFT_SHIFT);
            Type.mapKey(KEYSYM, "key.keyboard.left.win", 343);
            Type.mapKey(KEYSYM, "key.keyboard.right.alt", GLFW.GLFW_KEY_RIGHT_ALT);
            Type.mapKey(KEYSYM, "key.keyboard.right.control", GLFW.GLFW_KEY_RIGHT_CONTROL);
            Type.mapKey(KEYSYM, "key.keyboard.right.shift", GLFW.GLFW_KEY_RIGHT_SHIFT);
            Type.mapKey(KEYSYM, "key.keyboard.right.win", 347);
            Type.mapKey(KEYSYM, "key.keyboard.enter", GLFW.GLFW_KEY_ENTER);
            Type.mapKey(KEYSYM, "key.keyboard.escape", GLFW.GLFW_KEY_ESCAPE);
            Type.mapKey(KEYSYM, "key.keyboard.backspace", GLFW.GLFW_KEY_BACKSPACE);
            Type.mapKey(KEYSYM, "key.keyboard.delete", GLFW.GLFW_KEY_DELETE);
            Type.mapKey(KEYSYM, "key.keyboard.end", GLFW.GLFW_KEY_END);
            Type.mapKey(KEYSYM, "key.keyboard.home", GLFW.GLFW_KEY_HOME);
            Type.mapKey(KEYSYM, "key.keyboard.insert", GLFW.GLFW_KEY_INSERT);
            Type.mapKey(KEYSYM, "key.keyboard.page.down", GLFW.GLFW_KEY_PAGE_DOWN);
            Type.mapKey(KEYSYM, "key.keyboard.page.up", GLFW.GLFW_KEY_PAGE_UP);
            Type.mapKey(KEYSYM, "key.keyboard.caps.lock", GLFW.GLFW_KEY_CAPS_LOCK);
            Type.mapKey(KEYSYM, "key.keyboard.pause", GLFW.GLFW_KEY_PAUSE);
            Type.mapKey(KEYSYM, "key.keyboard.scroll.lock", GLFW.GLFW_KEY_SCROLL_LOCK);
            Type.mapKey(KEYSYM, "key.keyboard.menu", GLFW.GLFW_KEY_MENU);
            Type.mapKey(KEYSYM, "key.keyboard.print.screen", GLFW.GLFW_KEY_PRINT_SCREEN);
            Type.mapKey(KEYSYM, "key.keyboard.world.1", GLFW.GLFW_KEY_WORLD_1);
            Type.mapKey(KEYSYM, "key.keyboard.world.2", GLFW.GLFW_KEY_WORLD_2);
        }
    }
}

