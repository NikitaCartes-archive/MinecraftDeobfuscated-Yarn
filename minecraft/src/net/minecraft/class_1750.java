package net.minecraft;

import javax.annotation.Nullable;

public class class_1750 extends class_1838 {
	private final class_2338 field_7903;
	protected boolean field_7904 = true;

	public class_1750(class_1838 arg) {
		this(arg.method_8045(), arg.method_8036(), arg.method_8041(), arg.field_17543);
	}

	protected class_1750(class_1937 arg, @Nullable class_1657 arg2, class_1799 arg3, class_3965 arg4) {
		super(arg, arg2, arg3, arg4);
		this.field_7903 = arg4.method_17777().method_10093(arg4.method_17780());
		this.field_7904 = arg.method_8320(arg4.method_17777()).method_11587(this);
	}

	public static class_1750 method_16355(class_1750 arg, class_2338 arg2, class_2350 arg3) {
		return new class_1750(
			arg.method_8045(),
			arg.method_8036(),
			arg.method_8041(),
			new class_3965(
				new class_243(
					(double)arg2.method_10263() + 0.5 + (double)arg3.method_10148() * 0.5,
					(double)arg2.method_10264() + 0.5 + (double)arg3.method_10164() * 0.5,
					(double)arg2.method_10260() + 0.5 + (double)arg3.method_10165() * 0.5
				),
				arg3,
				arg2,
				false
			)
		);
	}

	@Override
	public class_2338 method_8037() {
		return this.field_7904 ? super.method_8037() : this.field_7903;
	}

	public boolean method_7716() {
		return this.field_7904 || this.method_8045().method_8320(this.method_8037()).method_11587(this);
	}

	public boolean method_7717() {
		return this.field_7904;
	}

	public class_2350 method_7715() {
		return class_2350.method_10159(this.field_8942)[0];
	}

	public class_2350[] method_7718() {
		class_2350[] lvs = class_2350.method_10159(this.field_8942);
		if (this.field_7904) {
			return lvs;
		} else {
			class_2350 lv = this.method_8038();
			int i = 0;

			while (i < lvs.length && lvs[i] != lv.method_10153()) {
				i++;
			}

			if (i > 0) {
				System.arraycopy(lvs, 0, lvs, 1, i);
				lvs[0] = lv.method_10153();
			}

			return lvs;
		}
	}
}
