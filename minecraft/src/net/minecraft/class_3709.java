package net.minecraft;

import javax.annotation.Nullable;

public class class_3709 extends class_2237 {
	public static final class_2753 field_16324 = class_2383.field_11177;
	private static final class_2754<class_3867> field_16326 = class_2741.field_17104;
	private static final class_265 field_16325 = class_2248.method_9541(0.0, 0.0, 4.0, 16.0, 16.0, 12.0);
	private static final class_265 field_16322 = class_2248.method_9541(4.0, 0.0, 0.0, 12.0, 16.0, 16.0);
	private static final class_265 field_17087 = class_2248.method_9541(5.0, 6.0, 5.0, 11.0, 13.0, 11.0);
	private static final class_265 field_17088 = class_2248.method_9541(4.0, 4.0, 4.0, 12.0, 6.0, 12.0);
	private static final class_265 field_17089 = class_259.method_1084(field_17088, field_17087);
	private static final class_265 field_17090 = class_259.method_1084(field_17089, class_2248.method_9541(7.0, 13.0, 0.0, 9.0, 15.0, 16.0));
	private static final class_265 field_16321 = class_259.method_1084(field_17089, class_2248.method_9541(0.0, 13.0, 7.0, 16.0, 15.0, 9.0));
	private static final class_265 field_17091 = class_259.method_1084(field_17089, class_2248.method_9541(0.0, 13.0, 7.0, 13.0, 15.0, 9.0));
	private static final class_265 field_17092 = class_259.method_1084(field_17089, class_2248.method_9541(3.0, 13.0, 7.0, 16.0, 15.0, 9.0));
	private static final class_265 field_16323 = class_259.method_1084(field_17089, class_2248.method_9541(7.0, 13.0, 0.0, 9.0, 15.0, 13.0));
	private static final class_265 field_17093 = class_259.method_1084(field_17089, class_2248.method_9541(7.0, 13.0, 3.0, 9.0, 15.0, 16.0));
	private static final class_265 field_17094 = class_259.method_1084(field_17089, class_2248.method_9541(7.0, 13.0, 7.0, 9.0, 16.0, 9.0));

