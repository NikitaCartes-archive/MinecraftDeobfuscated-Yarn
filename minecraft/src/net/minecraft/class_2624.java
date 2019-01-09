package net.minecraft;

import javax.annotation.Nullable;

public abstract class class_2624 extends class_2586 implements class_1263, class_3908 {
	private class_1273 field_12045 = class_1273.field_5817;
	private class_2561 field_17376;

	protected class_2624(class_2591<?> arg) {
		super(arg);
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		this.field_12045 = class_1273.method_5473(arg);
		if (arg.method_10573("CustomName", 8)) {
			this.field_17376 = class_2561.class_2562.method_10877(arg.method_10558("CustomName"));
		}
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		this.field_12045.method_5474(arg);
		if (this.field_17376 != null) {
			arg.method_10582("CustomName", class_2561.class_2562.method_10867(this.field_17376));
		}

		return arg;
	}

	public void method_17488(class_2561 arg) {
		this.field_17376 = arg;
	}

	@Override
	public class_2561 method_5476() {
		return this.field_17376 != null ? this.field_17376 : this.method_5477();
	}

	protected abstract class_2561 method_5477();

	public boolean method_17489(class_1657 arg) {
		return method_17487(arg, this.field_12045, this.method_5476());
	}

	public static boolean method_17487(class_1657 arg, class_1273 arg2, class_2561 arg3) {
		if (!arg.method_7325() && !arg2.method_5472(arg.method_6047())) {
			arg.method_7353(new class_2588("container.isLocked", arg3), true);
			arg.method_17356(class_3417.field_14731, class_3419.field_15245, 1.0F, 1.0F);
			return false;
		} else {
			return true;
		}
	}

	@Nullable
	@Override
	public class_1703 createMenu(int i, class_1661 arg, class_1657 arg2) {
		return this.method_17489(arg2) ? this.method_5465(i, arg) : null;
	}

	protected abstract class_1703 method_5465(int i, class_1661 arg);
}
