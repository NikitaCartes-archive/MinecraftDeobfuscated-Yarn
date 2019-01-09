package net.minecraft;

public class class_1817 extends class_1796 {
	private final class_3222 field_8910;

	public class_1817(class_3222 arg) {
		this.field_8910 = arg;
	}

	@Override
	protected void method_7902(class_1792 arg, int i) {
		super.method_7902(arg, i);
		this.field_8910.field_13987.method_14364(new class_2656(arg, i));
	}

	@Override
	protected void method_7901(class_1792 arg) {
		super.method_7901(arg);
		this.field_8910.field_13987.method_14364(new class_2656(arg, 0));
	}
}
