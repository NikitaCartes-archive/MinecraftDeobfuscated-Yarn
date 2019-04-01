package net.minecraft;

import java.util.Random;

public abstract class class_2231 extends class_2248 {
	protected static final class_265 field_9942 = class_2248.method_9541(1.0, 0.0, 1.0, 15.0, 0.5, 15.0);
	protected static final class_265 field_9943 = class_2248.method_9541(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);
	protected static final class_238 field_9941 = new class_238(0.125, 0.0, 0.125, 0.875, 0.25, 0.875);

	protected class_2231(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return this.method_9435(arg) > 0 ? field_9942 : field_9943;
	}

	@Override
	public int method_9563(class_1941 arg) {
		return 20;
	}

	@Override
	public boolean method_9538() {
		return true;
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return arg2 == class_2350.field_11033 && !arg.method_11591(arg4, arg5)
			? class_2246.field_10124.method_9564()
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2338 lv = arg3.method_10074();
		return method_16361(arg2, lv) || method_20044(arg2, lv, class_2350.field_11036);
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!arg2.field_9236) {
			int i = this.method_9435(arg);
			if (i > 0) {
				this.method_9433(arg2, arg3, arg, i);
			}
		}
	}

	@Override
	public void method_9548(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1297 arg4) {
		if (!arg2.field_9236) {
			int i = this.method_9435(arg);
			if (i == 0) {
				this.method_9433(arg2, arg3, arg, i);
			}
		}
	}

	protected void method_9433(class_1937 arg, class_2338 arg2, class_2680 arg3, int i) {
		int j = this.method_9434(arg, arg2);
		boolean bl = i > 0;
		boolean bl2 = j > 0;
		if (i != j) {
			arg3 = this.method_9432(arg3, j);
			arg.method_8652(arg2, arg3, 2);
			this.method_9437(arg, arg2);
			arg.method_16109(arg2);
		}

		if (!bl2 && bl) {
			this.method_9438(arg, arg2);
		} else if (bl2 && !bl) {
			this.method_9436(arg, arg2);
		}

		if (bl2) {
			arg.method_8397().method_8676(new class_2338(arg2), this, this.method_9563(arg));
		}
	}

	protected abstract void method_9436(class_1936 arg, class_2338 arg2);

	protected abstract void method_9438(class_1936 arg, class_2338 arg2);

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (!bl && arg.method_11614() != arg4.method_11614()) {
			if (this.method_9435(arg) > 0) {
				this.method_9437(arg2, arg3);
			}

			super.method_9536(arg, arg2, arg3, arg4, bl);
		}
	}

	protected void method_9437(class_1937 arg, class_2338 arg2) {
		arg.method_8452(arg2, this);
		arg.method_8452(arg2.method_10074(), this);
	}

	@Override
	public int method_9524(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return this.method_9435(arg);
	}

	@Override
	public int method_9603(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return arg4 == class_2350.field_11036 ? this.method_9435(arg) : 0;
	}

	@Override
	public boolean method_9506(class_2680 arg) {
		return true;
	}

	@Override
	public class_3619 method_9527(class_2680 arg) {
		return class_3619.field_15971;
	}

	protected abstract int method_9434(class_1937 arg, class_2338 arg2);

	protected abstract int method_9435(class_2680 arg);

	protected abstract class_2680 method_9432(class_2680 arg, int i);
}
