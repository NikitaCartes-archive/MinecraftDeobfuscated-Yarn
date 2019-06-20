package net.minecraft;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_894 extends class_927<class_1560, class_566<class_1560>> {
	private static final class_2960 field_4666 = new class_2960("textures/entity/enderman/enderman.png");
	private final Random field_4667 = new Random();

	public class_894(class_898 arg) {
		super(arg, new class_566<>(0.0F), 0.5F);
		this.method_4046(new class_985<>(this));
		this.method_4046(new class_975(this));
	}

	public void method_3911(class_1560 arg, double d, double e, double f, float g, float h) {
		class_2680 lv = arg.method_7027();
		class_566<class_1560> lv2 = this.method_4038();
		lv2.field_3371 = lv != null;
		lv2.field_3370 = arg.method_7028();
		if (arg.method_7028()) {
			double i = 0.02;
			d += this.field_4667.nextGaussian() * 0.02;
			f += this.field_4667.nextGaussian() * 0.02;
		}

		super.method_4072(arg, d, e, f, g, h);
	}

	protected class_2960 method_3912(class_1560 arg) {
		return field_4666;
	}
}
