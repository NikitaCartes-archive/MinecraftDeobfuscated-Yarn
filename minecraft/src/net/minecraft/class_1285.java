package net.minecraft;

import javax.annotation.Nullable;

public class class_1285 extends class_1282 {
	@Nullable
	protected final class_1297 field_5879;
	private boolean field_5880;
	private boolean field_23643;

	public class_1285(String string, @Nullable class_1297 arg) {
		super(string);
		this.field_5879 = arg;
	}

	public class_1285 method_5550() {
		this.field_5880 = true;
		return this;
	}

	public class_1285 method_26729(boolean bl) {
		this.field_23643 = bl;
		return this;
	}

	public boolean method_5549() {
		return this.field_5880;
	}

	public boolean method_26730() {
		return this.field_23643;
	}

	@Nullable
	@Override
	public class_1297 method_5529() {
		return this.field_5879;
	}

	@Override
	public class_2561 method_5506(class_1309 arg) {
		class_1799 lv = this.field_5879 instanceof class_1309 ? ((class_1309)this.field_5879).method_6047() : class_1799.field_8037;
		String string = "death.attack." + this.field_5841;
		return !lv.method_7960() && lv.method_7938()
			? new class_2588(string + ".item", arg.method_5476(), this.field_5879.method_5476(), lv.method_7954())
			: new class_2588(string, arg.method_5476(), this.field_5879.method_5476());
	}

	@Override
	public boolean method_5514() {
		return this.field_5879 != null && this.field_5879 instanceof class_1309 && !(this.field_5879 instanceof class_1657);
	}

	@Nullable
	@Override
	public class_243 method_5510() {
		return new class_243(this.field_5879.field_5987, this.field_5879.field_6010, this.field_5879.field_6035);
	}
}
