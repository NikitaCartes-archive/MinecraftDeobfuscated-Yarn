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
import java.util.function.BiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
        if (keyCode == -1) {
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
        UNKNOWN_KEY = Type.KEYSYM.createFromCode(-1);
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
        MOUSE("key.mouse", (integer, string) -> new TranslatableText((String)string));

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
            Type.mapKey(KEYSYM, "key.keyboard.unknown", -1);
            Type.mapKey(MOUSE, "key.mouse.left", 0);
            Type.mapKey(MOUSE, "key.mouse.right", 1);
            Type.mapKey(MOUSE, "key.mouse.middle", 2);
            Type.mapKey(MOUSE, "key.mouse.4", 3);
            Type.mapKey(MOUSE, "key.mouse.5", 4);
            Type.mapKey(MOUSE, "key.mouse.6", 5);
            Type.mapKey(MOUSE, "key.mouse.7", 6);
            Type.mapKey(MOUSE, "key.mouse.8", 7);
            Type.mapKey(KEYSYM, "key.keyboard.0", 48);
            Type.mapKey(KEYSYM, "key.keyboard.1", 49);
            Type.mapKey(KEYSYM, "key.keyboard.2", 50);
            Type.mapKey(KEYSYM, "key.keyboard.3", 51);
            Type.mapKey(KEYSYM, "key.keyboard.4", 52);
            Type.mapKey(KEYSYM, "key.keyboard.5", 53);
            Type.mapKey(KEYSYM, "key.keyboard.6", 54);
            Type.mapKey(KEYSYM, "key.keyboard.7", 55);
            Type.mapKey(KEYSYM, "key.keyboard.8", 56);
            Type.mapKey(KEYSYM, "key.keyboard.9", 57);
            Type.mapKey(KEYSYM, "key.keyboard.a", 65);
            Type.mapKey(KEYSYM, "key.keyboard.b", 66);
            Type.mapKey(KEYSYM, "key.keyboard.c", 67);
            Type.mapKey(KEYSYM, "key.keyboard.d", 68);
            Type.mapKey(KEYSYM, "key.keyboard.e", 69);
            Type.mapKey(KEYSYM, "key.keyboard.f", 70);
            Type.mapKey(KEYSYM, "key.keyboard.g", 71);
            Type.mapKey(KEYSYM, "key.keyboard.h", 72);
            Type.mapKey(KEYSYM, "key.keyboard.i", 73);
            Type.mapKey(KEYSYM, "key.keyboard.j", 74);
            Type.mapKey(KEYSYM, "key.keyboard.k", 75);
            Type.mapKey(KEYSYM, "key.keyboard.l", 76);
            Type.mapKey(KEYSYM, "key.keyboard.m", 77);
            Type.mapKey(KEYSYM, "key.keyboard.n", 78);
            Type.mapKey(KEYSYM, "key.keyboard.o", 79);
            Type.mapKey(KEYSYM, "key.keyboard.p", 80);
            Type.mapKey(KEYSYM, "key.keyboard.q", 81);
            Type.mapKey(KEYSYM, "key.keyboard.r", 82);
            Type.mapKey(KEYSYM, "key.keyboard.s", 83);
            Type.mapKey(KEYSYM, "key.keyboard.t", 84);
            Type.mapKey(KEYSYM, "key.keyboard.u", 85);
            Type.mapKey(KEYSYM, "key.keyboard.v", 86);
            Type.mapKey(KEYSYM, "key.keyboard.w", 87);
            Type.mapKey(KEYSYM, "key.keyboard.x", 88);
            Type.mapKey(KEYSYM, "key.keyboard.y", 89);
            Type.mapKey(KEYSYM, "key.keyboard.z", 90);
            Type.mapKey(KEYSYM, "key.keyboard.f1", 290);
            Type.mapKey(KEYSYM, "key.keyboard.f2", 291);
            Type.mapKey(KEYSYM, "key.keyboard.f3", 292);
            Type.mapKey(KEYSYM, "key.keyboard.f4", 293);
            Type.mapKey(KEYSYM, "key.keyboard.f5", 294);
            Type.mapKey(KEYSYM, "key.keyboard.f6", 295);
            Type.mapKey(KEYSYM, "key.keyboard.f7", 296);
            Type.mapKey(KEYSYM, "key.keyboard.f8", 297);
            Type.mapKey(KEYSYM, "key.keyboard.f9", 298);
            Type.mapKey(KEYSYM, "key.keyboard.f10", 299);
            Type.mapKey(KEYSYM, "key.keyboard.f11", 300);
            Type.mapKey(KEYSYM, "key.keyboard.f12", 301);
            Type.mapKey(KEYSYM, "key.keyboard.f13", 302);
            Type.mapKey(KEYSYM, "key.keyboard.f14", 303);
            Type.mapKey(KEYSYM, "key.keyboard.f15", 304);
            Type.mapKey(KEYSYM, "key.keyboard.f16", 305);
            Type.mapKey(KEYSYM, "key.keyboard.f17", 306);
            Type.mapKey(KEYSYM, "key.keyboard.f18", 307);
            Type.mapKey(KEYSYM, "key.keyboard.f19", 308);
            Type.mapKey(KEYSYM, "key.keyboard.f20", 309);
            Type.mapKey(KEYSYM, "key.keyboard.f21", 310);
            Type.mapKey(KEYSYM, "key.keyboard.f22", 311);
            Type.mapKey(KEYSYM, "key.keyboard.f23", 312);
            Type.mapKey(KEYSYM, "key.keyboard.f24", 313);
            Type.mapKey(KEYSYM, "key.keyboard.f25", 314);
            Type.mapKey(KEYSYM, "key.keyboard.num.lock", 282);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.0", 320);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.1", 321);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.2", 322);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.3", 323);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.4", 324);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.5", 325);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.6", 326);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.7", 327);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.8", 328);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.9", 329);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.add", 334);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.decimal", 330);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.enter", 335);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.equal", 336);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.multiply", 332);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.divide", 331);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.subtract", 333);
            Type.mapKey(KEYSYM, "key.keyboard.down", 264);
            Type.mapKey(KEYSYM, "key.keyboard.left", 263);
            Type.mapKey(KEYSYM, "key.keyboard.right", 262);
            Type.mapKey(KEYSYM, "key.keyboard.up", 265);
            Type.mapKey(KEYSYM, "key.keyboard.apostrophe", 39);
            Type.mapKey(KEYSYM, "key.keyboard.backslash", 92);
            Type.mapKey(KEYSYM, "key.keyboard.comma", 44);
            Type.mapKey(KEYSYM, "key.keyboard.equal", 61);
            Type.mapKey(KEYSYM, "key.keyboard.grave.accent", 96);
            Type.mapKey(KEYSYM, "key.keyboard.left.bracket", 91);
            Type.mapKey(KEYSYM, "key.keyboard.minus", 45);
            Type.mapKey(KEYSYM, "key.keyboard.period", 46);
            Type.mapKey(KEYSYM, "key.keyboard.right.bracket", 93);
            Type.mapKey(KEYSYM, "key.keyboard.semicolon", 59);
            Type.mapKey(KEYSYM, "key.keyboard.slash", 47);
            Type.mapKey(KEYSYM, "key.keyboard.space", 32);
            Type.mapKey(KEYSYM, "key.keyboard.tab", 258);
            Type.mapKey(KEYSYM, "key.keyboard.left.alt", 342);
            Type.mapKey(KEYSYM, "key.keyboard.left.control", 341);
            Type.mapKey(KEYSYM, "key.keyboard.left.shift", 340);
            Type.mapKey(KEYSYM, "key.keyboard.left.win", 343);
            Type.mapKey(KEYSYM, "key.keyboard.right.alt", 346);
            Type.mapKey(KEYSYM, "key.keyboard.right.control", 345);
            Type.mapKey(KEYSYM, "key.keyboard.right.shift", 344);
            Type.mapKey(KEYSYM, "key.keyboard.right.win", 347);
            Type.mapKey(KEYSYM, "key.keyboard.enter", 257);
            Type.mapKey(KEYSYM, "key.keyboard.escape", 256);
            Type.mapKey(KEYSYM, "key.keyboard.backspace", 259);
            Type.mapKey(KEYSYM, "key.keyboard.delete", 261);
            Type.mapKey(KEYSYM, "key.keyboard.end", 269);
            Type.mapKey(KEYSYM, "key.keyboard.home", 268);
            Type.mapKey(KEYSYM, "key.keyboard.insert", 260);
            Type.mapKey(KEYSYM, "key.keyboard.page.down", 267);
            Type.mapKey(KEYSYM, "key.keyboard.page.up", 266);
            Type.mapKey(KEYSYM, "key.keyboard.caps.lock", 280);
            Type.mapKey(KEYSYM, "key.keyboard.pause", 284);
            Type.mapKey(KEYSYM, "key.keyboard.scroll.lock", 281);
            Type.mapKey(KEYSYM, "key.keyboard.menu", 348);
            Type.mapKey(KEYSYM, "key.keyboard.print.screen", 283);
            Type.mapKey(KEYSYM, "key.keyboard.world.1", 161);
            Type.mapKey(KEYSYM, "key.keyboard.world.2", 162);
        }
    }
}

