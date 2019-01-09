package net.minecraft;

public interface class_3816 extends class_3817<class_3784> {
	class_3816 field_16973 = method_16814("single_pool_element", class_3781::new);
	class_3816 field_16974 = method_16814("list_pool_element", class_3782::new);
	class_3816 field_16971 = method_16814("feature_pool_element", class_3776::new);
	class_3816 field_16972 = method_16814("empty_pool_element", dynamic -> class_3777.field_16663);

	static class_3816 method_16814(String string, class_3816 arg) {
		return class_2378.method_10226(class_2378.field_16793, string, arg);
	}
}
