package net.minecraft;

import java.util.EnumSet;

public class class_1397 extends class_1405 {
	private final class_1439 field_6629;
	private class_1309 field_6630;

	public class_1397(class_1439 arg) {
		super(arg, false, true);
		this.field_6629 = arg;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18408));
	}

	@Override
	public boolean method_6264() {
		return false;
	}

	@Override
	public void method_6269() {
		this.field_6629.method_5980(this.field_6630);
		super.method_6269();
	}
}
