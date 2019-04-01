package net.minecraft;

import java.util.EnumSet;

public class class_1347 extends class_1352 {
	private final class_1308 field_6429;

	public class_1347(class_1308 arg) {
		this.field_6429 = arg;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18407));
		arg.method_5942().method_6354(true);
	}

	@Override
	public boolean method_6264() {
		return this.field_6429.method_5799() && this.field_6429.method_5861() > 0.4 || this.field_6429.method_5771();
	}

	@Override
	public void method_6268() {
		if (this.field_6429.method_6051().nextFloat() < 0.8F) {
			this.field_6429.method_5993().method_6233();
		}
	}
}
