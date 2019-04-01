package net.minecraft;

public class class_2328 extends class_2346 {
	protected static final class_265 field_10950 = class_2248.method_9541(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

	public class_2328(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_10950;
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_3965 arg6) {
		this.method_10047(arg, arg2, arg3);
		return true;
	}

	@Override
	public void method_9606(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4) {
		this.method_10047(arg, arg2, arg3);
	}

	private void method_10047(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		for (int i = 0; i < 1000; i++) {
			class_2338 lv = arg3.method_10069(
				arg2.field_9229.nextInt(16) - arg2.field_9229.nextInt(16),
				arg2.field_9229.nextInt(8) - arg2.field_9229.nextInt(8),
				arg2.field_9229.nextInt(16) - arg2.field_9229.nextInt(16)
			);
			if (arg2.method_8320(lv).method_11588()) {
				if (arg2.field_9236) {
					for (int j = 0; j < 128; j++) {
						double d = arg2.field_9229.nextDouble();
						float f = (arg2.field_9229.nextFloat() - 0.5F) * 0.2F;
						float g = (arg2.field_9229.nextFloat() - 0.5F) * 0.2F;
						float h = (arg2.field_9229.nextFloat() - 0.5F) * 0.2F;
						double e = class_3532.method_16436(d, (double)lv.method_10263(), (double)arg3.method_10263()) + (arg2.field_9229.nextDouble() - 0.5) + 0.5;
						double k = class_3532.method_16436(d, (double)lv.method_10264(), (double)arg3.method_10264()) + arg2.field_9229.nextDouble() - 0.5;
						double l = class_3532.method_16436(d, (double)lv.method_10260(), (double)arg3.method_10260()) + (arg2.field_9229.nextDouble() - 0.5) + 0.5;
						arg2.method_8406(class_2398.field_11214, e, k, l, (double)f, (double)g, (double)h);
					}
				} else {
					arg2.method_8652(lv, arg, 2);
					arg2.method_8650(arg3, false);
				}

				return;
			}
		}
	}

	@Override
	public int method_9563(class_1941 arg) {
		return 5;
	}

	@Override
	public boolean method_9516(class_2680 arg, class_1922 arg2, class_2338 arg3, class_10 arg4) {
		return false;
	}
}
