package net.minecraft;

import java.util.EnumSet;
import java.util.List;

public class class_1397 extends class_1405 {
	private final class_1439 field_6629;
	private class_1309 field_6630;
	private final class_4051 field_19340 = new class_4051().method_18418(64.0);

	public class_1397(class_1439 arg) {
		super(arg, false, true);
		this.field_6629 = arg;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18408));
	}

	@Override
	public boolean method_6264() {
		class_238 lv = this.field_6629.method_5829().method_1009(10.0, 8.0, 10.0);
		List<class_1309> list = this.field_6629.field_6002.method_18466(class_1646.class, this.field_19340, this.field_6629, lv);
		List<class_1657> list2 = this.field_6629.field_6002.method_18464(this.field_19340, this.field_6629, lv);

		for (class_1309 lv2 : list) {
			class_1646 lv3 = (class_1646)lv2;

			for (class_1657 lv4 : list2) {
				int i = lv3.method_20594(lv4);
				if (i <= -100) {
					this.field_6630 = lv4;
				}
			}
		}

		return this.field_6630 != null;
	}

	@Override
	public void method_6269() {
		this.field_6629.method_5980(this.field_6630);
		super.method_6269();
	}
}
