package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_417 extends class_437 {
	private static final class_2960 field_2447 = new class_2960("textures/gui/demo_background.png");

	@Override
	protected void method_2224() {
		int i = -16;
		this.method_2219(new class_339(1, this.field_2561 / 2 - 116, this.field_2559 / 2 + 62 + -16, 114, 20, class_1074.method_4662("demo.help.buy")) {
			@Override
			public void method_1826(double d, double e) {
				this.field_2078 = false;
				class_156.method_668().method_670("http://www.minecraft.net/store?source=demo");
			}
		});
		this.method_2219(new class_339(2, this.field_2561 / 2 + 2, this.field_2559 / 2 + 62 + -16, 114, 20, class_1074.method_4662("demo.help.later")) {
			@Override
			public void method_1826(double d, double e) {
				class_417.this.field_2563.method_1507(null);
				class_417.this.field_2563.field_1729.method_1612();
			}
		});
	}

	@Override
	public void method_2240() {
		super.method_2240();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_2563.method_1531().method_4618(field_2447);
		int i = (this.field_2561 - 248) / 2;
		int j = (this.field_2559 - 166) / 2;
		this.method_1788(i, j, 0, 0, 248, 166);
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		int k = (this.field_2561 - 248) / 2 + 10;
		int l = (this.field_2559 - 166) / 2 + 8;
		this.field_2554.method_1729(class_1074.method_4662("demo.help.title"), (float)k, (float)l, 2039583);
		l += 12;
		class_315 lv = this.field_2563.field_1690;
		this.field_2554
			.method_1729(
				class_1074.method_4662(
					"demo.help.movementShort", lv.field_1894.method_16007(), lv.field_1913.method_16007(), lv.field_1881.method_16007(), lv.field_1849.method_16007()
				),
				(float)k,
				(float)l,
				5197647
			);
		this.field_2554.method_1729(class_1074.method_4662("demo.help.movementMouse"), (float)k, (float)(l + 12), 5197647);
		this.field_2554.method_1729(class_1074.method_4662("demo.help.jump", lv.field_1903.method_16007()), (float)k, (float)(l + 24), 5197647);
		this.field_2554.method_1729(class_1074.method_4662("demo.help.inventory", lv.field_1822.method_16007()), (float)k, (float)(l + 36), 5197647);
		this.field_2554.method_1712(class_1074.method_4662("demo.help.fullWrapped"), k, l + 68, 218, 2039583);
		super.method_2214(i, j, f);
	}
}
