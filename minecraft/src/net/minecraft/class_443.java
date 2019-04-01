package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_443 extends class_437 {
	private final class_437 field_2617;
	private final class_315 field_2618;

	public class_443(class_437 arg, class_315 arg2) {
		super(new class_2588("options.sounds.title"));
		this.field_2617 = arg;
		this.field_2618 = arg2;
	}

	@Override
	protected void init() {
		int i = 0;
		this.addButton(new class_444(this.minecraft, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), class_3419.field_15250, 310));
		i += 2;

		for (class_3419 lv : class_3419.values()) {
			if (lv != class_3419.field_15250) {
				this.addButton(new class_444(this.minecraft, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), lv, 150));
				i++;
			}
		}

		this.addButton(
			new 1(
				this.width / 2 - 75, this.height / 6 - 12 + 24 * (++i >> 1), 150, 20, class_316.field_18188, class_316.field_18188.method_18495(this.field_2618), arg -> {
					class_316.field_18188.method_18491(this.minecraft.field_1690);
					arg.setMessage(class_316.field_18188.method_18495(this.minecraft.field_1690));
					this.minecraft.field_1690.method_1640();
				}
			)
		);
		this.addButton(
			new class_4185(this.width / 2 - 100, this.height / 6 + 168, 200, 20, class_1074.method_4662("gui.done"), arg -> this.minecraft.method_1507(this.field_2617))
		);
	}

	@Override
	public void removed() {
		this.minecraft.field_1690.method_1640();
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 15, 16777215);
		super.render(i, j, f);
	}
}
