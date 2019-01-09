package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_458 extends class_437 {
	private static final class_315.class_316[] field_2726 = new class_315.class_316[]{
		class_315.class_316.field_1963, class_315.class_316.field_1944, class_315.class_316.field_1930, class_315.class_316.field_1960
	};
	private final class_437 field_2729;
	protected String field_2722 = "Controls";
	private final class_315 field_2724;
	public class_304 field_2727;
	public long field_2723;
	private class_459 field_2728;
	private class_339 field_2725;

	public class_458(class_437 arg, class_315 arg2) {
		this.field_2729 = arg;
		this.field_2724 = arg2;
	}

	@Override
	protected void method_2224() {
		this.field_2728 = new class_459(this, this.field_2563);
		this.field_2557.add(this.field_2728);
		this.method_1967(this.field_2728);
		this.method_2219(new class_339(200, this.field_2561 / 2 - 155 + 160, this.field_2559 - 29, 150, 20, class_1074.method_4662("gui.done")) {
			@Override
			public void method_1826(double d, double e) {
				class_458.this.field_2563.method_1507(class_458.this.field_2729);
			}
		});
		this.field_2725 = this.method_2219(
			new class_339(201, this.field_2561 / 2 - 155, this.field_2559 - 29, 150, 20, class_1074.method_4662("controls.resetAll")) {
				@Override
				public void method_1826(double d, double e) {
					for (class_304 lv : class_458.this.field_2563.field_1690.field_1839) {
						lv.method_1422(lv.method_1429());
					}

					class_304.method_1426();
				}
			}
		);
		this.field_2722 = class_1074.method_4662("controls.title");
		int i = 0;

		for (class_315.class_316 lv : field_2726) {
			if (lv.method_1653()) {
				this.method_2219(new class_357(lv.method_1647(), this.field_2561 / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), lv));
			} else {
				this.method_2219(new class_349(lv.method_1647(), this.field_2561 / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), lv, this.field_2724.method_1642(lv)) {
					@Override
					public void method_1826(double d, double e) {
						class_458.this.field_2724.method_1629(this.method_1899(), 1);
						this.field_2074 = class_458.this.field_2724.method_1642(class_315.class_316.method_1655(this.field_2077));
					}
				});
			}

			i++;
		}
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		if (this.field_2727 != null) {
			this.field_2724.method_1641(this.field_2727, class_3675.class_307.field_1672.method_1447(i));
			this.field_2727 = null;
			class_304.method_1426();
			return true;
		} else if (i == 0 && this.field_2728.method_16807(d, e, i)) {
			this.method_1966(true);
			this.method_1967(this.field_2728);
			return true;
		} else {
			return super.method_16807(d, e, i);
		}
	}

	@Override
	public boolean method_16804(double d, double e, int i) {
		if (i == 0 && this.field_2728.method_16804(d, e, i)) {
			this.method_1966(false);
			return true;
		} else {
			return super.method_16804(d, e, i);
		}
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		if (this.field_2727 != null) {
			if (i == 256) {
				this.field_2724.method_1641(this.field_2727, class_3675.field_16237);
			} else {
				this.field_2724.method_1641(this.field_2727, class_3675.method_15985(i, j));
			}

			this.field_2727 = null;
			this.field_2723 = class_156.method_658();
			class_304.method_1426();
			return true;
		} else {
			return super.method_16805(i, j, k);
		}
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.field_2728.method_1930(i, j, f);
		this.method_1789(this.field_2554, this.field_2722, this.field_2561 / 2, 8, 16777215);
		boolean bl = false;

		for (class_304 lv : this.field_2724.field_1839) {
			if (!lv.method_1427()) {
				bl = true;
				break;
			}
		}

		this.field_2725.field_2078 = bl;
		super.method_2214(i, j, f);
	}
}
