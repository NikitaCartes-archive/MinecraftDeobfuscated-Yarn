package net.minecraft;

public class class_1627 extends class_1547 {
	public class_1627(class_1299<? extends class_1627> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	public boolean method_5979(class_1936 arg, class_3730 arg2) {
		return super.method_5979(arg, arg2) && (arg2 == class_3730.field_16469 || arg.method_8311(new class_2338(this)));
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_15041;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14805;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14771;
	}

	@Override
	class_3414 method_6998() {
		return class_3417.field_14540;
	}

	@Override
	protected class_1665 method_6996(class_1799 arg, float f) {
		class_1665 lv = super.method_6996(arg, f);
		if (lv instanceof class_1667) {
			((class_1667)lv).method_7463(new class_1293(class_1294.field_5909, 600));
		}

		return lv;
	}
}
