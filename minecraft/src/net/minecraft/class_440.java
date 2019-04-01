package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_440 extends class_437 {
	private final class_437 field_2577;

	public class_440(class_437 arg) {
		super(new class_2588("options.skinCustomisation.title"));
		this.field_2577 = arg;
	}

	@Override
	protected void init() {
		int i = 0;

		for (class_1664 lv : class_1664.values()) {
			this.addButton(new class_4185(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, this.method_2248(lv), arg2 -> {
				this.minecraft.field_1690.method_1631(lv);
				arg2.setMessage(this.method_2248(lv));
			}));
			i++;
		}

		this.addButton(
			new 1(
				this.width / 2 - 155 + i % 2 * 160,
				this.height / 6 + 24 * (i >> 1),
				150,
				20,
				class_316.field_18193,
				class_316.field_18193.method_18501(this.minecraft.field_1690),
				arg -> {
					class_316.field_18193.method_18500(this.minecraft.field_1690, 1);
					this.minecraft.field_1690.method_1640();
					arg.setMessage(class_316.field_18193.method_18501(this.minecraft.field_1690));
					this.minecraft.field_1690.method_1643();
				}
			)
		);
		if (++i % 2 == 1) {
			i++;
		}

		this.addButton(
			new class_4185(
				this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), 200, 20, class_1074.method_4662("gui.done"), arg -> this.minecraft.method_1507(this.field_2577)
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

	private String method_2248(class_1664 arg) {
		String string;
		if (this.minecraft.field_1690.method_1633().contains(arg)) {
			string = class_1074.method_4662("options.on");
		} else {
			string = class_1074.method_4662("options.off");
		}

		return arg.method_7428().method_10863() + ": " + string;
	}
}
