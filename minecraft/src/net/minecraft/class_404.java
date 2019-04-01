package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_404 extends class_437 {
	private static final class_316[] field_2352 = new class_316[]{
		class_316.field_1923,
		class_316.field_1917,
		class_316.field_1920,
		class_316.field_1925,
		class_316.field_1921,
		class_316.field_18723,
		class_316.field_1946,
		class_316.field_1941,
		class_316.field_1940,
		class_316.field_1939,
		class_316.field_18187,
		class_316.field_18196,
		class_316.field_18194
	};
	private final class_437 field_2354;
	private final class_315 field_2356;
	private class_339 field_2355;

	public class_404(class_437 arg, class_315 arg2) {
		super(new class_2588("options.chat.title"));
		this.field_2354 = arg;
		this.field_2356 = arg2;
	}

	@Override
	protected void init() {
		int i = 0;

		for (class_316 lv : field_2352) {
			int j = this.width / 2 - 155 + i % 2 * 160;
			int k = this.height / 6 + 24 * (i >> 1);
			class_339 lv2 = this.addButton(lv.method_18520(this.minecraft.field_1690, j, k, 150));
			if (lv == class_316.field_18194) {
				this.field_2355 = lv2;
				lv2.active = class_333.field_2054.method_1791();
			}

			i++;
		}

		this.addButton(
			new class_4185(
				this.width / 2 - 100, this.height / 6 + 24 * (i + 1) / 2, 200, 20, class_1074.method_4662("gui.done"), arg -> this.minecraft.method_1507(this.field_2354)
			)
		);
	}

	@Override
	public void removed() {
		this.minecraft.field_1690.method_1640();
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 20, 16777215);
		super.render(i, j, f);
	}

	public void method_2096() {
		this.field_2355.setMessage(class_316.field_18194.method_18501(this.field_2356));
	}
}
