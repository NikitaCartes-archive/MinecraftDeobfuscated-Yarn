package net.minecraft;

public class class_1787 extends class_1792 {
	public class_1787(class_1792.class_1793 arg) {
		super(arg);
		this.method_7863(new class_2960("cast"), (argx, arg2, arg3) -> {
			if (arg3 == null) {
				return 0.0F;
			} else {
				boolean bl = arg3.method_6047() == argx;
				boolean bl2 = arg3.method_6079() == argx;
				if (arg3.method_6047().method_7909() instanceof class_1787) {
					bl2 = false;
				}

				return (bl || bl2) && arg3 instanceof class_1657 && ((class_1657)arg3).field_7513 != null ? 1.0F : 0.0F;
			}
		});
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		if (arg2.field_7513 != null) {
			int i = arg2.field_7513.method_6957(lv);
			lv.method_7956(i, arg2);
			arg2.method_6104(arg3);
			arg.method_8465(
				null,
				arg2.field_5987,
				arg2.field_6010,
				arg2.field_6035,
				class_3417.field_15093,
				class_3419.field_15254,
				1.0F,
				0.4F / (field_8005.nextFloat() * 0.4F + 0.8F)
			);
		} else {
			arg.method_8465(
				null,
				arg2.field_5987,
				arg2.field_6010,
				arg2.field_6035,
				class_3417.field_14596,
				class_3419.field_15254,
				0.5F,
				0.4F / (field_8005.nextFloat() * 0.4F + 0.8F)
			);
			if (!arg.field_9236) {
				class_1536 lv2 = new class_1536(arg, arg2);
				int j = class_1890.method_8215(lv);
				if (j > 0) {
					lv2.method_6955(j);
				}

				int k = class_1890.method_8223(lv);
				if (k > 0) {
					lv2.method_6956(k);
				}

				arg.method_8649(lv2);
			}

			arg2.method_6104(arg3);
			arg2.method_7259(class_3468.field_15372.method_14956(this));
		}

		return new class_1271<>(class_1269.field_5812, lv);
	}

	@Override
	public int method_7837() {
		return 1;
	}
}
