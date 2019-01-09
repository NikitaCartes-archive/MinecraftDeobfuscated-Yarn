package net.minecraft;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.ArrayUtils;

@Environment(EnvType.CLIENT)
public class class_459 extends class_350<class_459.class_461> {
	private final class_458 field_2735;
	private final class_310 field_2734;
	private int field_2733;

	public class_459(class_458 arg, class_310 arg2) {
		super(arg2, arg.field_2561 + 45, arg.field_2559, 63, arg.field_2559 - 32, 20);
		this.field_2735 = arg;
		this.field_2734 = arg2;
		class_304[] lvs = ArrayUtils.clone(arg2.field_1690.field_1839);
		Arrays.sort(lvs);
		String string = null;

		for (class_304 lv : lvs) {
			String string2 = lv.method_1423();
			if (!string2.equals(string)) {
				string = string2;
				this.method_1901(new class_459.class_460(string2));
			}

			int i = arg2.field_1772.method_1727(class_1074.method_4662(lv.method_1431()));
			if (i > this.field_2733) {
				this.field_2733 = i;
			}

			this.method_1901(new class_459.class_462(lv));
		}
	}

	@Override
	protected int method_1948() {
		return super.method_1948() + 15;
	}

	@Override
	public int method_1932() {
		return super.method_1932() + 32;
	}

	@Environment(EnvType.CLIENT)
	public class class_460 extends class_459.class_461 {
		private final String field_2736;
		private final int field_2737;

		public class_460(String string) {
			this.field_2736 = class_1074.method_4662(string);
			this.field_2737 = class_459.this.field_2734.field_1772.method_1727(this.field_2736);
		}

		@Override
		public void method_1903(int i, int j, int k, int l, boolean bl, float f) {
			class_459.this.field_2734
				.field_1772
				.method_1729(
					this.field_2736, (float)(class_459.this.field_2734.field_1755.field_2561 / 2 - this.field_2737 / 2), (float)(this.method_1906() + j - 9 - 1), 16777215
				);
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract static class class_461 extends class_350.class_351<class_459.class_461> {
	}

	@Environment(EnvType.CLIENT)
	public class class_462 extends class_459.class_461 {
		private final class_304 field_2740;
		private final String field_2741;
		private final class_339 field_2739;
		private final class_339 field_2743;

		private class_462(class_304 arg2) {
			this.field_2740 = arg2;
			this.field_2741 = class_1074.method_4662(arg2.method_1431());
			this.field_2739 = new class_339(0, 0, 0, 75, 20, class_1074.method_4662(arg2.method_1431())) {
				@Override
				public void method_1826(double d, double e) {
					class_459.this.field_2735.field_2727 = arg2;
				}
			};
			this.field_2743 = new class_339(0, 0, 0, 50, 20, class_1074.method_4662("controls.reset")) {
				@Override
				public void method_1826(double d, double e) {
					class_459.this.field_2734.field_1690.method_1641(arg2, arg2.method_1429());
					class_304.method_1426();
				}
			};
		}

		@Override
		public void method_1903(int i, int j, int k, int l, boolean bl, float f) {
			int m = this.method_1906();
			int n = this.method_1907();
			boolean bl2 = class_459.this.field_2735.field_2727 == this.field_2740;
			class_459.this.field_2734.field_1772.method_1729(this.field_2741, (float)(n + 90 - class_459.this.field_2733), (float)(m + j / 2 - 9 / 2), 16777215);
			this.field_2743.field_2069 = n + 190;
			this.field_2743.field_2068 = m;
			this.field_2743.field_2078 = !this.field_2740.method_1427();
			this.field_2743.method_1824(k, l, f);
			this.field_2739.field_2069 = n + 105;
			this.field_2739.field_2068 = m;
			this.field_2739.field_2074 = this.field_2740.method_16007();
			boolean bl3 = false;
			if (!this.field_2740.method_1415()) {
				for (class_304 lv : class_459.this.field_2734.field_1690.field_1839) {
					if (lv != this.field_2740 && this.field_2740.method_1435(lv)) {
						bl3 = true;
						break;
					}
				}
			}

			if (bl2) {
				this.field_2739.field_2074 = class_124.field_1068 + "> " + class_124.field_1054 + this.field_2739.field_2074 + class_124.field_1068 + " <";
			} else if (bl3) {
				this.field_2739.field_2074 = class_124.field_1061 + this.field_2739.field_2074;
			}

			this.field_2739.method_1824(k, l, f);
		}

		@Override
		public boolean method_16807(double d, double e, int i) {
			return this.field_2739.method_16807(d, e, i) ? true : this.field_2743.method_16807(d, e, i);
		}

		@Override
		public boolean method_16804(double d, double e, int i) {
			return this.field_2739.method_16804(d, e, i) || this.field_2743.method_16804(d, e, i);
		}
	}
}
