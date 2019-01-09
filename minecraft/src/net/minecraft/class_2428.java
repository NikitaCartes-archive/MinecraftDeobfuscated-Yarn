package net.minecraft;

public class class_2428 extends class_2248 {
	public static final class_2754<class_2766> field_11325 = class_2741.field_12499;
	public static final class_2746 field_11326 = class_2741.field_12484;
	public static final class_2758 field_11324 = class_2741.field_12524;

	public class_2428(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11325, class_2766.field_12648)
				.method_11657(field_11324, Integer.valueOf(0))
				.method_11657(field_11326, Boolean.valueOf(false))
		);
	}

	@Override
	public class_2680 method_9605(class_1750 arg) {
		return this.method_9564().method_11657(field_11325, class_2766.method_11887(arg.method_8045().method_8320(arg.method_8037().method_10074())));
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return arg2 == class_2350.field_11033 ? arg.method_11657(field_11325, class_2766.method_11887(arg3)) : super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public void method_9612(class_2680 arg, class_1937 arg2, class_2338 arg3, class_2248 arg4, class_2338 arg5) {
		boolean bl = arg2.method_8479(arg3);
		if (bl != (Boolean)arg.method_11654(field_11326)) {
			if (bl) {
				this.method_10367(arg2, arg3);
			}

			arg2.method_8652(arg3, arg.method_11657(field_11326, Boolean.valueOf(bl)), 3);
		}
	}

	private void method_10367(class_1937 arg, class_2338 arg2) {
		if (arg.method_8320(arg2.method_10084()).method_11588()) {
			arg.method_8427(arg2, this, 0, 0);
		}
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_2350 arg6, float f, float g, float h) {
		if (arg2.field_9236) {
			return true;
		} else {
			arg = arg.method_11572(field_11324);
			arg2.method_8652(arg3, arg, 3);
			this.method_10367(arg2, arg3);
			arg4.method_7281(class_3468.field_15393);
			return true;
		}
	}

	@Override
	public void method_9606(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4) {
		if (!arg2.field_9236) {
			this.method_10367(arg2, arg3);
			arg4.method_7281(class_3468.field_15385);
		}
	}

	@Override
	public boolean method_9592(class_2680 arg, class_1937 arg2, class_2338 arg3, int i, int j) {
		int k = (Integer)arg.method_11654(field_11324);
		float f = (float)Math.pow(2.0, (double)(k - 12) / 12.0);
		arg2.method_8396(null, arg3, ((class_2766)arg.method_11654(field_11325)).method_11886(), class_3419.field_15247, 3.0F, f);
		arg2.method_8406(
			class_2398.field_11224, (double)arg3.method_10263() + 0.5, (double)arg3.method_10264() + 1.2, (double)arg3.method_10260() + 0.5, (double)k / 24.0, 0.0, 0.0
		);
		return true;
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_11325, field_11326, field_11324);
	}
}
