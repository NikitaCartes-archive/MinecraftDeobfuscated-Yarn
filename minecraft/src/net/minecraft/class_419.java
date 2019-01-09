package net.minecraft;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_419 extends class_437 {
	private final String field_2455;
	private final class_2561 field_2457;
	private List<String> field_2458;
	private final class_437 field_2456;
	private int field_2454;

	public class_419(class_437 arg, String string, class_2561 arg2) {
		this.field_2456 = arg;
		this.field_2455 = class_1074.method_4662(string);
		this.field_2457 = arg2;
	}

	@Override
	public boolean method_16890() {
		return false;
	}

	@Override
	protected void method_2224() {
		this.field_2458 = this.field_2554.method_1728(this.field_2457.method_10863(), this.field_2561 - 50);
		this.field_2454 = this.field_2458.size() * 9;
		this.method_2219(
			new class_339(
				0, this.field_2561 / 2 - 100, Math.min(this.field_2559 / 2 + this.field_2454 / 2 + 9, this.field_2559 - 30), class_1074.method_4662("gui.toMenu")
			) {
				@Override
				public void method_1826(double d, double e) {
					class_419.this.field_2563.method_1507(class_419.this.field_2456);
				}
			}
		);
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.method_1789(this.field_2554, this.field_2455, this.field_2561 / 2, this.field_2559 / 2 - this.field_2454 / 2 - 9 * 2, 11184810);
		int k = this.field_2559 / 2 - this.field_2454 / 2;
		if (this.field_2458 != null) {
			for (String string : this.field_2458) {
				this.method_1789(this.field_2554, string, this.field_2561 / 2, k, 16777215);
				k += 9;
			}
		}

		super.method_2214(i, j, f);
	}
}
