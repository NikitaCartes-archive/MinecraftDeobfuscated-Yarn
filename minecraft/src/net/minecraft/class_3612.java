package net.minecraft;

public class class_3612 {
	public static final class_3611 field_15906 = method_15796("empty", new class_3576());
	public static final class_3609 field_15909 = method_15796("flowing_water", new class_3621.class_3622());
	public static final class_3609 field_15910 = method_15796("water", new class_3621.class_3623());
	public static final class_3609 field_15907 = method_15796("flowing_lava", new class_3616.class_3617());
	public static final class_3609 field_15908 = method_15796("lava", new class_3616.class_3618());

	private static <T extends class_3611> T method_15796(String string, T arg) {
		return class_2378.method_10226(class_2378.field_11154, string, arg);
	}

	static {
		for (class_3611 lv : class_2378.field_11154) {
			for (class_3610 lv2 : lv.method_15783().method_11662()) {
				class_3611.field_15904.method_10205(lv2);
			}
		}
	}
}
