package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmsBridge;

@Environment(EnvType.CLIENT)
public class class_433 extends class_437 {
	@Override
	protected void method_2224() {
		int i = -16;
		int j = 98;
		class_339 lv = this.method_2219(
			new class_339(1, this.field_2561 / 2 - 102, this.field_2559 / 4 + 120 + -16, 204, 20, class_1074.method_4662("menu.returnToMenu")) {
				@Override
				public void method_1826(double d, double e) {
					boolean bl = class_433.this.field_2563.method_1542();
					boolean bl2 = class_433.this.field_2563.method_1589();
					this.field_2078 = false;
					class_433.this.field_2563.field_1687.method_8525();
					if (bl) {
						class_433.this.field_2563.method_1550(null, new class_424(class_1074.method_4662("menu.savingLevel")));
					} else {
						class_433.this.field_2563.method_1481(null);
					}

					if (bl) {
						class_433.this.field_2563.method_1507(new class_442());
					} else if (bl2) {
						RealmsBridge realmsBridge = new RealmsBridge();
						realmsBridge.switchToRealms(new class_442());
					} else {
						class_433.this.field_2563.method_1507(new class_500(new class_442()));
					}
				}
			}
		);
		if (!this.field_2563.method_1542()) {
			lv.field_2074 = class_1074.method_4662("menu.disconnect");
		}

		this.method_2219(new class_339(4, this.field_2561 / 2 - 102, this.field_2559 / 4 + 24 + -16, 204, 20, class_1074.method_4662("menu.returnToGame")) {
			@Override
			public void method_1826(double d, double e) {
				class_433.this.field_2563.method_1507(null);
				class_433.this.field_2563.field_1729.method_1612();
			}
		});
		this.method_2219(new class_339(0, this.field_2561 / 2 - 102, this.field_2559 / 4 + 96 + -16, 98, 20, class_1074.method_4662("menu.options")) {
			@Override
			public void method_1826(double d, double e) {
				class_433.this.field_2563.method_1507(new class_429(class_433.this, class_433.this.field_2563.field_1690));
			}
		});
		class_339 lv2 = this.method_2219(
			new class_339(7, this.field_2561 / 2 + 4, this.field_2559 / 4 + 96 + -16, 98, 20, class_1074.method_4662("menu.shareToLan")) {
				@Override
				public void method_1826(double d, double e) {
					class_433.this.field_2563.method_1507(new class_436(class_433.this));
				}
			}
		);
		lv2.field_2078 = this.field_2563.method_1496() && !this.field_2563.method_1576().method_3860();
		this.method_2219(new class_339(5, this.field_2561 / 2 - 102, this.field_2559 / 4 + 48 + -16, 98, 20, class_1074.method_4662("gui.advancements")) {
			@Override
			public void method_1826(double d, double e) {
				class_433.this.field_2563.method_1507(new class_457(class_433.this.field_2563.field_1724.field_3944.method_2869()));
			}
		});
		this.method_2219(new class_339(6, this.field_2561 / 2 + 4, this.field_2559 / 4 + 48 + -16, 98, 20, class_1074.method_4662("gui.stats")) {
			@Override
			public void method_1826(double d, double e) {
				class_433.this.field_2563.method_1507(new class_447(class_433.this, class_433.this.field_2563.field_1724.method_3143()));
			}
		});
		this.method_2219(new class_339(8, this.field_2561 / 2 - 102, this.field_2559 / 4 + 72 + -16, 98, 20, class_1074.method_4662("menu.sendFeedback")) {
			@Override
			public void method_1826(double d, double e) {
				class_433.this.field_2563.method_1507(new class_407((bl, i) -> {
					if (bl) {
						class_156.method_668().method_670("https://aka.ms/snapshotfeedback?ref=game");
					}

					class_433.this.field_2563.method_1507(class_433.this);
				}, "https://aka.ms/snapshotfeedback?ref=game", 0, true));
			}
		});
		this.method_2219(new class_339(9, this.field_2561 / 2 + 4, this.field_2559 / 4 + 72 + -16, 98, 20, class_1074.method_4662("menu.reportBugs")) {
			@Override
			public void method_1826(double d, double e) {
				class_433.this.field_2563.method_1507(new class_407((bl, i) -> {
					if (bl) {
						class_156.method_668().method_670("https://aka.ms/snapshotbugs?ref=game");
					}

					class_433.this.field_2563.method_1507(class_433.this);
				}, "https://aka.ms/snapshotbugs?ref=game", 0, true));
			}
		});
	}

	@Override
	public void method_2225() {
		super.method_2225();
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.method_1789(this.field_2554, class_1074.method_4662("menu.game"), this.field_2561 / 2, 40, 16777215);
		super.method_2214(i, j, f);
	}
}
