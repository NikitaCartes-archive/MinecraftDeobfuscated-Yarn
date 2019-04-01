package net.minecraft;

public class class_2356 extends class_2261 {
	protected static final class_265 field_11085 = class_2248.method_9541(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);
	private final class_1291 field_11087;
	private final int field_11086;

	public class_2356(class_1291 arg, int i, class_2248.class_2251 arg2) {
		super(arg2);
		this.field_11087 = arg;
		this.field_11086 = i * 20;
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		class_243 lv = arg.method_11599(arg2, arg3);
		return field_11085.method_1096(lv.field_1352, lv.field_1351, lv.field_1350);
	}

	@Override
	public class_2248.class_2250 method_16841() {
		return class_2248.class_2250.field_10657;
	}

	public class_1291 method_10188() {
		return this.field_11087;
	}

	public int method_10187() {
		return this.field_11086;
	}
}
