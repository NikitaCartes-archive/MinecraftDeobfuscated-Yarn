package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_418 extends class_437 {
	private int field_2451;
	private final class_2561 field_2450;
	private final boolean field_18974;

	public class_418(@Nullable class_2561 arg, boolean bl) {
		super(new class_2588(bl ? "deathScreen.title.hardcore" : "deathScreen.title"));
		this.field_2450 = arg;
		this.field_18974 = bl;
	}

	@Override
	protected void init() {
		this.field_2451 = 0;
		String string;
		String string2;
		if (this.field_18974) {
			string = class_1074.method_4662("deathScreen.spectate");
			string2 = class_1074.method_4662("deathScreen." + (this.minecraft.method_1542() ? "deleteWorld" : "leaveServer"));
		} else {
			string = class_1074.method_4662("deathScreen.respawn");
			string2 = class_1074.method_4662("deathScreen.titleScreen");
		}

		this.addButton(new class_4185(this.width / 2 - 100, this.height / 4 + 72, 200, 20, string, arg -> {
			this.minecraft.field_1724.method_7331();
			this.minecraft.method_1507(null);
		}));
		class_4185 lv = this.addButton(
			new class_4185(
				this.width / 2 - 100,
				this.height / 4 + 96,
				200,
				20,
				string2,
				arg -> {
					if (this.field_18974) {
						this.minecraft.method_1507(new class_442());
					} else {
						class_410 lvx = new class_410(
							this::method_20373,
							new class_2588("deathScreen.quit.confirm"),
							new class_2585(""),
							class_1074.method_4662("deathScreen.titleScreen"),
							class_1074.method_4662("deathScreen.respawn")
						);
						this.minecraft.method_1507(lvx);
						lvx.method_2125(20);
					}
				}
			)
		);
		if (!this.field_18974 && this.minecraft.method_1548() == null) {
			lv.active = false;
		}

		for (class_339 lv2 : this.buttons) {
			lv2.active = false;
		}
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	private void method_20373(boolean bl) {
		if (bl) {
			if (this.minecraft.field_1687 != null) {
				this.minecraft.field_1687.method_8525();
			}

			this.minecraft.method_18096(new class_424(new class_2588("menu.savingLevel")));
			this.minecraft.method_1507(new class_442());
		} else {
			this.minecraft.field_1724.method_7331();
			this.minecraft.method_1507(null);
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.fillGradient(0, 0, this.width, this.height, 1615855616, -1602211792);
		GlStateManager.pushMatrix();
		GlStateManager.scalef(2.0F, 2.0F, 2.0F);
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2 / 2, 30, 16777215);
		GlStateManager.popMatrix();
		if (this.field_2450 != null) {
			this.drawCenteredString(this.font, this.field_2450.method_10863(), this.width / 2, 85, 16777215);
		}

		this.drawCenteredString(
			this.font,
			class_1074.method_4662("deathScreen.score") + ": " + class_124.field_1054 + this.minecraft.field_1724.method_7272(),
			this.width / 2,
			100,
			16777215
		);
		if (this.field_2450 != null && j > 85 && j < 85 + 9) {
			class_2561 lv = this.method_2164(i);
			if (lv != null && lv.method_10866().method_10969() != null) {
				this.renderComponentHoverEffect(lv, i, j);
			}
		}

		super.render(i, j, f);
	}

	@Nullable
	public class_2561 method_2164(int i) {
		if (this.field_2450 == null) {
			return null;
		} else {
			int j = this.minecraft.field_1772.method_1727(this.field_2450.method_10863());
			int k = this.width / 2 - j / 2;
			int l = this.width / 2 + j / 2;
			int m = k;
			if (i >= k && i <= l) {
				for (class_2561 lv : this.field_2450) {
					m += this.minecraft.field_1772.method_1727(class_341.method_1849(lv.method_10851(), false));
					if (m > i) {
						return lv;
					}
				}

				return null;
			} else {
				return null;
			}
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.field_2450 != null && e > 85.0 && e < (double)(85 + 9)) {
			class_2561 lv = this.method_2164((int)d);
			if (lv != null && lv.method_10866().method_10970() != null && lv.method_10866().method_10970().method_10845() == class_2558.class_2559.field_11749) {
				this.handleComponentClicked(lv);
				return false;
			}
		}

		return super.mouseClicked(d, e, i);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		this.field_2451++;
		if (this.field_2451 == 20) {
			for (class_339 lv : this.buttons) {
				lv.active = true;
			}
		}
	}
}
