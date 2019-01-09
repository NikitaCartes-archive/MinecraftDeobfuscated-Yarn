package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_1344 extends class_1352 {
	private final class_1314 field_6419;
	private double field_6417;
	private double field_6416;
	private double field_6415;
	private final double field_6420;
	private final class_1937 field_6418;

	public class_1344(class_1314 arg, double d) {
		this.field_6419 = arg;
		this.field_6420 = d;
		this.field_6418 = arg.field_6002;
		this.method_6265(1);
	}

	@Override
	public boolean method_6264() {
		if (!this.field_6418.method_8530()) {
			return false;
		} else if (!this.field_6419.method_5809()) {
			return false;
		} else if (!this.field_6418.method_8311(new class_2338(this.field_6419.field_5987, this.field_6419.method_5829().field_1322, this.field_6419.field_6035))) {
			return false;
		} else if (!this.field_6419.method_6118(class_1304.field_6169).method_7960()) {
			return false;
		} else {
			class_243 lv = this.method_6257();
			if (lv == null) {
				return false;
			} else {
				this.field_6417 = lv.field_1352;
				this.field_6416 = lv.field_1351;
				this.field_6415 = lv.field_1350;
				return true;
			}
		}
	}

	@Override
	public boolean method_6266() {
		return !this.field_6419.method_5942().method_6357();
	}

	@Override
	public void method_6269() {
		this.field_6419.method_5942().method_6337(this.field_6417, this.field_6416, this.field_6415, this.field_6420);
	}

	@Nullable
	private class_243 method_6257() {
		Random random = this.field_6419.method_6051();
		class_2338 lv = new class_2338(this.field_6419.field_5987, this.field_6419.method_5829().field_1322, this.field_6419.field_6035);

		for (int i = 0; i < 10; i++) {
			class_2338 lv2 = lv.method_10069(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
			if (!this.field_6418.method_8311(lv2) && this.field_6419.method_6149(lv2) < 0.0F) {
				return new class_243((double)lv2.method_10263(), (double)lv2.method_10264(), (double)lv2.method_10260());
			}
		}

		return null;
	}
}
