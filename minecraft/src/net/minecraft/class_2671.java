package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2671 extends class_2318 {
	public static final class_2754<class_2764> field_12224 = class_2741.field_12492;
	public static final class_2746 field_12227 = class_2741.field_12535;
	protected static final class_265 field_12222 = class_2248.method_9541(12.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final class_265 field_12214 = class_2248.method_9541(0.0, 0.0, 0.0, 4.0, 16.0, 16.0);
	protected static final class_265 field_12228 = class_2248.method_9541(0.0, 0.0, 12.0, 16.0, 16.0, 16.0);
	protected static final class_265 field_12213 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 16.0, 4.0);
	protected static final class_265 field_12230 = class_2248.method_9541(0.0, 12.0, 0.0, 16.0, 16.0, 16.0);
	protected static final class_265 field_12220 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
	protected static final class_265 field_12215 = class_2248.method_9541(6.0, -4.0, 6.0, 10.0, 12.0, 10.0);
	protected static final class_265 field_12226 = class_2248.method_9541(6.0, 4.0, 6.0, 10.0, 20.0, 10.0);
	protected static final class_265 field_12221 = class_2248.method_9541(6.0, 6.0, -4.0, 10.0, 10.0, 12.0);
	protected static final class_265 field_12229 = class_2248.method_9541(6.0, 6.0, 4.0, 10.0, 10.0, 20.0);
	protected static final class_265 field_12218 = class_2248.method_9541(-4.0, 6.0, 6.0, 12.0, 10.0, 10.0);
	protected static final class_265 field_12223 = class_2248.method_9541(4.0, 6.0, 6.0, 20.0, 10.0, 10.0);
	protected static final class_265 field_12231 = class_2248.method_9541(6.0, 0.0, 6.0, 10.0, 12.0, 10.0);
	protected static final class_265 field_12217 = class_2248.method_9541(6.0, 4.0, 6.0, 10.0, 16.0, 10.0);
	protected static final class_265 field_12216 = class_2248.method_9541(6.0, 6.0, 0.0, 10.0, 10.0, 12.0);
	protected static final class_265 field_12225 = class_2248.method_9541(6.0, 6.0, 4.0, 10.0, 10.0, 16.0);
	protected static final class_265 field_12219 = class_2248.method_9541(0.0, 6.0, 6.0, 12.0, 10.0, 10.0);
	protected static final class_265 field_12212 = class_2248.method_9541(4.0, 6.0, 6.0, 16.0, 10.0, 10.0);

	public class_2671(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_10927, class_2350.field_11043)
				.method_11657(field_12224, class_2764.field_12637)
				.method_11657(field_12227, Boolean.valueOf(false))
		);
	}

	private class_265 method_11520(class_2680 arg) {
		switch ((class_2350)arg.method_11654(field_10927)) {
			case field_11033:
			default:
				return field_12220;
			case field_11036:
				return field_12230;
			case field_11043:
				return field_12213;
			case field_11035:
				return field_12228;
			case field_11039:
				return field_12214;
			case field_11034:
				return field_12222;
		}
	}

	@Override
	public boolean method_9526(class_2680 arg) {
		return true;
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return class_259.method_1084(this.method_11520(arg), this.method_11519(arg));
	}

	private class_265 method_11519(class_2680 arg) {
		boolean bl = (Boolean)arg.method_11654(field_12227);
		switch ((class_2350)arg.method_11654(field_10927)) {
			case field_11033:
			default:
				return bl ? field_12217 : field_12226;
			case field_11036:
				return bl ? field_12231 : field_12215;
			case field_11043:
				return bl ? field_12225 : field_12229;
			case field_11035:
				return bl ? field_12216 : field_12221;
			case field_11039:
				return bl ? field_12212 : field_12223;
			case field_11034:
				return bl ? field_12219 : field_12218;
		}
	}

	@Override
	public void method_9576(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1657 arg4) {
		if (!arg.field_9236 && arg4.field_7503.field_7477) {
			class_2338 lv = arg2.method_10093(((class_2350)arg3.method_11654(field_10927)).method_10153());
			class_2248 lv2 = arg.method_8320(lv).method_11614();
			if (lv2 == class_2246.field_10560 || lv2 == class_2246.field_10615) {
				arg.method_8650(lv, false);
			}
		}

		super.method_9576(arg, arg2, arg3, arg4);
	}

	@Override
	public void method_9536(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2680 arg4, boolean bl) {
		if (arg.method_11614() != arg4.method_11614()) {
			super.method_9536(arg, arg2, arg3, arg4, bl);
			class_2350 lv = ((class_2350)arg.method_11654(field_10927)).method_10153();
			arg3 = arg3.method_10093(lv);
			class_2680 lv2 = arg2.method_8320(arg3);
			if ((lv2.method_11614() == class_2246.field_10560 || lv2.method_11614() == class_2246.field_10615) && (Boolean)lv2.method_11654(class_2665.field_12191)) {
				method_9497(lv2, arg2, arg3);
				arg2.method_8650(arg3, false);
			}
		}
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return arg2.method_10153() == arg.method_11654(field_10927) && !arg.method_11591(arg4, arg5)
			? class_2246.field_10124.method_9564()
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2248 lv = arg2.method_8320(arg3.method_10093(((class_2350)arg.method_11654(field_10927)).method_10153())).method_11614();
		return lv == class_2246.field_10560 || lv == class_2246.field_10615 || lv == class_2246.field_10008;
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5, boolean bl) {
		if (arg.method_11591(arg2, arg3)) {
			class_2338 lv = arg3.method_10093(((class_2350)arg.method_11654(field_10927)).method_10153());
			arg2.method_8320(lv).method_11622(arg2, lv, arg4, arg5, false);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_9574(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		return new class_1799(arg3.method_11654(field_12224) == class_2764.field_12634 ? class_2246.field_10615 : class_2246.field_10560);
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_10927, arg2.method_10503(arg.method_11654(field_10927)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg.method_11626(arg2.method_10345(arg.method_11654(field_10927)));
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10927, field_12224, field_12227);
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
