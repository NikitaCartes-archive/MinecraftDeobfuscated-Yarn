package net.minecraft;

import com.mojang.datafixers.Dynamic;

public class class_3527 implements class_3531 {
	private final class_2680 field_15715;
	private final class_2680 field_15717;
	private final class_2680 field_15716;

	public class_3527(class_2680 arg, class_2680 arg2, class_2680 arg3) {
		this.field_15715 = arg;
		this.field_15717 = arg2;
		this.field_15716 = arg3;
	}

	@Override
	public class_2680 method_15337() {
		return this.field_15715;
	}

	@Override
	public class_2680 method_15336() {
		return this.field_15717;
	}

	public class_2680 method_15330() {
		return this.field_15716;
	}

	public static class_3527 method_15331(Dynamic<?> dynamic) {
		class_2680 lv = (class_2680)dynamic.get("top_material").map(class_2680::method_11633).orElse(class_2246.field_10124.method_9564());
		class_2680 lv2 = (class_2680)dynamic.get("under_material").map(class_2680::method_11633).orElse(class_2246.field_10124.method_9564());
		class_2680 lv3 = (class_2680)dynamic.get("underwater_material").map(class_2680::method_11633).orElse(class_2246.field_10124.method_9564());
		return new class_3527(lv, lv2, lv3);
	}
}
