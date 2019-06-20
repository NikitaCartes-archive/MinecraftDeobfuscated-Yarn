package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum class_385 implements class_383 {
	field_2283;

	private static final class_1011 field_2281 = class_156.method_654(new class_1011(class_1011.class_1012.field_4997, 5, 8, false), arg -> {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 5; j++) {
				boolean bl = j == 0 || j + 1 == 5 || i == 0 || i + 1 == 8;
				arg.method_4305(j, i, bl ? -1 : 0);
			}
		}

		arg.method_4302();
	});

	@Override
	public int method_2031() {
		return 5;
	}

	@Override
	public int method_2032() {
		return 8;
	}

	@Override
	public float getAdvance() {
		return 6.0F;
	}

	@Override
	public float method_2035() {
		return 1.0F;
	}

	@Override
	public void method_2030(int i, int j) {
		field_2281.method_4301(0, i, j, false);
	}

	@Override
	public boolean method_2033() {
		return true;
	}
}
