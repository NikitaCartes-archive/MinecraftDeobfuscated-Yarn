package net.minecraft;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class class_3759<T extends class_3763> extends class_1352 {
	private final T field_16597;

	public class_3759(T arg) {
		this.field_16597 = arg;
		this.method_6265(1);
	}

	@Override
	public boolean method_6264() {
		return this.field_16597.method_5968() == null && !this.field_16597.method_5782() && this.field_16597.method_16482();
	}

	@Override
	public boolean method_6266() {
		class_1415 lv = this.field_16597.field_6002.method_8557().method_6438(new class_2338(this.field_16597), 0);
		return lv == null;
	}

	@Override
	public void method_6268() {
		if (this.field_16597.method_16482()) {
			class_3765 lv = this.field_16597.method_16478();
			if (this.field_16597.method_16219()) {
				this.field_16597.method_16216(lv.method_16495());
			} else {
				class_3763 lv2 = lv.method_16496(this.field_16597.method_16486());
				if (lv2 != null && lv2.method_16220()) {
					this.field_16597.method_16216(lv2.method_16215());
				}
			}

			this.method_16465(lv);
		}

		if (!this.field_16597.method_6150() && this.field_16597.method_16220()) {
			class_243 lv3 = new class_243(this.field_16597.method_16215());
			class_243 lv4 = new class_243(this.field_16597.field_5987, this.field_16597.field_6010, this.field_16597.field_6035);
			class_243 lv5 = lv4.method_1020(lv3);
			lv3 = lv5.method_1021(0.4).method_1019(lv3);
			class_243 lv6 = lv3.method_1020(lv4).method_1029().method_1021(10.0).method_1019(lv4);
			class_2338 lv7 = new class_2338(lv6);
			lv7 = this.field_16597.field_6002.method_8598(class_2902.class_2903.field_13203, lv7);
			if (!this.field_16597.method_5942().method_6337((double)lv7.method_10263(), (double)lv7.method_10264(), (double)lv7.method_10260(), 1.0)) {
				this.method_16464();
			}
		}
	}

	private void method_16465(class_3765 arg) {
		Set<class_3763> set = Sets.<class_3763>newHashSet();
		class_1415 lv = arg.method_16511();
		class_238 lv2 = arg.method_16498();
		if (lv != null) {
			List<class_3763> list = this.field_16597
				.field_6002
				.method_8390(
					class_3763.class, lv2 == null ? this.field_16597.method_5829().method_1014(16.0) : lv2, arg2 -> !arg2.method_16482() && class_3767.method_16838(arg2, arg)
				);
			set.addAll(list);

			for (class_3763 lv3 : set) {
				arg.method_16516(arg.method_16490(), lv3, null, true);
			}
		}
	}

	private void method_16464() {
		Random random = this.field_16597.method_6051();
		class_2338 lv = this.field_16597
			.field_6002
			.method_8598(class_2902.class_2903.field_13203, new class_2338(this.field_16597).method_10069(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
		this.field_16597.method_5942().method_6337((double)lv.method_10263(), (double)lv.method_10264(), (double)lv.method_10260(), 1.0);
	}
}
