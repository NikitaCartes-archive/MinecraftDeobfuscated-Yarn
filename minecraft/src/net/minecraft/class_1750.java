package net.minecraft;

import javax.annotation.Nullable;

public class class_1750 extends class_1838 {
	private final class_2338 field_7903;
	protected boolean field_7904 = true;

	public class_1750(class_1838 arg) {
		this(arg.method_8045(), arg.method_8036(), arg.method_8041(), arg.method_8037(), arg.method_8038(), arg.method_8043(), arg.method_8039(), arg.method_8040());
	}

	protected class_1750(class_1937 arg, @Nullable class_1657 arg2, class_1799 arg3, class_2338 arg4, class_2350 arg5, float f, float g, float h) {
		super(arg, arg2, arg3, arg4, arg5, f, g, h);
		this.field_7903 = this.field_8944.method_10093(this.field_8943);
		this.field_7904 = this.method_8045().method_8320(this.field_8944).method_11587(this);
	}

	private class_1750(class_1750 arg, class_2338 arg2, class_2350 arg3) {
		super(
			arg.method_8045(),
			arg.method_8036(),
			arg.method_8041(),
			arg2.method_10093(arg3.method_10153()),
			arg3,
			arg.method_8043(),
			arg.method_8039(),
			arg.method_8040()
		);
		this.field_7903 = arg2;
		this.field_7904 = false;
	}

	public static class_1750 method_16355(class_1750 arg, class_2338.class_2339 arg2, class_2350 arg3) {
		return new class_1750(arg, arg2, arg3);
	}

	@Override
	public class_2338 method_8037() {
		return this.field_7904 ? this.field_8944 : this.field_7903;
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
			int i = 0;

			while (i < lvs.length && lvs[i] != this.field_8943.method_10153()) {
				i++;
			}

			if (i > 0) {
				System.arraycopy(lvs, 0, lvs, 1, i);
				lvs[0] = this.field_8943.method_10153();
			}

			return lvs;
		}
	}
}
