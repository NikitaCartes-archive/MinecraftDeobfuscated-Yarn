package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_423 extends class_408 {
	@Override
	protected void method_2224() {
		super.method_2224();
		this.method_2219(new class_339(1, this.field_2561 / 2 - 100, this.field_2559 - 40, class_1074.method_4662("multiplayer.stopSleeping")) {
			@Override
			public void method_1826(double d, double e) {
				class_423.this.method_2180();
			}
		});
	}

	@Override
	public void method_2210() {
		this.method_2180();
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		if (i == 256) {
			this.method_2180();
		} else if (i == 257 || i == 335) {
			String string = this.field_2382.method_1882().trim();
			if (!string.isEmpty()) {
				this.field_2563.field_1724.method_3142(string);
			}

			this.field_2382.method_1852("");
			this.field_2563.field_1705.method_1743().method_1820();
			return true;
		}

		return super.method_16805(i, j, k);
	}

	private void method_2180() {
		class_634 lv = this.field_2563.field_1724.field_3944;
		lv.method_2883(new class_2848(this.field_2563.field_1724, class_2848.class_2849.field_12986));
	}
}