	public class_3709(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_16324, class_2350.field_11043).method_11657(field_16326, class_3867.field_17098));
	}

	@Override
	public void method_19286(class_1937 arg, class_2680 arg2, class_3965 arg3, class_1297 arg4) {
		if (arg4 instanceof class_1665) {
			class_1297 lv = ((class_1665)arg4).method_7452();
			class_1657 lv2 = lv instanceof class_1657 ? (class_1657)lv : null;
			this.method_19285(arg, arg2, arg.method_8321(arg3.method_17777()), arg3, lv2, true);
		}
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		return this.method_19285(arg2, arg, arg2.method_8321(arg3), arg6, arg4, true);
	}

	public boolean method_19285(class_1937 arg, class_2680 arg2, @Nullable class_2586 arg3, class_3965 arg4, @Nullable class_1657 arg5, boolean bl) {
		class_2350 lv = arg4.method_17780();
		class_2338 lv2 = arg4.method_17777();
		boolean bl2 = !bl || this.method_17028(arg2, lv, arg4.method_17784().field_1351 - (double)lv2.method_10264());
		if (!arg.field_9236 && arg3 instanceof class_3721 && bl2) {
			((class_3721)arg3).method_17031(lv);
			this.method_17026(arg, lv2);
			if (arg5 != null) {
				arg5.method_7281(class_3468.field_19255);
			}

			return true;
		} else {
			return true;
		}
	}

	private boolean method_17028(class_2680 arg, class_2350 arg2, double d) {
		if (arg2.method_10166() != class_2350.class_2351.field_11052 && !(d > 0.8124F)) {
			class_2350 lv = arg.method_11654(field_16324);
			class_3867 lv2 = arg.method_11654(field_16326);
			switch (lv2) {
				case field_17098:
					return lv.method_10166() == arg2.method_10166();
				case field_17100:
				case field_17101:
					return lv.method_10166() != arg2.method_10166();
				case field_17099:
					return true;
				default:
					return false;
			}
		} else {
			return false;
		}
	}

	private void method_17026(class_1937 arg, class_2338 arg2) {
		arg.method_8396(null, arg2, class_3417.field_17265, class_3419.field_15245, 2.0F, 1.0F);
	}

	private class_265 method_16116(class_2680 arg) {
		class_2350 lv = arg.method_11654(field_16324);
		class_3867 lv2 = arg.method_11654(field_16326);
		if (lv2 == class_3867.field_17098) {
			return lv != class_2350.field_11043 && lv != class_2350.field_11035 ? field_16322 : field_16325;
		} else if (lv2 == class_3867.field_17099) {
			return field_17094;
		} else if (lv2 == class_3867.field_17101) {
			return lv != class_2350.field_11043 && lv != class_2350.field_11035 ? field_16321 : field_17090;
		} else if (lv == class_2350.field_11043) {
			return field_16323;
		} else if (lv == class_2350.field_11035) {
			return field_17093;
		} else {
			return lv == class_2350.field_11034 ? field_17092 : field_17091;
		}
	}

	@Override
	public class_265 method_9549(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return this.method_16116(arg);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return this.method_16116(arg);
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11458;
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2350 lv = arg.method_8038();
		class_2338 lv2 = arg.method_8037();
		class_1937 lv3 = arg.method_8045();
		class_2350.class_2351 lv4 = lv.method_10166();
		if (lv4 == class_2350.class_2351.field_11052) {
			class_2680 lv5 = this.method_9564()
				.method_11657(field_16326, lv == class_2350.field_11033 ? class_3867.field_17099 : class_3867.field_17098)
				.method_11657(field_16324, arg.method_8042());
			if (lv5.method_11591(arg.method_8045(), lv2)) {
				return lv5;
			}
		} else {
			boolean bl = lv4 == class_2350.class_2351.field_11048
					&& method_20045(lv3.method_8320(lv2.method_10067()), lv3, lv2.method_10067(), class_2350.field_11034)
					&& method_20045(lv3.method_8320(lv2.method_10078()), lv3, lv2.method_10078(), class_2350.field_11039)
				|| lv4 == class_2350.class_2351.field_11051
					&& method_20045(lv3.method_8320(lv2.method_10095()), lv3, lv2.method_10095(), class_2350.field_11035)
					&& method_20045(lv3.method_8320(lv2.method_10072()), lv3, lv2.method_10072(), class_2350.field_11043);
			class_2680 lv5 = this.method_9564()
				.method_11657(field_16324, lv.method_10153())
				.method_11657(field_16326, bl ? class_3867.field_17101 : class_3867.field_17100);
			if (lv5.method_11591(arg.method_8045(), arg.method_8037())) {
				return lv5;
			}

			boolean bl2 = method_20045(lv3.method_8320(lv2.method_10074()), lv3, lv2.method_10074(), class_2350.field_11036);
			lv5 = lv5.method_11657(field_16326, bl2 ? class_3867.field_17098 : class_3867.field_17099);
			if (lv5.method_11591(arg.method_8045(), arg.method_8037())) {
				return lv5;
			}
		}

		return null;
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		class_3867 lv = arg.method_11654(field_16326);
		class_2350 lv2 = method_16115(arg).method_10153();
		if (lv2 == arg2 && !arg.method_11591(arg4, arg5) && lv != class_3867.field_17101) {
			return class_2246.field_10124.method_9564();
		} else {
			if (arg2.method_10166() == ((class_2350)arg.method_11654(field_16324)).method_10166()) {
				if (lv == class_3867.field_17101 && !method_20045(arg3, arg4, arg6, arg2)) {
					return arg.method_11657(field_16326, class_3867.field_17100).method_11657(field_16324, arg2.method_10153());
				}

				if (lv == class_3867.field_17100 && lv2.method_10153() == arg2 && method_20045(arg3, arg4, arg6, arg.method_11654(field_16324))) {
					return arg.method_11657(field_16326, class_3867.field_17101);
				}
			}

			return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
		}
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		return class_2341.method_20046(arg2, arg3, method_16115(arg).method_10153());
	}

	private static class_2350 method_16115(class_2680 arg) {
		switch ((class_3867)arg.method_11654(field_16326)) {
			case field_17098:
				return class_2350.field_11036;
			case field_17099:
				return class_2350.field_11033;
			default:
				return ((class_2350)arg.method_11654(field_16324)).method_10153();
		}
	}

	@Override
	public class_3619 method_9527(class_2680 arg) {
		return class_3619.field_15971;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_16324, field_16326);
	}

	@Nullable
	@Override
	public class_2586 method_10123(class_1922 arg) {
		return new class_3721();
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
