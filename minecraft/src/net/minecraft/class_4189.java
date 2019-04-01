package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4189 extends class_437 {
	private static final class_316[] field_18730 = new class_316[]{
		class_316.field_18194, class_316.field_18188, class_316.field_18723, class_316.field_18724, class_316.field_1921, class_316.field_18195
	};
	private final class_437 field_18731;
	private final class_315 field_18732;
	private class_339 field_18734;

	public class_4189(class_437 arg, class_315 arg2) {
		super(new class_2588("options.accessibility.title"));
		this.field_18731 = arg;
		this.field_18732 = arg2;
	}

	@Override
	protected void init() {
		int i = 0;

		for (class_316 lv : field_18730) {
			int j = this.width / 2 - 155 + i % 2 * 160;
			int k = this.height / 6 + 24 * (i >> 1);
			class_339 lv2 = this.addButton(lv.method_18520(this.minecraft.field_1690, j, k, 150));
			if (lv == class_316.field_18194) {
				this.field_18734 = lv2;
				lv2.active = class_333.field_2054.method_1791();
			}

			i++;
		}

		this.addButton(
			new class_4185(this.width / 2 - 100, this.height / 6 + 144, 200, 20, class_1074.method_4662("gui.done"), arg -> this.minecraft.method_1507(this.field_18731))
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

	public void method_19366() {
		this.field_18734.setMessage(class_316.field_18194.method_18501(this.field_18732));
	}
}
