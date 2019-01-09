package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_429 extends class_437 {
	private static final class_315.class_316[] field_2504 = new class_315.class_316[]{class_315.class_316.field_1964};
	private final class_437 field_2501;
	private final class_315 field_2502;
	private class_339 field_2500;
	private class_347 field_2503;
	protected String field_2499 = "Options";

	public class_429(class_437 arg, class_315 arg2) {
		this.field_2501 = arg;
		this.field_2502 = arg2;
	}

	@Override
	protected void method_2224() {
		this.field_2499 = class_1074.method_4662("options.title");
		int i = 0;

		for (class_315.class_316 lv : field_2504) {
			if (lv.method_1653()) {
				this.method_2219(new class_357(lv.method_1647(), this.field_2561 / 2 - 155 + i % 2 * 160, this.field_2559 / 6 - 12 + 24 * (i >> 1), lv));
			} else {
				class_349 lv2 = new class_349(
					lv.method_1647(), this.field_2561 / 2 - 155 + i % 2 * 160, this.field_2559 / 6 - 12 + 24 * (i >> 1), lv, this.field_2502.method_1642(lv)
				) {
					@Override
					public void method_1826(double d, double e) {
						class_429.this.field_2502.method_1629(this.method_1899(), 1);
						this.field_2074 = class_429.this.field_2502.method_1642(class_315.class_316.method_1655(this.field_2077));
					}
				};
				this.method_2219(lv2);
			}

			i++;
		}

		if (this.field_2563.field_1687 != null) {
			class_1267 lv3 = this.field_2563.field_1687.method_8407();
			this.field_2500 = new class_339(108, this.field_2561 / 2 - 155 + i % 2 * 160, this.field_2559 / 6 - 12 + 24 * (i >> 1), 150, 20, this.method_2189(lv3)) {
				@Override
				public void method_1826(double d, double e) {
					class_429.this.field_2563
						.field_1687
						.method_8401()
						.method_208(class_1267.method_5462(class_429.this.field_2563.field_1687.method_8407().method_5461() + 1));
					class_429.this.field_2500.field_2074 = class_429.this.method_2189(class_429.this.field_2563.field_1687.method_8407());
				}
			};
			this.method_2219(this.field_2500);
			if (this.field_2563.method_1496() && !this.field_2563.field_1687.method_8401().method_152()) {
				this.field_2500.method_1821(this.field_2500.method_1825() - 20);
				this.field_2503 = new class_347(109, this.field_2500.field_2069 + this.field_2500.method_1825(), this.field_2500.field_2068) {
					@Override
					public void method_1826(double d, double e) {
						class_429.this.field_2563
							.method_1507(
								new class_410(
									class_429.this,
									new class_2588("difficulty.lock.title").method_10863(),
									new class_2588("difficulty.lock.question", new class_2588(class_429.this.field_2563.field_1687.method_8401().method_207().method_5460()))
										.method_10863(),
									109
								)
							);
					}
				};
				this.method_2219(this.field_2503);
				this.field_2503.method_1895(this.field_2563.field_1687.method_8401().method_197());
				this.field_2503.field_2078 = !this.field_2503.method_1896();
				this.field_2500.field_2078 = !this.field_2503.method_1896();
			} else {
				this.field_2500.field_2078 = false;
			}
		} else {
			this.method_2219(
				new class_349(
					class_315.class_316.field_1953.method_1647(),
					this.field_2561 / 2 - 155 + i % 2 * 160,
					this.field_2559 / 6 - 12 + 24 * (i >> 1),
					class_315.class_316.field_1953,
					this.field_2502.method_1642(class_315.class_316.field_1953)
				) {
					@Override
					public void method_1826(double d, double e) {
						class_429.this.field_2502.method_1629(this.method_1899(), 1);
						this.field_2074 = class_429.this.field_2502.method_1642(class_315.class_316.method_1655(this.field_2077));
					}
				}
			);
		}

		this.method_2219(new class_339(110, this.field_2561 / 2 - 155, this.field_2559 / 6 + 48 - 6, 150, 20, class_1074.method_4662("options.skinCustomisation")) {
			@Override
			public void method_1826(double d, double e) {
				class_429.this.field_2563.field_1690.method_1640();
				class_429.this.field_2563.method_1507(new class_440(class_429.this));
			}
		});
		this.method_2219(new class_339(106, this.field_2561 / 2 + 5, this.field_2559 / 6 + 48 - 6, 150, 20, class_1074.method_4662("options.sounds")) {
			@Override
			public void method_1826(double d, double e) {
				class_429.this.field_2563.field_1690.method_1640();
				class_429.this.field_2563.method_1507(new class_443(class_429.this, class_429.this.field_2502));
			}
		});
		this.method_2219(new class_339(101, this.field_2561 / 2 - 155, this.field_2559 / 6 + 72 - 6, 150, 20, class_1074.method_4662("options.video")) {
			@Override
			public void method_1826(double d, double e) {
				class_429.this.field_2563.field_1690.method_1640();
				class_429.this.field_2563.method_1507(new class_446(class_429.this, class_429.this.field_2502));
			}
		});
		this.method_2219(new class_339(100, this.field_2561 / 2 + 5, this.field_2559 / 6 + 72 - 6, 150, 20, class_1074.method_4662("options.controls")) {
			@Override
			public void method_1826(double d, double e) {
				class_429.this.field_2563.field_1690.method_1640();
				class_429.this.field_2563.method_1507(new class_458(class_429.this, class_429.this.field_2502));
			}
		});
		this.method_2219(new class_339(102, this.field_2561 / 2 - 155, this.field_2559 / 6 + 96 - 6, 150, 20, class_1074.method_4662("options.language")) {
			@Override
			public void method_1826(double d, double e) {
				class_429.this.field_2563.field_1690.method_1640();
				class_429.this.field_2563.method_1507(new class_426(class_429.this, class_429.this.field_2502, class_429.this.field_2563.method_1526()));
			}
		});
		this.method_2219(new class_339(103, this.field_2561 / 2 + 5, this.field_2559 / 6 + 96 - 6, 150, 20, class_1074.method_4662("options.chat.title")) {
			@Override
			public void method_1826(double d, double e) {
				class_429.this.field_2563.field_1690.method_1640();
				class_429.this.field_2563.method_1507(new class_404(class_429.this, class_429.this.field_2502));
			}
		});
		this.method_2219(new class_339(105, this.field_2561 / 2 - 155, this.field_2559 / 6 + 120 - 6, 150, 20, class_1074.method_4662("options.resourcepack")) {
			@Override
			public void method_1826(double d, double e) {
				class_429.this.field_2563.field_1690.method_1640();
				class_429.this.field_2563.method_1507(new class_519(class_429.this));
			}
		});
		this.method_2219(new class_339(104, this.field_2561 / 2 + 5, this.field_2559 / 6 + 120 - 6, 150, 20, class_1074.method_4662("options.snooper.view")) {
			@Override
			public void method_1826(double d, double e) {
				class_429.this.field_2563.field_1690.method_1640();
				class_429.this.field_2563.method_1507(new class_438(class_429.this, class_429.this.field_2502));
			}
		});
		this.method_2219(new class_339(200, this.field_2561 / 2 - 100, this.field_2559 / 6 + 168, class_1074.method_4662("gui.done")) {
			@Override
			public void method_1826(double d, double e) {
				class_429.this.field_2563.field_1690.method_1640();
				class_429.this.field_2563.method_1507(class_429.this.field_2501);
			}
		});
	}

	public String method_2189(class_1267 arg) {
		return new class_2588("options.difficulty").method_10864(": ").method_10852(arg.method_5463()).method_10863();
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		this.field_2563.method_1507(this);
		if (i == 109 && bl && this.field_2563.field_1687 != null) {
			this.field_2563.field_1687.method_8401().method_186(true);
			this.field_2503.method_1895(true);
			this.field_2503.field_2078 = false;
			this.field_2500.field_2078 = false;
		}
	}

	@Override
	public void method_2210() {
		this.field_2563.field_1690.method_1640();
		super.method_2210();
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.method_1789(this.field_2554, this.field_2499, this.field_2561 / 2, 15, 16777215);
		super.method_2214(i, j, f);
	}
}
