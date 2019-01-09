package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_413 extends class_437 {
	private final class_525 field_2422;
	private class_3232 field_2419 = class_3232.method_14309();
	private String field_2423;
	private String field_2418;
	private String field_2425;
	private class_413.class_414 field_2424;
	private class_339 field_2426;
	private class_339 field_2420;
	private class_339 field_2421;

	public class_413(class_525 arg, class_2487 arg2) {
		this.field_2422 = arg;
		this.method_2144(arg2);
	}

	public String method_2138() {
		return this.field_2419.toString();
	}

	public class_2487 method_2140() {
		return (class_2487)this.field_2419.method_14313(class_2509.field_11560).getValue();
	}

	public void method_2139(String string) {
		this.field_2419 = class_3232.method_14319(string);
	}

	public void method_2144(class_2487 arg) {
		this.field_2419 = class_3232.method_14323(new Dynamic<>(class_2509.field_11560, arg));
	}

	@Override
	protected void method_2224() {
		this.field_2423 = class_1074.method_4662("createWorld.customize.flat.title");
		this.field_2418 = class_1074.method_4662("createWorld.customize.flat.tile");
		this.field_2425 = class_1074.method_4662("createWorld.customize.flat.height");
		this.field_2424 = new class_413.class_414();
		this.field_2557.add(this.field_2424);
		this.field_2426 = this.method_2219(
			new class_339(2, this.field_2561 / 2 - 154, this.field_2559 - 52, 100, 20, class_1074.method_4662("createWorld.customize.flat.addLayer") + " (NYI)") {
				@Override
				public void method_1826(double d, double e) {
					class_413.this.field_2419.method_14330();
					class_413.this.method_2145();
				}
			}
		);
		this.field_2420 = this.method_2219(
			new class_339(3, this.field_2561 / 2 - 50, this.field_2559 - 52, 100, 20, class_1074.method_4662("createWorld.customize.flat.editLayer") + " (NYI)") {
				@Override
				public void method_1826(double d, double e) {
					class_413.this.field_2419.method_14330();
					class_413.this.method_2145();
				}
			}
		);
		this.field_2421 = this.method_2219(
			new class_339(4, this.field_2561 / 2 - 155, this.field_2559 - 52, 150, 20, class_1074.method_4662("createWorld.customize.flat.removeLayer")) {
				@Override
				public void method_1826(double d, double e) {
					if (class_413.this.method_2147()) {
						List<class_3229> list = class_413.this.field_2419.method_14327();
						int i = list.size() - class_413.this.field_2424.field_2428 - 1;
						list.remove(i);
						class_413.this.field_2424.field_2428 = Math.min(class_413.this.field_2424.field_2428, list.size() - 1);
						class_413.this.field_2419.method_14330();
						class_413.this.method_2145();
					}
				}
			}
		);
		this.method_2219(new class_339(0, this.field_2561 / 2 - 155, this.field_2559 - 28, 150, 20, class_1074.method_4662("gui.done")) {
			@Override
			public void method_1826(double d, double e) {
				class_413.this.field_2422.field_3200 = class_413.this.method_2140();
				class_413.this.field_2563.method_1507(class_413.this.field_2422);
				class_413.this.field_2419.method_14330();
				class_413.this.method_2145();
			}
		});
		this.method_2219(new class_339(5, this.field_2561 / 2 + 5, this.field_2559 - 52, 150, 20, class_1074.method_4662("createWorld.customize.presets")) {
			@Override
			public void method_1826(double d, double e) {
				class_413.this.field_2563.method_1507(new class_430(class_413.this));
				class_413.this.field_2419.method_14330();
				class_413.this.method_2145();
			}
		});
		this.method_2219(new class_339(1, this.field_2561 / 2 + 5, this.field_2559 - 28, 150, 20, class_1074.method_4662("gui.cancel")) {
			@Override
			public void method_1826(double d, double e) {
				class_413.this.field_2563.method_1507(class_413.this.field_2422);
				class_413.this.field_2419.method_14330();
				class_413.this.method_2145();
			}
		});
		this.field_2426.field_2076 = false;
		this.field_2420.field_2076 = false;
		this.field_2419.method_14330();
		this.method_2145();
	}

	public void method_2145() {
		boolean bl = this.method_2147();
		this.field_2421.field_2078 = bl;
		this.field_2420.field_2078 = bl;
		this.field_2420.field_2078 = false;
		this.field_2426.field_2078 = false;
	}

	private boolean method_2147() {
		return this.field_2424.field_2428 > -1 && this.field_2424.field_2428 < this.field_2419.method_14327().size();
	}

	@Nullable
	@Override
	public class_364 getFocused() {
		return this.field_2424;
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.field_2424.method_1930(i, j, f);
		this.method_1789(this.field_2554, this.field_2423, this.field_2561 / 2, 8, 16777215);
		int k = this.field_2561 / 2 - 92 - 16;
		this.method_1780(this.field_2554, this.field_2418, k, 32, 16777215);
		this.method_1780(this.field_2554, this.field_2425, k + 2 + 213 - this.field_2554.method_1727(this.field_2425), 32, 16777215);
		super.method_2214(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	class class_414 extends class_358 {
		public int field_2428 = -1;

		public class_414() {
			super(class_413.this.field_2563, class_413.this.field_2561, class_413.this.field_2559, 43, class_413.this.field_2559 - 60, 24);
		}

		private void method_2148(int i, int j, class_1799 arg) {
			this.method_2149(i + 1, j + 1);
			GlStateManager.enableRescaleNormal();
			if (!arg.method_7960()) {
				class_308.method_1453();
				class_413.this.field_2560.method_4010(arg, i + 2, j + 2);
				class_308.method_1450();
			}

			GlStateManager.disableRescaleNormal();
		}

		private void method_2149(int i, int j) {
			this.method_2150(i, j, 0, 0);
		}

		private void method_2150(int i, int j, int k, int l) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_2164.method_1531().method_4618(field_2052);
			float f = 0.0078125F;
			float g = 0.0078125F;
			int m = 18;
			int n = 18;
			class_289 lv = class_289.method_1348();
			class_287 lv2 = lv.method_1349();
			lv2.method_1328(7, class_290.field_1585);
			lv2.method_1315((double)(i + 0), (double)(j + 18), (double)this.field_2050)
				.method_1312((double)((float)(k + 0) * 0.0078125F), (double)((float)(l + 18) * 0.0078125F))
				.method_1344();
			lv2.method_1315((double)(i + 18), (double)(j + 18), (double)this.field_2050)
				.method_1312((double)((float)(k + 18) * 0.0078125F), (double)((float)(l + 18) * 0.0078125F))
				.method_1344();
			lv2.method_1315((double)(i + 18), (double)(j + 0), (double)this.field_2050)
				.method_1312((double)((float)(k + 18) * 0.0078125F), (double)((float)(l + 0) * 0.0078125F))
				.method_1344();
			lv2.method_1315((double)(i + 0), (double)(j + 0), (double)this.field_2050)
				.method_1312((double)((float)(k + 0) * 0.0078125F), (double)((float)(l + 0) * 0.0078125F))
				.method_1344();
			lv.method_1350();
		}

		@Override
		protected int method_1947() {
			return class_413.this.field_2419.method_14327().size();
		}

		@Override
		protected boolean method_1937(int i, int j, double d, double e) {
			this.field_2428 = i;
			class_413.this.method_2145();
			return true;
		}

		@Override
		protected boolean method_1955(int i) {
			return i == this.field_2428;
		}

		@Override
		protected void method_1936() {
		}

		@Override
		protected void method_1935(int i, int j, int k, int l, int m, int n, float f) {
			class_3229 lv = (class_3229)class_413.this.field_2419.method_14327().get(class_413.this.field_2419.method_14327().size() - i - 1);
			class_2680 lv2 = lv.method_14286();
			class_2248 lv3 = lv2.method_11614();
			class_1792 lv4 = lv3.method_8389();
			if (lv4 == class_1802.field_8162) {
				if (lv3 == class_2246.field_10382) {
					lv4 = class_1802.field_8705;
				} else if (lv3 == class_2246.field_10164) {
					lv4 = class_1802.field_8187;
				}
			}

			class_1799 lv5 = new class_1799(lv4);
			String string = lv4.method_7864(lv5).method_10863();
			this.method_2148(j, k, lv5);
			class_413.this.field_2554.method_1729(string, (float)(j + 18 + 5), (float)(k + 3), 16777215);
			String string2;
			if (i == 0) {
				string2 = class_1074.method_4662("createWorld.customize.flat.layer.top", lv.method_14289());
			} else if (i == class_413.this.field_2419.method_14327().size() - 1) {
				string2 = class_1074.method_4662("createWorld.customize.flat.layer.bottom", lv.method_14289());
			} else {
				string2 = class_1074.method_4662("createWorld.customize.flat.layer", lv.method_14289());
			}

			class_413.this.field_2554.method_1729(string2, (float)(j + 2 + 213 - class_413.this.field_2554.method_1727(string2)), (float)(k + 3), 16777215);
		}

		@Override
		protected int method_1948() {
			return this.field_2168 - 70;
		}
	}
}
