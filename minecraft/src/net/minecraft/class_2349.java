package net.minecraft;

public class class_2349 extends class_2383 {
	public static final class_2746 field_11026 = class_2741.field_12537;
	public static final class_2746 field_11021 = class_2741.field_12484;
	public static final class_2746 field_11024 = class_2741.field_12491;
	protected static final class_265 field_11022 = class_2248.method_9541(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
	protected static final class_265 field_11017 = class_2248.method_9541(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
	protected static final class_265 field_11025 = class_2248.method_9541(0.0, 0.0, 6.0, 16.0, 13.0, 10.0);
	protected static final class_265 field_11016 = class_2248.method_9541(6.0, 0.0, 0.0, 10.0, 13.0, 16.0);
	protected static final class_265 field_11028 = class_2248.method_9541(0.0, 0.0, 6.0, 16.0, 24.0, 10.0);
	protected static final class_265 field_11019 = class_2248.method_9541(6.0, 0.0, 0.0, 10.0, 24.0, 16.0);
	protected static final class_265 field_11018 = class_259.method_1084(
		class_2248.method_9541(0.0, 5.0, 7.0, 2.0, 16.0, 9.0), class_2248.method_9541(14.0, 5.0, 7.0, 16.0, 16.0, 9.0)
	);
	protected static final class_265 field_11023 = class_259.method_1084(
		class_2248.method_9541(7.0, 5.0, 0.0, 9.0, 16.0, 2.0), class_2248.method_9541(7.0, 5.0, 14.0, 9.0, 16.0, 16.0)
	);
	protected static final class_265 field_11020 = class_259.method_1084(
		class_2248.method_9541(0.0, 2.0, 7.0, 2.0, 13.0, 9.0), class_2248.method_9541(14.0, 2.0, 7.0, 16.0, 13.0, 9.0)
	);
	protected static final class_265 field_11027 = class_259.method_1084(
		class_2248.method_9541(7.0, 2.0, 0.0, 9.0, 13.0, 2.0), class_2248.method_9541(7.0, 2.0, 14.0, 9.0, 13.0, 16.0)
	);

	public class_2349(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11026, Boolean.valueOf(false))
				.method_11657(field_11021, Boolean.valueOf(false))
				.method_11657(field_11024, Boolean.valueOf(false))
		);
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		if ((Boolean)arg.method_11654(field_11024)) {
			return ((class_2350)arg.method_11654(field_11177)).method_10166() == class_2350.class_2351.field_11048 ? field_11016 : field_11025;
		} else {
			return ((class_2350)arg.method_11654(field_11177)).method_10166() == class_2350.class_2351.field_11048 ? field_11017 : field_11022;
		}
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		class_2350.class_2351 lv = arg2.method_10166();
		if (((class_2350)arg.method_11654(field_11177)).method_10170().method_10166() != lv) {
			return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
		} else {
			boolean bl = this.method_10138(arg3) || this.method_10138(arg4.method_8320(arg5.method_10093(arg2.method_10153())));
			return arg.method_11657(field_11024, Boolean.valueOf(bl));
		}
	}

	@Override
	public class_265 method_9549(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		if ((Boolean)arg.method_11654(field_11026)) {
			return class_259.method_1073();
		} else {
			return ((class_2350)arg.method_11654(field_11177)).method_10166() == class_2350.class_2351.field_11051 ? field_11028 : field_11019;
		}
	}

	@Override
	public class_265 method_9571(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		if ((Boolean)arg.method_11654(field_11024)) {
			return ((class_2350)arg.method_11654(field_11177)).method_10166() == class_2350.class_2351.field_11048 ? field_11027 : field_11020;
		} else {
			return ((class_2350)arg.method_11654(field_11177)).method_10166() == class_2350.class_2351.field_11048 ? field_11023 : field_11018;
		}
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		switch (arg4) {
			case field_50:
				return (Boolean)arg.method_11654(field_11026);
			case field_48:
				return false;
			case field_51:
				return (Boolean)arg.method_11654(field_11026);
			default:
				return false;
		}
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_1937 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		boolean bl = lv.method_8479(lv2);
		class_2350 lv3 = arg.method_8042();
		class_2350.class_2351 lv4 = lv3.method_10166();
		boolean bl2 = lv4 == class_2350.class_2351.field_11051
				&& (this.method_10138(lv.method_8320(lv2.method_10067())) || this.method_10138(lv.method_8320(lv2.method_10078())))
			|| lv4 == class_2350.class_2351.field_11048
				&& (this.method_10138(lv.method_8320(lv2.method_10095())) || this.method_10138(lv.method_8320(lv2.method_10072())));
		return this.method_9564()
			.method_11657(field_11177, lv3)
			.method_11657(field_11026, Boolean.valueOf(bl))
			.method_11657(field_11021, Boolean.valueOf(bl))
			.method_11657(field_11024, Boolean.valueOf(bl2));
	}

	private boolean method_10138(class_2680 arg) {
		return arg.method_11614().method_9525(class_3481.field_15504);
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_2350 arg6, float f, float g, float h) {
		if ((Boolean)arg.method_11654(field_11026)) {
			arg = arg.method_11657(field_11026, Boolean.valueOf(false));
			arg2.method_8652(arg3, arg, 10);
		} else {
			class_2350 lv = arg4.method_5735();
			if (arg.method_11654(field_11177) == lv.method_10153()) {
				arg = arg.method_11657(field_11177, lv);
			}

			arg = arg.method_11657(field_11026, Boolean.valueOf(true));
			arg2.method_8652(arg3, arg, 10);
		}

		arg2.method_8444(arg4, arg.method_11654(field_11026) ? 1008 : 1014, arg3, 0);
		return true;
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5) {
		if (!arg2.field_9236) {
			boolean bl = arg2.method_8479(arg3);
			if ((Boolean)arg.method_11654(field_11021) != bl) {
				arg2.method_8652(arg3, arg.method_11657(field_11021, Boolean.valueOf(bl)).method_11657(field_11026, Boolean.valueOf(bl)), 2);
				if ((Boolean)arg.method_11654(field_11026) != bl) {
					arg2.method_8444(null, bl ? 1008 : 1014, arg3, 0);
				}
			}
		}
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11177, field_11026, field_11021, field_11024);
	}

	public static boolean method_16703(class_2680 arg, class_2350 arg2) {
		return ((class_2350)arg.method_11654(field_11177)).method_10166() == arg2.method_10170().method_10166();
	}
}
