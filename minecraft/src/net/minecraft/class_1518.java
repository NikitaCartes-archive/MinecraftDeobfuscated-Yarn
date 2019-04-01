package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_1518 extends class_1512 {
	private class_243 field_7046;

	public class_1518(class_1510 arg) {
		super(arg);
	}

	@Override
	public void method_6853() {
		class_243 lv = this.field_7036.method_6834(1.0F).method_1029();
		lv.method_1024((float) (-Math.PI / 4));
		double d = this.field_7036.field_7017.field_5987;
		double e = this.field_7036.field_7017.field_6010 + (double)(this.field_7036.field_7017.method_17682() / 2.0F);
		double f = this.field_7036.field_7017.field_6035;

		for (int i = 0; i < 8; i++) {
			Random random = this.field_7036.method_6051();
			double g = d + random.nextGaussian() / 2.0;
			double h = e + random.nextGaussian() / 2.0;
			double j = f + random.nextGaussian() / 2.0;
			class_243 lv2 = this.field_7036.method_18798();
			this.field_7036
				.field_6002
				.method_8406(
					class_2398.field_11216, g, h, j, -lv.field_1352 * 0.08F + lv2.field_1352, -lv.field_1351 * 0.3F + lv2.field_1351, -lv.field_1350 * 0.08F + lv2.field_1350
				);
			lv.method_1024((float) (Math.PI / 16));
		}
	}

	@Override
	public void method_6855() {
		if (this.field_7046 == null) {
			this.field_7046 = new class_243(this.field_7036.field_6002.method_8598(class_2902.class_2903.field_13203, class_3033.field_13600));
		}

		if (this.field_7046.method_1028(this.field_7036.field_5987, this.field_7036.field_6010, this.field_7036.field_6035) < 1.0) {
			this.field_7036.method_6831().method_6865(class_1527.field_7072).method_6857();
			this.field_7036.method_6831().method_6863(class_1527.field_7081);
		}
	}

	@Override
	public float method_6846() {
		return 1.5F;
	}

	@Override
	public float method_6847() {
		float f = class_3532.method_15368(class_1297.method_17996(this.field_7036.method_18798())) + 1.0F;
		float g = Math.min(f, 40.0F);
		return g / f;
	}

	@Override
	public void method_6856() {
		this.field_7046 = null;
	}

	@Nullable
	@Override
	public class_243 method_6851() {
		return this.field_7046;
	}

	@Override
	public class_1527<class_1518> method_6849() {
		return class_1527.field_7067;
	}
}
