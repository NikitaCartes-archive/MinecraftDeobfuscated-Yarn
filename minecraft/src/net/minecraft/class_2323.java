package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2323 extends class_2248 {
	public static final class_2753 field_10938 = class_2383.field_11177;
	public static final class_2746 field_10945 = class_2741.field_12537;
	public static final class_2754<class_2750> field_10941 = class_2741.field_12520;
	public static final class_2746 field_10940 = class_2741.field_12484;
	public static final class_2754<class_2756> field_10946 = class_2741.field_12533;
	protected static final class_265 field_10942 = class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
	protected static final class_265 field_10939 = class_2248.method_9541(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
	protected static final class_265 field_10944 = class_2248.method_9541(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final class_265 field_10943 = class_2248.method_9541(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);

	protected class_2323(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_10938, class_2350.field_11043)
				.method_11657(field_10945, Boolean.valueOf(false))
				.method_11657(field_10941, class_2750.field_12588)
				.method_11657(field_10940, Boolean.valueOf(false))
				.method_11657(field_10946, class_2756.field_12607)
		);
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		class_2350 lv = arg.method_11654(field_10938);
		boolean bl = !(Boolean)arg.method_11654(field_10945);
		boolean bl2 = arg.method_11654(field_10941) == class_2750.field_12586;
		switch (lv) {
			case field_11034:
			default:
				return bl ? field_10943 : (bl2 ? field_10939 : field_10942);
			case field_11035:
				return bl ? field_10942 : (bl2 ? field_10943 : field_10944);
			case field_11039:
				return bl ? field_10944 : (bl2 ? field_10942 : field_10939);
			case field_11043:
				return bl ? field_10939 : (bl2 ? field_10944 : field_10943);
		}
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		class_2756 lv = arg.method_11654(field_10946);
		if (arg2.method_10166() != class_2350.class_2351.field_11052 || lv == class_2756.field_12607 != (arg2 == class_2350.field_11036)) {
			return lv == class_2756.field_12607 && arg2 == class_2350.field_11033 && !arg.method_11591(arg4, arg5)
				? class_2246.field_10124.method_9564()
				: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
		} else {
			return arg3.method_11614() == this && arg3.method_11654(field_10946) != lv
				? arg.method_11657(field_10938, arg3.method_11654(field_10938))
					.method_11657(field_10945, arg3.method_11654(field_10945))
					.method_11657(field_10941, arg3.method_11654(field_10941))
					.method_11657(field_10940, arg3.method_11654(field_10940))
				: class_2246.field_10124.method_9564();
		}
	}

	@Override
	public void method_9556(class_1937 arg, class_1657 arg2, class_2338 arg3, class_2680 arg4, @Nullable class_2586 arg5, class_1799 arg6) {
		super.method_9556(arg, arg2, arg3, class_2246.field_10124.method_9564(), arg5, arg6);
	}

	@Override
	public void method_9576(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1657 arg4) {
		class_2756 lv = arg3.method_11654(field_10946);
		class_2338 lv2 = lv == class_2756.field_12607 ? arg2.method_10084() : arg2.method_10074();
		class_2680 lv3 = arg.method_8320(lv2);
		if (lv3.method_11614() == this && lv3.method_11654(field_10946) != lv) {
			arg.method_8652(lv2, class_2246.field_10124.method_9564(), 35);
			arg.method_8444(arg4, 2001, lv2, class_2248.method_9507(lv3));
			class_1799 lv4 = arg4.method_6047();
			if (!arg.field_9236 && !arg4.method_7337()) {
				class_2248.method_9511(arg3, arg, arg2, null, arg4, lv4);
				class_2248.method_9511(lv3, arg, lv2, null, arg4, lv4);
			}
		}

		super.method_9576(arg, arg2, arg3, arg4);
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		switch (arg4) {
			case field_50:
				return (Boolean)arg.method_11654(field_10945);
			case field_48:
				return false;
			case field_51:
				return (Boolean)arg.method_11654(field_10945);
			default:
				return false;
		}
	}

	private int method_10034() {
		return this.field_10635 == class_3614.field_15953 ? 1011 : 1012;
	}

	private int method_10032() {
		return this.field_10635 == class_3614.field_15953 ? 1005 : 1006;
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2338 lv = arg.method_8037();
		if (lv.method_10264() < 255 && arg.method_8045().method_8320(lv.method_10084()).method_11587(arg)) {
			class_1937 lv2 = arg.method_8045();
			boolean bl = lv2.method_8479(lv) || lv2.method_8479(lv.method_10084());
			return this.method_9564()
				.method_11657(field_10938, arg.method_8042())
				.method_11657(field_10941, this.method_10035(arg))
				.method_11657(field_10940, Boolean.valueOf(bl))
				.method_11657(field_10945, Boolean.valueOf(bl))
				.method_11657(field_10946, class_2756.field_12607);
		} else {
			return null;
		}
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1309 arg4, class_1799 arg5) {
		arg.method_8652(arg2.method_10084(), arg3.method_11657(field_10946, class_2756.field_12609), 3);
	}

	private class_2750 method_10035(class_1750 arg) {
		class_1922 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		class_2350 lv3 = arg.method_8042();
		class_2338 lv4 = lv2.method_10084();
		class_2350 lv5 = lv3.method_10160();
		class_2338 lv6 = lv2.method_10093(lv5);
		class_2680 lv7 = lv.method_8320(lv6);
		class_2338 lv8 = lv4.method_10093(lv5);
		class_2680 lv9 = lv.method_8320(lv8);
		class_2350 lv10 = lv3.method_10170();
		class_2338 lv11 = lv2.method_10093(lv10);
		class_2680 lv12 = lv.method_8320(lv11);
		class_2338 lv13 = lv4.method_10093(lv10);
		class_2680 lv14 = lv.method_8320(lv13);
		int i = (lv7.method_11603(lv, lv6) ? -1 : 0)
			+ (lv9.method_11603(lv, lv8) ? -1 : 0)
			+ (lv12.method_11603(lv, lv11) ? 1 : 0)
			+ (lv14.method_11603(lv, lv13) ? 1 : 0);
		boolean bl = lv7.method_11614() == this && lv7.method_11654(field_10946) == class_2756.field_12607;
		boolean bl2 = lv12.method_11614() == this && lv12.method_11654(field_10946) == class_2756.field_12607;
		if ((!bl || bl2) && i <= 0) {
			if ((!bl2 || bl) && i >= 0) {
				int j = lv3.method_10148();
				int k = lv3.method_10165();
				float f = arg.method_8043();
				float g = arg.method_8040();
				return (j >= 0 || !(g < 0.5F)) && (j <= 0 || !(g > 0.5F)) && (k >= 0 || !(f > 0.5F)) && (k <= 0 || !(f < 0.5F))
					? class_2750.field_12588
					: class_2750.field_12586;
			} else {
				return class_2750.field_12588;
			}
		} else {
			return class_2750.field_12586;
		}
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_2350 arg6, float f, float g, float h) {
		if (this.field_10635 == class_3614.field_15953) {
			return false;
		} else {
			arg = arg.method_11572(field_10945);
			arg2.method_8652(arg3, arg, 10);
			arg2.method_8444(arg4, arg.method_11654(field_10945) ? this.method_10032() : this.method_10034(), arg3, 0);
			return true;
		}
	}

	public void method_10033(class_1937 arg, class_2338 arg2, boolean bl) {
		class_2680 lv = arg.method_8320(arg2);
		if (lv.method_11614() == this && (Boolean)lv.method_11654(field_10945) != bl) {
			arg.method_8652(arg2, lv.method_11657(field_10945, Boolean.valueOf(bl)), 10);
			this.method_10036(arg, arg2, bl);
		}
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5) {
		boolean bl = arg2.method_8479(arg3)
			|| arg2.method_8479(arg3.method_10093(arg.method_11654(field_10946) == class_2756.field_12607 ? class_2350.field_11036 : class_2350.field_11033));
		if (arg4 != this && bl != (Boolean)arg.method_11654(field_10940)) {
			if (bl != (Boolean)arg.method_11654(field_10945)) {
				this.method_10036(arg2, arg3, bl);
			}

			arg2.method_8652(arg3, arg.method_11657(field_10940, Boolean.valueOf(bl)).method_11657(field_10945, Boolean.valueOf(bl)), 2);
		}
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		class_2338 lv = arg3.method_10074();
		class_2680 lv2 = arg2.method_8320(lv);
		return arg.method_11654(field_10946) == class_2756.field_12607 ? lv2.method_11631(arg2, lv) : lv2.method_11614() == this;
	}

	private void method_10036(class_1937 arg, class_2338 arg2, boolean bl) {
		arg.method_8444(null, bl ? this.method_10032() : this.method_10034(), arg2, 0);
	}

	@Override
	public class_3619 method_9527(class_2680 arg) {
		return class_3619.field_15971;
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	@Override
	public class_2680 method_9598(class_2680 arg, class_2470 arg2) {
		return arg.method_11657(field_10938, arg2.method_10503(arg.method_11654(field_10938)));
	}

	@Override
	public class_2680 method_9569(class_2680 arg, class_2415 arg2) {
		return arg2 == class_2415.field_11302 ? arg : arg.method_11626(arg2.method_10345(arg.method_11654(field_10938))).method_11572(field_10941);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long method_9535(class_2680 arg, class_2338 arg2) {
		return class_3532.method_15371(
			arg2.method_10263(), arg2.method_10087(arg.method_11654(field_10946) == class_2756.field_12607 ? 0 : 1).method_10264(), arg2.method_10260()
		);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10946, field_10938, field_10945, field_10941, field_10940);
	}
}
