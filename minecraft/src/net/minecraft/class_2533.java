package net.minecraft;

import javax.annotation.Nullable;

public class class_2533 extends class_2383 implements class_3737 {
	public static final class_2746 field_11631 = class_2741.field_12537;
	public static final class_2754<class_2760> field_11625 = class_2741.field_12518;
	public static final class_2746 field_11629 = class_2741.field_12484;
	public static final class_2746 field_11626 = class_2741.field_12508;
	protected static final class_265 field_11627 = class_2248.method_9541(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
	protected static final class_265 field_11630 = class_2248.method_9541(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final class_265 field_11624 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
	protected static final class_265 field_11633 = class_2248.method_9541(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
	protected static final class_265 field_11632 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
	protected static final class_265 field_11628 = class_2248.method_9541(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);

	protected class_2533(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11177, class_2350.field_11043)
				.method_11657(field_11631, Boolean.valueOf(false))
				.method_11657(field_11625, class_2760.field_12617)
				.method_11657(field_11629, Boolean.valueOf(false))
				.method_11657(field_11626, Boolean.valueOf(false))
		);
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		if (!(Boolean)arg.method_11654(field_11631)) {
			return arg.method_11654(field_11625) == class_2760.field_12619 ? field_11628 : field_11632;
		} else {
			switch ((class_2350)arg.method_11654(field_11177)) {
				case field_11043:
				default:
					return field_11633;
				case field_11035:
					return field_11624;
				case field_11039:
					return field_11630;
				case field_11034:
					return field_11627;
			}
		}
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		switch (arg4) {
			case field_50:
				return (Boolean)arg.method_11654(field_11631);
			case field_48:
				return (Boolean)arg.method_11654(field_11626);
			case field_51:
				return (Boolean)arg.method_11654(field_11631);
			default:
				return false;
		}
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_2350 arg6, float f, float g, float h) {
		if (this.field_10635 == class_3614.field_15953) {
			return false;
		} else {
			arg = arg.method_11572(field_11631);
			arg2.method_8652(arg3, arg, 2);
			if ((Boolean)arg.method_11654(field_11626)) {
				arg2.method_8405().method_8676(arg3, class_3612.field_15910, class_3612.field_15910.method_15789(arg2));
			}

			this.method_10740(arg4, arg2, arg3, (Boolean)arg.method_11654(field_11631));
			return true;
		}
	}

	protected void method_10740(@Nullable class_1657 arg, class_1937 arg2, class_2338 arg3, boolean bl) {
		if (bl) {
			int i = this.field_10635 == class_3614.field_15953 ? 1037 : 1007;
			arg2.method_8444(arg, i, arg3, 0);
		} else {
			int i = this.field_10635 == class_3614.field_15953 ? 1036 : 1013;
			arg2.method_8444(arg, i, arg3, 0);
		}
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5) {
		if (!arg2.field_9236) {
			boolean bl = arg2.method_8479(arg3);
			if (bl != (Boolean)arg.method_11654(field_11629)) {
				if ((Boolean)arg.method_11654(field_11631) != bl) {
					arg = arg.method_11657(field_11631, Boolean.valueOf(bl));
					this.method_10740(null, arg2, arg3, bl);
				}

				arg2.method_8652(arg3, arg.method_11657(field_11629, Boolean.valueOf(bl)), 2);
				if ((Boolean)arg.method_11654(field_11626)) {
					arg2.method_8405().method_8676(arg3, class_3612.field_15910, class_3612.field_15910.method_15789(arg2));
				}
			}
		}
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2680 lv = this.method_9564();
		class_3610 lv2 = arg.method_8045().method_8316(arg.method_8037());
		class_2350 lv3 = arg.method_8038();
		if (!arg.method_7717() && lv3.method_10166().method_10179()) {
			lv = lv.method_11657(field_11177, lv3).method_11657(field_11625, arg.method_8039() > 0.5F ? class_2760.field_12619 : class_2760.field_12617);
		} else {
			lv = lv.method_11657(field_11177, arg.method_8042().method_10153())
				.method_11657(field_11625, lv3 == class_2350.field_11036 ? class_2760.field_12617 : class_2760.field_12619);
		}

		if (arg.method_8045().method_8479(arg.method_8037())) {
			lv = lv.method_11657(field_11631, Boolean.valueOf(true)).method_11657(field_11629, Boolean.valueOf(true));
		}

		return lv.method_11657(field_11626, Boolean.valueOf(lv2.method_15772() == class_3612.field_15910));
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11177, field_11631, field_11625, field_11629, field_11626);
	}

	@Override
	public class_3610 method_9545(class_2680 arg) {
		return arg.method_11654(field_11626) ? class_3612.field_15910.method_15729(false) : super.method_9545(arg);
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if ((Boolean)arg.method_11654(field_11626)) {
			arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
		}

		return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}
}
