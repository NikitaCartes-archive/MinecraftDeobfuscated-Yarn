package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_407 extends class_410 {
	private final String field_2372;
	private final String field_2373;
	private final String field_2371;
	private final boolean field_2370;

	public class_407(class_411 arg, String string, int i, boolean bl) {
		super(arg, class_1074.method_4662(bl ? "chat.link.confirmTrusted" : "chat.link.confirm"), string, i);
		this.field_2402 = class_1074.method_4662(bl ? "chat.link.open" : "gui.yes");
		this.field_2399 = class_1074.method_4662(bl ? "gui.cancel" : "gui.no");
		this.field_2373 = class_1074.method_4662("chat.copy");
		this.field_2372 = class_1074.method_4662("chat.link.warning");
		this.field_2370 = !bl;
		this.field_2371 = string;
	}

	@Override
	protected void method_2224() {
		super.method_2224();
		this.field_2564.clear();
		this.field_2557.clear();
		this.method_2219(new class_339(0, this.field_2561 / 2 - 50 - 105, this.field_2559 / 6 + 96, 100, 20, this.field_2402) {
			@Override
			public void method_1826(double d, double e) {
				class_407.this.field_2403.confirmResult(true, class_407.this.field_2398);
			}
		});
		this.method_2219(new class_339(2, this.field_2561 / 2 - 50, this.field_2559 / 6 + 96, 100, 20, this.field_2373) {
			@Override
			public void method_1826(double d, double e) {
				class_407.this.method_2100();
				class_407.this.field_2403.confirmResult(false, class_407.this.field_2398);
			}
		});
		this.method_2219(new class_339(1, this.field_2561 / 2 - 50 + 105, this.field_2559 / 6 + 96, 100, 20, this.field_2399) {
			@Override
			public void method_1826(double d, double e) {
				class_407.this.field_2403.confirmResult(false, class_407.this.field_2398);
			}
		});
	}

	public void method_2100() {
		this.field_2563.field_1774.method_1455(this.field_2371);
	}

	@Override
	public void method_2214(int i, int j, float f) {
		super.method_2214(i, j, f);
		if (this.field_2370) {
			this.method_1789(this.field_2554, this.field_2372, this.field_2561 / 2, 110, 16764108);
		}
	}
}
