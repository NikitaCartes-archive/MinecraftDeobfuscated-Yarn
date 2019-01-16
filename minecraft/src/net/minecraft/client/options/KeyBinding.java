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

@Environment(EnvType.CLIENT)
public class KeyBinding implements Comparable<KeyBinding> {
	private static final Map<String, KeyBinding> keysById = Maps.<String, KeyBinding>newHashMap();
	private static final Map<InputUtil.KeyCode, KeyBinding> keysByCode = Maps.<InputUtil.KeyCode, KeyBinding>newHashMap();
	private static final Set<String> keyCategories = Sets.<String>newHashSet();
	private static final Map<String, Integer> categoryOrderMap = SystemUtil.consume(Maps.<String, Integer>newHashMap(), hashMap -> {
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

	public static void setKeyPressed(InputUtil.KeyCode keyCode, boolean bl) {
		KeyBinding keyBinding = (KeyBinding)keysByCode.get(keyCode);
		if (keyBinding != null) {
			keyBinding.pressed = bl;
		}
	}

	public static void updatePressedStates() {
		for (KeyBinding keyBinding : keysById.values()) {
			if (keyBinding.keyCode.getCategory() == InputUtil.Type.field_1668 && keyBinding.keyCode.getKeyCode() != InputUtil.UNKNOWN_KEYCODE.getKeyCode()) {
				keyBinding.pressed = InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), keyBinding.keyCode.getKeyCode());
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

	public KeyBinding(String string, int i, String string2) {
		this(string, InputUtil.Type.field_1668, i, string2);
	}

	public KeyBinding(String string, InputUtil.Type type, int i, String string2) {
		this.id = string;
		this.keyCode = type.createFromCode(i);
		this.defaultKeyCode = this.keyCode;
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
		} else {
			this.timesPressed--;
			return true;
		}
	}

	private void reset() {
		this.timesPressed = 0;
		this.pressed = false;
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

	public static Supplier<String> method_1419(String string) {
		KeyBinding keyBinding = (KeyBinding)keysById.get(string);
		return keyBinding == null ? () -> string : keyBinding::getLocalizedName;
	}

	public boolean equals(KeyBinding keyBinding) {
		return this.keyCode.equals(keyBinding.keyCode);
	}

	public boolean isNotBound() {
		return this.keyCode.equals(InputUtil.UNKNOWN_KEYCODE);
	}

	public boolean matchesKey(int i, int j) {
		return i == InputUtil.UNKNOWN_KEYCODE.getKeyCode()
			? this.keyCode.getCategory() == InputUtil.Type.field_1671 && this.keyCode.getKeyCode() == j
			: this.keyCode.getCategory() == InputUtil.Type.field_1668 && this.keyCode.getKeyCode() == i;
	}

	public boolean matchesMouse(int i) {
		return this.keyCode.getCategory() == InputUtil.Type.field_1672 && this.keyCode.getKeyCode() == i;
	}

	public String getLocalizedName() {
		String string = this.keyCode.getName();
		int i = this.keyCode.getKeyCode();
		String string2 = null;
		switch (this.keyCode.getCategory()) {
			case field_1668:
				string2 = InputUtil.getKeycodeName(i);
				break;
			case field_1671:
				string2 = InputUtil.getScancodeName(i);
				break;
			case field_1672:
				String string3 = I18n.translate(string);
				string2 = Objects.equals(string3, string) ? I18n.translate(InputUtil.Type.field_1672.getName(), i + 1) : string3;
		}

		return string2 == null ? I18n.translate(string) : string2;
	}

	public boolean isDefault() {
		return this.keyCode.equals(this.defaultKeyCode);
	}

	public String getName() {
		return this.keyCode.getName();
	}
}
