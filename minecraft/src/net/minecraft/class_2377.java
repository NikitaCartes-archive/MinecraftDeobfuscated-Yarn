package net.minecraft;

public class class_2377 extends class_2237 {
	public static final class_2753 field_11129 = class_2741.field_12545;
	public static final class_2746 field_11126 = class_2741.field_12515;
	private static final class_265 field_11131 = class_2248.method_9541(0.0, 10.0, 0.0, 16.0, 16.0, 16.0);
	private static final class_265 field_11127 = class_2248.method_9541(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
	private static final class_265 field_11121 = class_259.method_1084(field_11127, field_11131);
	private static final class_265 field_11132 = class_259.method_1072(field_11121, class_2615.field_12025, class_247.field_16886);
	private static final class_265 field_11120 = class_259.method_1084(field_11132, class_2248.method_9541(6.0, 0.0, 6.0, 10.0, 4.0, 10.0));
	private static final class_265 field_11134 = class_259.method_1084(field_11132, class_2248.method_9541(12.0, 4.0, 6.0, 16.0, 8.0, 10.0));
	private static final class_265 field_11124 = class_259.method_1084(field_11132, class_2248.method_9541(6.0, 4.0, 0.0, 10.0, 8.0, 4.0));
	private static final class_265 field_11122 = class_259.method_1084(field_11132, class_2248.method_9541(6.0, 4.0, 12.0, 10.0, 8.0, 16.0));
	private static final class_265 field_11130 = class_259.method_1084(field_11132, class_2248.method_9541(0.0, 4.0, 6.0, 4.0, 8.0, 10.0));
	private static final class_265 field_11125 = class_2615.field_12025;
	private static final class_265 field_11133 = class_259.method_1084(class_2615.field_12025, class_2248.method_9541(12.0, 8.0, 6.0, 16.0, 10.0, 10.0));
	private static final class_265 field_11123 = class_259.method_1084(class_2615.field_12025, class_2248.method_9541(6.0, 8.0, 0.0, 10.0, 10.0, 4.0));
	private static final class_265 field_11128 = class_259.method_1084(class_2615.field_12025, class_2248.method_9541(6.0, 8.0, 12.0, 10.0, 10.0, 16.0));
	private static final class_265 field_11135 = class_259.method_1084(class_2615.field_12025, class_2248.method_9541(0.0, 8.0, 6.0, 4.0, 10.0, 10.0));

	public class_2377(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11129, class_2350.field_11033).method_11657(field_11126, Boolean.valueOf(true)));
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		switch ((class_2350)arg.method_11654(field_11129)) {
			case field_11033:
				return field_11120;
			case field_11043:
				return field_11124;
			case field_11035:
				return field_11122;
			case field_11039:
				return field_11130;
			case field_11034:
				return field_11134;
			default:
				return field_11132;
		}
	}

	@Override
	public class_265 method_9584(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		switch ((class_2350)arg.method_11654(field_11129)) {
			case field_11033:
				return field_11125;
			case field_11043:
				return field_11123;
			case field_11035:
				return field_11128;
			case field_11039:
				return field_11135;
			case field_11034:
				return field_11133;
			default:
				return class_2615.field_12025;
		}
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2350 lv = arg.method_8038().method_10153();
		return this.method_9564()
			.method_11657(field_11129, lv.method_10166() == class_2350.class_2351.field_11052 ? class_2350.field_11033 : lv)
			.method_11657(field_11126, Boolean.valueOf(true));
	}

	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_2614();
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1309 arg4, class_1799 arg5) {
		if (arg5.method_7938()) {
			class_2586 lv = arg.method_8321(arg2);
			if (lv instanceof class_2614) {
				((class_2614)lv).method_17488(arg5.method_7964());
			}
		}
	}

	@Override
	public void method_9615(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg4.method_11614() != arg.method_11614()) {
			this.method_10217(arg2, arg3, arg);
		}
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		if (arg2.field_9236) {
			return true;
		} else {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_2614) {
				arg4.method_17355((class_2614)lv);
				arg4.method_7281(class_3468.field_15366);
			}

			return true;
		}
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5, boolean bl) {
		this.method_10217(arg2, arg3, arg);
	}

	private void method_10217(class_1937 arg, class_2338 arg2, class_2680 arg3) {
		boolean bl = !arg.method_8479(arg2);
		if (bl != (Boolean)arg3.method_11654(field_11126)) {
			arg.method_8652(arg2, arg3.method_11657(field_11126, Boolean.valueOf(bl)), 4);
		}
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg.method_11614() != arg4.method_11614()) {
			class_2586 lv = arg2.method_8321(arg3);
			if (lv instanceof class_2614) {
				class_1264.method_5451(arg2, arg3, (class_2614)lv);
				arg2.method_8455(arg3, this);
			}

			super.method_9536(arg, arg2, arg3, arg4, bl);
		}
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11458;
	}

	@Override
	public boolean method_9498(class_2680 arg) {
		return true;
	}

	@Override
	public int method_9572(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		return class_1703.method_7608(arg2.method_8321(arg3));
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9175;
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_11129, arg2.method_10503(arg.method_11654(field_11129)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_11129)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11129, field_11126);
	}

	@Override
	public void method_9548(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1297 arg4) {
		class_2586 lv = arg2.method_8321(arg3);
		if (lv instanceof class_2614) {
			((class_2614)lv).method_11236(arg4);
		}
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
