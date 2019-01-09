package net.minecraft;

public abstract class class_1588 extends class_1314 implements class_1569 {
	protected class_1588(class_1299<?> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6194 = 5;
	}

	@Override
	public class_3419 method_5634() {
		return class_3419.field_15251;
	}

	@Override
	public void method_6007() {
		this.method_6119();
		this.method_16827();
		super.method_6007();
	}

	protected void method_16827() {
		float f = this.method_5718();
		if (f > 0.5F) {
			this.field_6278 += 2;
		}
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (!this.field_6002.field_9236 && this.field_6002.method_8407() == class_1267.field_5801) {
			this.method_5650();
		}
	}

	@Override
	protected class_3414 method_5737() {
		return class_3417.field_14630;
	}

	@Override
	protected class_3414 method_5625() {
		return class_3417.field_14836;
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		return this.method_5679(arg) ? false : super.method_5643(arg, f);
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14994;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14899;
	}

	@Override
	protected class_3414 method_6041(int i) {
		return i > 4 ? class_3417.field_15157 : class_3417.field_14754;
	}

	@Override
	public float method_6144(class_2338 arg, class_1941 arg2) {
		return 0.5F - arg2.method_8610(arg);
	}

	protected boolean method_7075() {
		class_2338 lv = new class_2338(this.field_5987, this.method_5829().field_1322, this.field_6035);
		if (this.field_6002.method_8314(class_1944.field_9284, lv) > this.field_5974.nextInt(32)) {
			return false;
		} else {
			int i = this.field_6002.method_8546() ? this.field_6002.method_8603(lv, 10) : this.field_6002.method_8602(lv);
			return i <= this.field_5974.nextInt(8);
		}
	}

	@Override
	public boolean method_5979(class_1936 arg, class_3730 arg2) {
		return arg.method_8407() != class_1267.field_5801 && this.method_7075() && super.method_5979(arg, arg2);
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_6127().method_6208(class_1612.field_7363);
	}

	@Override
	protected boolean method_6054() {
		return true;
	}

	public boolean method_7076(class_1657 arg) {
		return true;
	}
}
