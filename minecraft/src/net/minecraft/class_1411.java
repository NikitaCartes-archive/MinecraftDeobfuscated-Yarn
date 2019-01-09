package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

public class class_1411 implements class_1938 {
	private final List<class_1408> field_6688 = Lists.<class_1408>newArrayList();

	@Override
	public void method_8570(class_1922 arg, class_2338 arg2, class_2680 arg3, class_2680 arg4, int i) {
		if (this.method_6368(arg, arg2, arg3, arg4)) {
			int j = 0;

			for (int k = this.field_6688.size(); j < k; j++) {
				class_1408 lv = (class_1408)this.field_6688.get(j);
				if (lv != null && !lv.method_6343()) {
					class_11 lv2 = lv.method_6345();
					if (lv2 != null && !lv2.method_46() && lv2.method_38() != 0) {
						class_9 lv3 = lv.field_6681.method_45();
						double d = arg2.method_10261(
							((double)lv3.field_40 + lv.field_6684.field_5987) / 2.0,
							((double)lv3.field_39 + lv.field_6684.field_6010) / 2.0,
							((double)lv3.field_38 + lv.field_6684.field_6035) / 2.0
						);
						int l = (lv2.method_38() - lv2.method_39()) * (lv2.method_38() - lv2.method_39());
						if (d < (double)l) {
							lv.method_6356();
						}
					}
				}
			}
		}
	}

	protected boolean method_6368(class_1922 arg, class_2338 arg2, class_2680 arg3, class_2680 arg4) {
		class_265 lv = arg3.method_11628(arg, arg2);
		class_265 lv2 = arg4.method_11628(arg, arg2);
		return class_259.method_1074(lv, lv2, class_247.field_16892);
	}

	@Override
	public void method_8572(@Nullable class_1657 arg, class_3414 arg2, class_3419 arg3, double d, double e, double f, float g, float h) {
	}

	@Override
	public void method_8565(@Nullable class_1657 arg, class_3414 arg2, class_3419 arg3, class_1297 arg4, float f, float g) {
	}

	@Override
	public void method_8568(class_2394 arg, boolean bl, double d, double e, double f, double g, double h, double i) {
	}

	@Override
	public void method_8563(class_2394 arg, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i) {
	}

	@Override
	public void method_8561(class_1297 arg) {
		if (arg instanceof class_1308) {
			this.field_6688.add(((class_1308)arg).method_5942());
		}
	}

	@Override
	public void method_8566(class_1297 arg) {
		if (arg instanceof class_1308) {
			this.field_6688.remove(((class_1308)arg).method_5942());
		}
	}

	@Override
	public void method_8562(class_3414 arg, class_2338 arg2) {
	}

	@Override
	public void method_8564(int i, class_2338 arg, int j) {
	}

	@Override
	public void method_8567(class_1657 arg, int i, class_2338 arg2, int j) {
	}

	@Override
	public void method_8569(int i, class_2338 arg, int j) {
	}
}
