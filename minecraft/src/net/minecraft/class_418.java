package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_418 extends class_437 {
	private int field_2451;
	private final class_2561 field_2450;

	public class_418(@Nullable class_2561 arg) {
		this.field_2450 = arg;
	}

	@Override
	protected void method_2224() {
		this.field_2451 = 0;
		String string;
		String string2;
		if (this.field_2563.field_1687.method_8401().method_152()) {
			string = class_1074.method_4662("deathScreen.spectate");
			string2 = class_1074.method_4662("deathScreen." + (this.field_2563.method_1542() ? "deleteWorld" : "leaveServer"));
		} else {
			string = class_1074.method_4662("deathScreen.respawn");
			string2 = class_1074.method_4662("deathScreen.titleScreen");
		}

		this.method_2219(new class_339(0, this.field_2561 / 2 - 100, this.field_2559 / 4 + 72, string) {
			@Override
			public void method_1826(double d, double e) {
				class_418.this.field_2563.field_1724.method_7331();
				class_418.this.field_2563.method_1507(null);
			}
		});
		class_339 lv = this.method_2219(
			new class_339(1, this.field_2561 / 2 - 100, this.field_2559 / 4 + 96, string2) {
				@Override
				public void method_1826(double d, double e) {
					if (class_418.this.field_2563.field_1687.method_8401().method_152()) {
						class_418.this.field_2563.method_1507(new class_442());
					} else {
						class_410 lv = new class_410(
							class_418.this,
							class_1074.method_4662("deathScreen.quit.confirm"),
							"",
							class_1074.method_4662("deathScreen.titleScreen"),
							class_1074.method_4662("deathScreen.respawn"),
							0
						);
						class_418.this.field_2563.method_1507(lv);
						lv.method_2125(20);
					}
				}
			}
		);
		if (!this.field_2563.field_1687.method_8401().method_152() && this.field_2563.method_1548() == null) {
			lv.field_2078 = false;
		}

		for (class_339 lv2 : this.field_2564) {
			lv2.field_2078 = false;
		}
	}

	@Override
	public boolean method_16890() {
		return false;
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		if (i == 31102009) {
			super.confirmResult(bl, i);
		} else if (bl) {
			if (this.field_2563.field_1687 != null) {
				this.field_2563.field_1687.method_8525();
			}

			this.field_2563.method_1550(null, new class_424(class_1074.method_4662("menu.savingLevel")));
			this.field_2563.method_1507(new class_442());
		} else {
			this.field_2563.field_1724.method_7331();
			this.field_2563.method_1507(null);
		}
	}

	@Override
	public void method_2214(int i, int j, float f) {
		boolean bl = this.field_2563.field_1687.method_8401().method_152();
		this.method_1782(0, 0, this.field_2561, this.field_2559, 1615855616, -1602211792);
		GlStateManager.pushMatrix();
		GlStateManager.scalef(2.0F, 2.0F, 2.0F);
		this.method_1789(this.field_2554, class_1074.method_4662(bl ? "deathScreen.title.hardcore" : "deathScreen.title"), this.field_2561 / 2 / 2, 30, 16777215);
		GlStateManager.popMatrix();
		if (this.field_2450 != null) {
			this.method_1789(this.field_2554, this.field_2450.method_10863(), this.field_2561 / 2, 85, 16777215);
		}

		this.method_1789(
			this.field_2554,
			class_1074.method_4662("deathScreen.score") + ": " + class_124.field_1054 + this.field_2563.field_1724.method_7272(),
			this.field_2561 / 2,
			100,
			16777215
		);
		if (this.field_2450 != null && j > 85 && j < 85 + 9) {
			class_2561 lv = this.method_2164(i);
			if (lv != null && lv.method_10866().method_10969() != null) {
				this.method_2229(lv, i, j);
			}
		}

		super.method_2214(i, j, f);
	}

	@Nullable
	public class_2561 method_2164(int i) {
		if (this.field_2450 == null) {
			return null;
		} else {
			int j = this.field_2563.field_1772.method_1727(this.field_2450.method_10863());
			int k = this.field_2561 / 2 - j / 2;
			int l = this.field_2561 / 2 + j / 2;
			int m = k;
			if (i >= k && i <= l) {
				for (class_2561 lv : this.field_2450) {
					m += this.field_2563.field_1772.method_1727(class_341.method_1849(lv.method_10851(), false));
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
	public boolean method_16807(double d, double e, int i) {
		if (this.field_2450 != null && e > 85.0 && e < (double)(85 + 9)) {
			class_2561 lv = this.method_2164((int)d);
			if (lv != null && lv.method_10866().method_10970() != null && lv.method_10866().method_10970().method_10845() == class_2558.class_2559.field_11749) {
				this.method_2216(lv);
				return false;
			}
		}

		return super.method_16807(d, e, i);
	}

	@Override
	public boolean method_2222() {
		return false;
	}

	@Override
	public void method_2225() {
		super.method_2225();
		this.field_2451++;
		if (this.field_2451 == 20) {
			for (class_339 lv : this.field_2564) {
				lv.field_2078 = true;
			}
		}
	}
}
