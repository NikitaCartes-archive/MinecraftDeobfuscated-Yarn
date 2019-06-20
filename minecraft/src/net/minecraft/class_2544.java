package net.minecraft;

public class class_2544 extends class_2310 {
	public static final class_2746 field_11717 = class_2741.field_12519;
	private final class_265[] field_11718;
	private final class_265[] field_11716;

	public class_2544(class_2248.class_2251 arg) {
		super(0.0F, 3.0F, 0.0F, 14.0F, 24.0F, arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11717, Boolean.valueOf(true))
				.method_11657(field_10905, Boolean.valueOf(false))
				.method_11657(field_10907, Boolean.valueOf(false))
				.method_11657(field_10904, Boolean.valueOf(false))
				.method_11657(field_10903, Boolean.valueOf(false))
				.method_11657(field_10900, Boolean.valueOf(false))
		);
		this.field_11718 = this.method_9984(4.0F, 3.0F, 16.0F, 0.0F, 14.0F);
		this.field_11716 = this.method_9984(4.0F, 3.0F, 24.0F, 0.0F, 24.0F);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return arg.method_11654(field_11717) ? this.field_11718[this.method_9987(arg)] : super.method_9530(arg, arg2, arg3, arg4);
	}

	@Override
	public class_265 method_9549(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return arg.method_11654(field_11717) ? this.field_11716[this.method_9987(arg)] : super.method_9549(arg, arg2, arg3, arg4);
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}

	private boolean method_16704(class_2680 arg, boolean bl, class_2350 arg2) {
		class_2248 lv = arg.method_11614();
		boolean bl2 = lv.method_9525(class_3481.field_15504) || lv instanceof class_2349 && class_2349.method_16703(arg, arg2);
		return !method_9581(lv) && bl || bl2;
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_1941 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		class_3610 lv3 = arg.method_8045().method_8316(arg.method_8037());
		class_2338 lv4 = lv2.method_10095();
		class_2338 lv5 = lv2.method_10078();
		class_2338 lv6 = lv2.method_10072();
		class_2338 lv7 = lv2.method_10067();
		class_2680 lv8 = lv.method_8320(lv4);
		class_2680 lv9 = lv.method_8320(lv5);
		class_2680 lv10 = lv.method_8320(lv6);
		class_2680 lv11 = lv.method_8320(lv7);
		boolean bl = this.method_16704(lv8, class_2248.method_20045(lv8, lv, lv4, class_2350.field_11035), class_2350.field_11035);
		boolean bl2 = this.method_16704(lv9, class_2248.method_20045(lv9, lv, lv5, class_2350.field_11039), class_2350.field_11039);
		boolean bl3 = this.method_16704(lv10, class_2248.method_20045(lv10, lv, lv6, class_2350.field_11043), class_2350.field_11043);
		boolean bl4 = this.method_16704(lv11, class_2248.method_20045(lv11, lv, lv7, class_2350.field_11034), class_2350.field_11034);
		boolean bl5 = (!bl || bl2 || !bl3 || bl4) && (bl || !bl2 || bl3 || !bl4);
		return this.method_9564()
			.method_11657(field_11717, Boolean.valueOf(bl5 || !lv.method_8623(lv2.method_10084())))
			.method_11657(field_10905, Boolean.valueOf(bl))
			.method_11657(field_10907, Boolean.valueOf(bl2))
			.method_11657(field_10904, Boolean.valueOf(bl3))
			.method_11657(field_10903, Boolean.valueOf(bl4))
			.method_11657(field_10900, Boolean.valueOf(lv3.method_15772() == class_3612.field_15910));
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if ((Boolean)arg.method_11654(field_10900)) {
			arg4.method_8405().method_8676(arg5, class_3612.field_15910, class_3612.field_15910.method_15789(arg4));
		}

		if (arg2 == class_2350.field_11033) {
			return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
		} else {
			class_2350 lv = arg2.method_10153();
			boolean bl = arg2 == class_2350.field_11043
				? this.method_16704(arg3, class_2248.method_20045(arg3, arg4, arg6, lv), lv)
				: (Boolean)arg.method_11654(field_10905);
			boolean bl2 = arg2 == class_2350.field_11034
				? this.method_16704(arg3, class_2248.method_20045(arg3, arg4, arg6, lv), lv)
				: (Boolean)arg.method_11654(field_10907);
			boolean bl3 = arg2 == class_2350.field_11035
				? this.method_16704(arg3, class_2248.method_20045(arg3, arg4, arg6, lv), lv)
				: (Boolean)arg.method_11654(field_10904);
			boolean bl4 = arg2 == class_2350.field_11039
				? this.method_16704(arg3, class_2248.method_20045(arg3, arg4, arg6, lv), lv)
				: (Boolean)arg.method_11654(field_10903);
			boolean bl5 = (!bl || bl2 || !bl3 || bl4) && (bl || !bl2 || bl3 || !bl4);
			return arg.method_11657(field_11717, Boolean.valueOf(bl5 || !arg4.method_8623(arg5.method_10084())))
				.method_11657(field_10905, Boolean.valueOf(bl))
				.method_11657(field_10907, Boolean.valueOf(bl2))
				.method_11657(field_10904, Boolean.valueOf(bl3))
				.method_11657(field_10903, Boolean.valueOf(bl4));
		}
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11717, field_10905, field_10907, field_10903, field_10904, field_10900);
	}
}
