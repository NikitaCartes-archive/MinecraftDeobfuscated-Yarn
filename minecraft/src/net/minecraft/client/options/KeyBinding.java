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
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class KeyBinding implements Comparable<KeyBinding> {
	private static final Map<String, KeyBinding> keysById = Maps.<String, KeyBinding>newHashMap();
	private static final Map<InputUtil.KeyCode, KeyBinding> keysByCode = Maps.<InputUtil.KeyCode, KeyBinding>newHashMap();
	private static final Set<String> keyCategories = Sets.<String>newHashSet();
	private static final Map<String, Integer> categoryOrderMap = Util.create(Maps.<String, Integer>newHashMap(), hashMap -> {
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
		KeyBinding keyBinding = (KeyBinding)keysByCode.get(keyCode);
		if (keyBinding != null) {
			keyBinding.timesPressed++;
		}
	}

	public static void setKeyPressed(InputUtil.KeyCode key, boolean pressed) {
		KeyBinding keyBinding = (KeyBinding)keysByCode.get(key);
		if (keyBinding != null) {
			keyBinding.setPressed(pressed);
		}
	}

	public static void updatePressedStates() {
		for (KeyBinding keyBinding : keysById.values()) {
			if (keyBinding.keyCode.getCategory() == InputUtil.Type.KEYSYM && keyBinding.keyCode.getKeyCode() != InputUtil.UNKNOWN_KEYCODE.getKeyCode()) {
				keyBinding.setPressed(InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), keyBinding.keyCode.getKeyCode()));
			}
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

	public KeyBinding(String id, int keyCode, String category) {
		this(id, InputUtil.Type.KEYSYM, keyCode, category);
	}

	public KeyBinding(String id, InputUtil.Type type, int code, String category) {
		this.id = id;
		this.keyCode = type.createFromCode(code);
		this.defaultKeyCode = this.keyCode;
		this.category = category;
		keysById.put(id, this);
		keysByCode.put(this.keyCode, this);
		keyCategories.add(category);
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
		} else {
			this.timesPressed--;
			return true;
		}
	}

	private void reset() {
		this.timesPressed = 0;
		this.setPressed(false);
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

	public int compareTo(KeyBinding keyBinding) {
		return this.category.equals(keyBinding.category)
			? I18n.translate(this.id).compareTo(I18n.translate(keyBinding.id))
			: ((Integer)categoryOrderMap.get(this.category)).compareTo((Integer)categoryOrderMap.get(keyBinding.category));
	}

	public static Supplier<String> getLocalizedName(String id) {
		KeyBinding keyBinding = (KeyBinding)keysById.get(id);
		return keyBinding == null ? () -> id : keyBinding::getLocalizedName;
	}

	public boolean equals(KeyBinding keyBinding) {
		return this.keyCode.equals(keyBinding.keyCode);
	}

	public boolean isNotBound() {
		return this.keyCode.equals(InputUtil.UNKNOWN_KEYCODE);
	}

	public boolean matchesKey(int keyCode, int scanCode) {
		return keyCode == InputUtil.UNKNOWN_KEYCODE.getKeyCode()
			? this.keyCode.getCategory() == InputUtil.Type.SCANCODE && this.keyCode.getKeyCode() == scanCode
			: this.keyCode.getCategory() == InputUtil.Type.KEYSYM && this.keyCode.getKeyCode() == keyCode;
	}

	public boolean matchesMouse(int code) {
		return this.keyCode.getCategory() == InputUtil.Type.MOUSE && this.keyCode.getKeyCode() == code;
	}

	public String getLocalizedName() {
		String string = this.keyCode.getName();
		int i = this.keyCode.getKeyCode();
		String string2 = null;
		switch (this.keyCode.getCategory()) {
			case KEYSYM:
				string2 = InputUtil.getKeycodeName(i);
				break;
			case SCANCODE:
				string2 = InputUtil.getScancodeName(i);
				break;
			case MOUSE:
				String string3 = I18n.translate(string);
				string2 = Objects.equals(string3, string) ? I18n.translate(InputUtil.Type.MOUSE.getName(), i + 1) : string3;
		}

		return string2 == null ? I18n.translate(string) : string2;
	}

	public boolean isDefault() {
		return this.keyCode.equals(this.defaultKeyCode);
	}

	public String getName() {
		return this.keyCode.getName();
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}
}
