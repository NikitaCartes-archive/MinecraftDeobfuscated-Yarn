package net.minecraft;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public abstract class class_2269 extends class_2341 {
	public static final class_2746 field_10729 = class_2741.field_12484;
	protected static final class_265 field_10721 = class_2248.method_9541(6.0, 14.0, 5.0, 10.0, 16.0, 11.0);
	protected static final class_265 field_10727 = class_2248.method_9541(5.0, 14.0, 6.0, 11.0, 16.0, 10.0);
	protected static final class_265 field_10723 = class_2248.method_9541(6.0, 0.0, 5.0, 10.0, 2.0, 11.0);
	protected static final class_265 field_10716 = class_2248.method_9541(5.0, 0.0, 6.0, 11.0, 2.0, 10.0);
	protected static final class_265 field_10728 = class_2248.method_9541(5.0, 6.0, 14.0, 11.0, 10.0, 16.0);
	protected static final class_265 field_10715 = class_2248.method_9541(5.0, 6.0, 0.0, 11.0, 10.0, 2.0);
	protected static final class_265 field_10731 = class_2248.method_9541(14.0, 6.0, 5.0, 16.0, 10.0, 11.0);
	protected static final class_265 field_10720 = class_2248.method_9541(0.0, 6.0, 5.0, 2.0, 10.0, 11.0);
	protected static final class_265 field_10717 = class_2248.method_9541(6.0, 15.0, 5.0, 10.0, 16.0, 11.0);
	protected static final class_265 field_10726 = class_2248.method_9541(5.0, 15.0, 6.0, 11.0, 16.0, 10.0);
	protected static final class_265 field_10722 = class_2248.method_9541(6.0, 0.0, 5.0, 10.0, 1.0, 11.0);
	protected static final class_265 field_10730 = class_2248.method_9541(5.0, 0.0, 6.0, 11.0, 1.0, 10.0);
	protected static final class_265 field_10719 = class_2248.method_9541(5.0, 6.0, 15.0, 11.0, 10.0, 16.0);
	protected static final class_265 field_10724 = class_2248.method_9541(5.0, 6.0, 0.0, 11.0, 10.0, 1.0);
	protected static final class_265 field_10732 = class_2248.method_9541(15.0, 6.0, 5.0, 16.0, 10.0, 11.0);
	protected static final class_265 field_10718 = class_2248.method_9541(0.0, 6.0, 5.0, 1.0, 10.0, 11.0);
	private final boolean field_10725;

	protected class_2269(boolean bl, class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11177, class_2350.field_11043)
				.method_11657(field_10729, Boolean.valueOf(false))
				.method_11657(field_11007, class_2738.field_12471)
		);
		this.field_10725 = bl;
	}

	@Override
	public int method_9563(class_1941 arg) {
		return this.field_10725 ? 30 : 20;
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		class_2350 lv = arg.method_11654(field_11177);
		boolean bl = (Boolean)arg.method_11654(field_10729);
		switch ((class_2738)arg.method_11654(field_11007)) {
			case field_12475:
				if (lv.method_10166() == class_2350.class_2351.field_11048) {
					return bl ? field_10722 : field_10723;
				}

				return bl ? field_10730 : field_10716;
			case field_12471:
				switch (lv) {
					case field_11034:
						return bl ? field_10718 : field_10720;
					case field_11039:
						return bl ? field_10732 : field_10731;
					case field_11035:
						return bl ? field_10724 : field_10715;
					case field_11043:
					default:
						return bl ? field_10719 : field_10728;
				}
			case field_12473:
			default:
				if (lv.method_10166() == class_2350.class_2351.field_11048) {
					return bl ? field_10717 : field_10721;
				} else {
					return bl ? field_10726 : field_10727;
				}
		}
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		if ((Boolean)arg.method_11654(field_10729)) {
			return true;
		} else {
			arg2.method_8652(arg3, arg.method_11657(field_10729, Boolean.valueOf(true)), 3);
			this.method_9714(arg4, arg2, arg3, true);
			this.method_9713(arg, arg2, arg3);
			arg2.method_8397().method_8676(arg3, this, this.method_9563(arg2));
			return true;
		}
	}

	protected void method_9714(@Nullable class_1657 arg, class_1936 arg2, class_2338 arg3, boolean bl) {
		arg2.method_8396(bl ? arg : null, arg3, this.method_9712(bl), class_3419.field_15245, 0.3F, bl ? 0.6F : 0.5F);
	}

	protected abstract class_3414 method_9712(boolean bl);

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (!bl && arg.method_11614() != arg4.method_11614()) {
			if ((Boolean)arg.method_11654(field_10729)) {
				this.method_9713(arg, arg2, arg3);
			}

			super.method_9536(arg, arg2, arg3, arg4, bl);
		}
	}

	@Override
	public int method_9524(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return arg.method_11654(field_10729) ? 15 : 0;
	}

	@Override
	public int method_9603(class_2680 arg, class_1922 arg2, class_2338 arg3, class_2350 arg4) {
		return arg.method_11654(field_10729) && method_10119(arg) == arg4 ? 15 : 0;
	}

	@Override
	public boolean method_9506(class_2680 arg) {
		return true;
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!arg2.field_9236 && (Boolean)arg.method_11654(field_10729)) {
			if (this.field_10725) {
				this.method_9715(arg, arg2, arg3);
			} else {
				arg2.method_8652(arg3, arg.method_11657(field_10729, Boolean.valueOf(false)), 3);
				this.method_9713(arg, arg2, arg3);
				this.method_9714(null, arg2, arg3, false);
			}
		}
	}

	@Override
	public void method_9548(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1297 arg4) {
		if (!arg2.field_9236 && this.field_10725 && !(Boolean)arg.method_11654(field_10729)) {
			this.method_9715(arg, arg2, arg3);
		}
	}

	private void method_9715(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		List<? extends class_1297> list = arg2.method_18467(class_1665.class, arg.method_17770(arg2, arg3).method_1107().method_996(arg3));
		boolean bl = !list.isEmpty();
		boolean bl2 = (Boolean)arg.method_11654(field_10729);
		if (bl != bl2) {
			arg2.method_8652(arg3, arg.method_11657(field_10729, Boolean.valueOf(bl)), 3);
			this.method_9713(arg, arg2, arg3);
			this.method_9714(null, arg2, arg3, bl);
		}

		if (bl) {
			arg2.method_8397().method_8676(new class_2338(arg3), this, this.method_9563(arg2));
		}
	}

	private void method_9713(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		arg2.method_8452(arg3, this);
		arg2.method_8452(arg3.method_10093(method_10119(arg).method_10153()), this);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11177, field_10729, field_11007);
	}
}
