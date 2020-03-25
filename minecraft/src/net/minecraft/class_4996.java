package net.minecraft;

import net.minecraft.util.dynamic.DynamicDeserializer;
import net.minecraft.util.registry.Registry;

public interface class_4996 extends DynamicDeserializer<class_4995> {
	class_4996 field_23344 = method_26409("always_true", dynamic -> class_4994.field_23343);
	class_4996 field_23345 = method_26409("linear_pos", class_4993::new);
	class_4996 field_23346 = method_26409("axis_aligned_linear_pos", class_4992::new);

	static class_4996 method_26409(String string, class_4996 arg) {
		return Registry.register(Registry.POS_RULE_TEST, string, arg);
	}
}
