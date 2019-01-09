package net.minecraft;

public class class_1288 extends class_1291 {
	protected class_1288(boolean bl, int i) {
		super(bl, i);
	}

	@Override
	public void method_5562(class_1309 arg, class_1325 arg2, int i) {
		arg.method_6073(arg.method_6067() - (float)(4 * (i + 1)));
		super.method_5562(arg, arg2, i);
	}

	@Override
	public void method_5555(class_1309 arg, class_1325 arg2, int i) {
		arg.method_6073(arg.method_6067() + (float)(4 * (i + 1)));
		super.method_5555(arg, arg2, i);
	}
}
