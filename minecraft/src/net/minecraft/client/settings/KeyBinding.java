package net.minecraft.client.settings;

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
	private static final Map<String, KeyBinding> field_1657 = Maps.<String, KeyBinding>newHashMap();
	private static final Map<InputUtil.KeyCode, KeyBinding> field_1658 = Maps.<InputUtil.KeyCode, KeyBinding>newHashMap();
	private static final Set<String> field_1652 = Sets.<String>newHashSet();
	private static final Map<String, Integer> field_1656 = SystemUtil.consume(Maps.<String, Integer>newHashMap(), hashMap -> {
		hashMap.put("key.categories.movement", 1);
		hashMap.put("key.categories.gameplay", 2);
		hashMap.put("key.categories.inventory", 3);
		hashMap.put("key.categories.creative", 4);
		hashMap.put("key.categories.multiplayer", 5);
		hashMap.put("key.categories.ui", 6);
		hashMap.put("key.categories.misc", 7);
	});
	private final String field_1660;
	private final InputUtil.KeyCode defaultKeyCode;
	private final String field_1659;
	private InputUtil.KeyCode keyCode;
	private boolean field_1653;
	private int field_1661;

	public static void method_1420(InputUtil.KeyCode keyCode) {
		KeyBinding keyBinding = (KeyBinding)field_1658.get(keyCode);
		if (keyBinding != null) {
			keyBinding.field_1661++;
		}
	}

	public static void method_1416(InputUtil.KeyCode keyCode, boolean bl) {
		KeyBinding keyBinding = (KeyBinding)field_1658.get(keyCode);
		if (keyBinding != null) {
			keyBinding.field_1653 = bl;
		}
	}

	public static void method_1424() {
		for (KeyBinding keyBinding : field_1657.values()) {
			if (keyBinding.keyCode.getCategory() == InputUtil.Type.field_1668 && keyBinding.keyCode.getKeyCode() != InputUtil.field_16237.getKeyCode()) {
				keyBinding.field_1653 = InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), keyBinding.keyCode.getKeyCode());
			}
		}
	}

	public static void method_1437() {
		for (KeyBinding keyBinding : field_1657.values()) {
			keyBinding.method_1425();
		}
	}

	public static void method_1426() {
		field_1658.clear();

		for (KeyBinding keyBinding : field_1657.values()) {
			field_1658.put(keyBinding.keyCode, keyBinding);
		}
	}

	public KeyBinding(String string, int i, String string2) {
		this(string, InputUtil.Type.field_1668, i, string2);
	}

	public KeyBinding(String string, InputUtil.Type type, int i, String string2) {
		this.field_1660 = string;
		this.keyCode = type.createFromCode(i);
		this.defaultKeyCode = this.keyCode;
		this.field_1659 = string2;
		field_1657.put(string, this);
		field_1658.put(this.keyCode, this);
		field_1652.add(string2);
	}

	public boolean method_1434() {
		return this.field_1653;
	}

	public String method_1423() {
		return this.field_1659;
	}

	public boolean method_1436() {
		if (this.field_1661 == 0) {
			return false;
		} else {
			this.field_1661--;
			return true;
		}
	}

	private void method_1425() {
		this.field_1661 = 0;
		this.field_1653 = false;
	}

	public String method_1431() {
		return this.field_1660;
	}

	public InputUtil.KeyCode getDefaultKeyCode() {
		return this.defaultKeyCode;
	}

	public void setKeyCode(InputUtil.KeyCode keyCode) {
		this.keyCode = keyCode;
	}

	public int method_1430(KeyBinding keyBinding) {
		return this.field_1659.equals(keyBinding.field_1659)
			? I18n.translate(this.field_1660).compareTo(I18n.translate(keyBinding.field_1660))
			: ((Integer)field_1656.get(this.field_1659)).compareTo((Integer)field_1656.get(keyBinding.field_1659));
	}

	public static Supplier<String> method_1419(String string) {
		KeyBinding keyBinding = (KeyBinding)field_1657.get(string);
		return keyBinding == null ? () -> string : keyBinding::method_16007;
	}

	public boolean equals(KeyBinding keyBinding) {
		return this.keyCode.equals(keyBinding.keyCode);
	}

	public boolean isUnbound() {
		return this.keyCode.equals(InputUtil.field_16237);
	}

	public boolean matches(int i, int j) {
		return i == InputUtil.field_16237.getKeyCode()
			? this.keyCode.getCategory() == InputUtil.Type.field_1671 && this.keyCode.getKeyCode() == j
			: this.keyCode.getCategory() == InputUtil.Type.field_1668 && this.keyCode.getKeyCode() == i;
	}

	public boolean matches(int i) {
		return this.keyCode.getCategory() == InputUtil.Type.field_1672 && this.keyCode.getKeyCode() == i;
	}

	public String method_16007() {
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
