package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_499 extends class_503.class_504 {
	private final class_310 field_3034 = class_310.method_1551();

	@Override
	public void method_1903(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.method_1906() + j / 2 - 9 / 2;
		this.field_3034
			.field_1772
			.method_1729(
				class_1074.method_4662("lanServer.scanning"),
				(float)(this.field_3034.field_1755.field_2561 / 2 - this.field_3034.field_1772.method_1727(class_1074.method_4662("lanServer.scanning")) / 2),
				(float)m,
				16777215
			);
		String string;
		switch ((int)(class_156.method_658() / 300L % 4L)) {
			case 0:
			default:
				string = "O o o";
				break;
			case 1:
			case 3:
				string = "o O o";
				break;
			case 2:
				string = "o o O";
		}

		this.field_3034
			.field_1772
			.method_1729(string, (float)(this.field_3034.field_1755.field_2561 / 2 - this.field_3034.field_1772.method_1727(string) / 2), (float)(m + 9), 8421504);
	}
}
