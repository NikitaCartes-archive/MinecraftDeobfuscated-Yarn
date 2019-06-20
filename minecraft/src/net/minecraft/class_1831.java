package net.minecraft;

public class class_1831 extends class_1792 {
	private final class_1832 field_8921;

	public class_1831(class_1832 arg, class_1792.class_1793 arg2) {
		super(arg2.method_7898(arg.method_8025()));
		this.field_8921 = arg;
	}

	public class_1832 method_8022() {
		return this.field_8921;
	}

	@Override
	public int method_7837() {
		return this.field_8921.method_8026();
	}

	@Override
	public boolean method_7878(class_1799 arg, class_1799 arg2) {
		return this.field_8921.method_8023().method_8093(arg2) || super.method_7878(arg, arg2);
	}
}
