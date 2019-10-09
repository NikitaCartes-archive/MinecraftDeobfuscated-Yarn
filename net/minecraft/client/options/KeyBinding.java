/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.options;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.SystemUtil;

@Environment(value=EnvType.CLIENT)
public class KeyBinding
implements Comparable<KeyBinding> {
    private static final Map<String, KeyBinding> keysById = Maps.newHashMap();
    private static final Map<InputUtil.KeyCode, KeyBinding> keysByCode = Maps.newHashMap();
    private static final Set<String> keyCategories = Sets.newHashSet();
    private static final Map<String, Integer> categoryOrderMap = SystemUtil.consume(Maps.newHashMap(), hashMap -> {
        hashMap.put("key.categories.movement", 1);
        hashMap.put("key.categories.gameplay", 2);
        hashMap.put("key.categories.inventory", 3);
        hashMap.put("key.categories.creative", 4);
        hashMap.put("key.categories.multiplayer", 5);
        hashMap.put("key.categories.ui", 6);
        hashMap.put("key.categories.misc", 7);
    });
    private final String id;
    private final InputUtil.KeyCode defaultKeyCode;
    private final String category;
    private InputUtil.KeyCode keyCode;
    private boolean pressed;
    private int timesPressed;

    public static void onKeyPressed(InputUtil.KeyCode keyCode) {
        KeyBinding keyBinding = keysByCode.get(keyCode);
        if (keyBinding != null) {
            ++keyBinding.timesPressed;
        }
    }

    public static void setKeyPressed(InputUtil.KeyCode keyCode, boolean bl) {
        KeyBinding keyBinding = keysByCode.get(keyCode);
        if (keyBinding != null) {
            keyBinding.method_23481(bl);
        }
    }

    public static void updatePressedStates() {
        for (KeyBinding keyBinding : keysById.values()) {
            if (keyBinding.keyCode.getCategory() != InputUtil.Type.KEYSYM || keyBinding.keyCode.getKeyCode() == InputUtil.UNKNOWN_KEYCODE.getKeyCode()) continue;
            keyBinding.method_23481(InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), keyBinding.keyCode.getKeyCode()));
        }
    }

    public static void unpressAll() {
        for (KeyBinding keyBinding : keysById.values()) {
            keyBinding.reset();
        }
    }

    public static void updateKeysByCode() {
        keysByCode.clear();
        for (KeyBinding keyBinding : keysById.values()) {
            keysByCode.put(keyBinding.keyCode, keyBinding);
        }
    }

    public KeyBinding(String string, int i, String string2) {
        this(string, InputUtil.Type.KEYSYM, i, string2);
    }

    public KeyBinding(String string, InputUtil.Type type, int i, String string2) {
        this.id = string;
        this.defaultKeyCode = this.keyCode = type.createFromCode(i);
        this.category = string2;
        keysById.put(string, this);
        keysByCode.put(this.keyCode, this);
        keyCategories.add(string2);
    }

    public boolean isPressed() {
        return this.pressed;
    }

    public String getCategory() {
        return this.category;
    }

    public boolean wasPressed() {
        if (this.timesPressed == 0) {
            return false;
        }
        --this.timesPressed;
        return true;
    }

    private void reset() {
        this.timesPressed = 0;
        this.method_23481(false);
    }

    public String getId() {
        return this.id;
    }

    public InputUtil.KeyCode getDefaultKeyCode() {
        return this.defaultKeyCode;
    }

    public void setKeyCode(InputUtil.KeyCode keyCode) {
        this.keyCode = keyCode;
    }

    public int method_1430(KeyBinding keyBinding) {
        if (this.category.equals(keyBinding.category)) {
            return I18n.translate(this.id, new Object[0]).compareTo(I18n.translate(keyBinding.id, new Object[0]));
        }
        return categoryOrderMap.get(this.category).compareTo(categoryOrderMap.get(keyBinding.category));
    }

    public static Supplier<String> getLocalizedName(String string) {
        KeyBinding keyBinding = keysById.get(string);
        if (keyBinding == null) {
            return () -> string;
        }
        return keyBinding::getLocalizedName;
    }

    public boolean equals(KeyBinding keyBinding) {
        return this.keyCode.equals(keyBinding.keyCode);
    }

    public boolean isNotBound() {
        return this.keyCode.equals(InputUtil.UNKNOWN_KEYCODE);
    }

    public boolean matchesKey(int i, int j) {
        if (i == InputUtil.UNKNOWN_KEYCODE.getKeyCode()) {
            return this.keyCode.getCategory() == InputUtil.Type.SCANCODE && this.keyCode.getKeyCode() == j;
        }
        return this.keyCode.getCategory() == InputUtil.Type.KEYSYM && this.keyCode.getKeyCode() == i;
    }

    public boolean matchesMouse(int i) {
        return this.keyCode.getCategory() == InputUtil.Type.MOUSE && this.keyCode.getKeyCode() == i;
    }

    public String getLocalizedName() {
        String string = this.keyCode.getName();
        int i = this.keyCode.getKeyCode();
        String string2 = null;
        switch (this.keyCode.getCategory()) {
            case KEYSYM: {
                string2 = InputUtil.getKeycodeName(i);
                break;
            }
            case SCANCODE: {
                string2 = InputUtil.getScancodeName(i);
                break;
            }
            case MOUSE: {
                String string3 = I18n.translate(string, new Object[0]);
                string2 = Objects.equals(string3, string) ? I18n.translate(InputUtil.Type.MOUSE.getName(), i + 1) : string3;
            }
        }
        return string2 == null ? I18n.translate(string, new Object[0]) : string2;
    }

    public boolean isDefault() {
        return this.keyCode.equals(this.defaultKeyCode);
    }

    public String getName() {
        return this.keyCode.getName();
    }

    public void method_23481(boolean bl) {
        this.pressed = bl;
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.method_1430((KeyBinding)object);
    }
}

