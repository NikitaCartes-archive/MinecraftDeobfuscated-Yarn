package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_429 extends class_437 {
	private static final class_316[] field_2504 = new class_316[]{class_316.field_1964};
	private final class_437 field_2501;
	private final class_315 field_2502;
	private class_4185 field_2500;
	private class_347 field_2503;
	private class_1267 field_18745;

	public class_429(class_437 arg, class_315 arg2) {
		super(new class_2588("options.title"));
		this.field_2501 = arg;
		this.field_2502 = arg2;
	}

	@Override
	protected void init() {
		int i = 0;

		for (class_316 lv : field_2504) {
			int j = this.width / 2 - 155 + i % 2 * 160;
			int k = this.height / 6 - 12 + 24 * (i >> 1);
			this.addButton(lv.method_18520(this.minecraft.field_1690, j, k, 150));
			i++;
		}

		if (this.minecraft.field_1687 != null) {
			this.field_18745 = this.minecraft.field_1687.method_8407();
			this.field_2500 = this.addButton(
				new class_4185(this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, this.method_2189(this.field_18745), arg -> {
					this.field_18745 = class_1267.method_5462(this.field_18745.method_5461() + 1);
					this.minecraft.method_1562().method_2883(new class_4210(this.field_18745));
					this.field_2500.setMessage(this.method_2189(this.field_18745));
				})
			);
			if (this.minecraft.method_1496() && !this.minecraft.field_1687.method_8401().method_152()) {
				this.field_2500.setWidth(this.field_2500.getWidth() - 20);
				this.field_2503 = this.addButton(
					new class_347(
						this.field_2500.x + this.field_2500.getWidth(),
						this.field_2500.y,
						arg -> this.minecraft
								.method_1507(
									new class_410(
										this,
										new class_2588("difficulty.lock.title"),
										new class_2588("difficulty.lock.question", new class_2588("options.difficulty." + this.minecraft.field_1687.method_8401().method_207().method_5460())),
										109
									)
								)
					)
				);
				this.field_2503.method_1895(this.minecraft.field_1687.method_8401().method_197());
				this.field_2503.active = !this.field_2503.method_1896();
				this.field_2500.active = !this.field_2503.method_1896();
			} else {
				this.field_2500.active = false;
			}
		} else {
			this.addButton(
				new 1(
					this.width / 2 - 155 + i % 2 * 160,
					this.height / 6 - 12 + 24 * (i >> 1),
					150,
					20,
					class_316.field_18186,
					class_316.field_18186.method_18495(this.field_2502),
					arg -> {
						class_316.field_18186.method_18491(this.field_2502);
						this.field_2502.method_1640();
						arg.setMessage(class_316.field_18186.method_18495(this.field_2502));
					}
				)
			);
		}

		this.addButton(
			new class_4185(
				this.width / 2 - 155,
				this.height / 6 + 48 - 6,
				150,
				20,
				class_1074.method_4662("options.skinCustomisation"),
				arg -> this.minecraft.method_1507(new class_440(this))
			)
		);
		this.addButton(
			new class_4185(
				this.width / 2 + 5,
				this.height / 6 + 48 - 6,
				150,
				20,
				class_1074.method_4662("options.sounds"),
				arg -> this.minecraft.method_1507(new class_443(this, this.field_2502))
			)
		);
		this.addButton(
			new class_4185(
				this.width / 2 - 155,
				this.height / 6 + 72 - 6,
				150,
				20,
				class_1074.method_4662("options.video"),
				arg -> this.minecraft.method_1507(new class_446(this, this.field_2502))
			)
		);
		this.addButton(
			new class_4185(
				this.width / 2 + 5,
				this.height / 6 + 72 - 6,
				150,
				20,
				class_1074.method_4662("options.controls"),
				arg -> this.minecraft.method_1507(new class_458(this, this.field_2502))
			)
		);
		this.addButton(
			new class_4185(
				this.width / 2 - 155,
				this.height / 6 + 96 - 6,
				150,
				20,
				class_1074.method_4662("options.language"),
				arg -> this.minecraft.method_1507(new class_426(this, this.field_2502, this.minecraft.method_1526()))
			)
		);
		this.addButton(
			new class_4185(
				this.width / 2 + 5,
				this.height / 6 + 96 - 6,
				150,
				20,
				class_1074.method_4662("options.chat.title"),
				arg -> this.minecraft.method_1507(new class_404(this, this.field_2502))
			)
		);
		this.addButton(
			new class_4185(
				this.width / 2 - 155,
				this.height / 6 + 120 - 6,
				150,
				20,
				class_1074.method_4662("options.resourcepack"),
				arg -> this.minecraft.method_1507(new class_519(this))
			)
		);
		this.addButton(
			new class_4185(
				this.width / 2 + 5,
				this.height / 6 + 120 - 6,
				150,
				20,
				class_1074.method_4662("options.accessibility.title"),
				arg -> this.minecraft.method_1507(new class_4189(this, this.field_2502))
			)
		);
		this.addButton(
			new class_4185(this.width / 2 - 100, this.height / 6 + 168, 200, 20, class_1074.method_4662("gui.done"), arg -> this.minecraft.method_1507(this.field_2501))
		);
	}

	public String method_2189(class_1267 arg) {
		return new class_2588("options.difficulty").method_10864(": ").method_10852(arg.method_5463()).method_10863();
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		this.minecraft.method_1507(this);
		if (i == 109 && bl && this.minecraft.field_1687 != null) {
			this.minecraft.method_1562().method_2883(new class_4211(true));
			this.field_2503.method_1895(true);
			this.field_2503.active = false;
			this.field_2500.active = false;
		}
	}

	@Override
	public void removed() {
		this.field_2502.method_1640();
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 15, 16777215);
		super.render(i, j, f);
	}
}
