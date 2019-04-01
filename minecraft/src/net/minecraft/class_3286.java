package net.minecraft;

import java.util.Map;

public class class_3286 implements class_3285 {
	private final class_3268 field_14269 = new class_3268("minecraft");

	@Override
	public <T extends class_3288> void method_14453(Map<String, T> map, class_3288.class_3290<T> arg) {
		T lv = class_3288.method_14456("vanilla", false, () -> this.field_14269, arg, class_3288.class_3289.field_14281);
		if (lv != null) {
			map.put("vanilla", lv);
		}
	}
}
