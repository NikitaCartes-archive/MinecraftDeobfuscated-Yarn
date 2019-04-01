package net.minecraft;

import javax.annotation.Nullable;

public class class_1284 extends class_1285 {
	private final class_1297 field_5878;

	public class_1284(String string, class_1297 arg, @Nullable class_1297 arg2) {
		super(string, arg);
		this.field_5878 = arg2;
	}

	@Nullable
	@Override
	public class_1297 method_5526() {
		return this.field_5879;
	}

	@Nullable
	@Override
	public class_1297 method_5529() {
		return this.field_5878;
	}

	@Override
	public class_2561 method_5506(class_1309 arg) {
		class_2561 lv = this.field_5878 == null ? this.field_5879.method_5476() : this.field_5878.method_5476();
		class_1799 lv2 = this.field_5878 instanceof class_1309 ? ((class_1309)this.field_5878).method_6047() : class_1799.field_8037;
		String string = "death.attack." + this.field_5841;
		String string2 = string + ".item";
		return !lv2.method_7960() && lv2.method_7938()
			? new class_2588(string2, arg.method_5476(), lv, lv2.method_7954())
			: new class_2588(string, arg.method_5476(), lv);
	}
}
