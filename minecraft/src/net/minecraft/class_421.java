package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_421 extends class_437 {
	private final String field_2466;
	private final String field_2467;

	public class_421(String string, String string2) {
		this.field_2466 = string;
		this.field_2467 = string2;
	}

	@Override
	protected void method_2224() {
		super.method_2224();
		this.method_2219(new class_339(0, this.field_2561 / 2 - 100, 140, class_1074.method_4662("gui.cancel")) {
			@Override
			public void method_1826(double d, double e) {
				class_421.this.field_2563.method_1507(null);
			}
		});
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_1782(0, 0, this.field_2561, this.field_2559, -12574688, -11530224);
		this.method_1789(this.field_2554, this.field_2466, this.field_2561 / 2, 90, 16777215);
		this.method_1789(this.field_2554, this.field_2467, this.field_2561 / 2, 110, 16777215);
		super.method_2214(i, j, f);
	}

	@Override
	public boolean method_16890() {
		return false;
	}
}
