package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.ArrayUtils;

@Environment(EnvType.CLIENT)
public class class_459 extends class_4265<class_459.class_461> {
	private final class_458 field_2735;
	private int field_2733;

	public class_459(class_458 arg, class_310 arg2) {
		super(arg2, arg.width + 45, arg.height, 63, arg.height - 32, 20);
		this.field_2735 = arg;
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
	protected int method_20078() {
		return super.method_20078() + 15;
	}

	@Override
	public int method_20053() {
		return super.method_20053() + 32;
	}

	@Environment(EnvType.CLIENT)
	public class class_460 extends class_459.class_461 {
		private final String field_2736;
		private final int field_2737;

		public class_460(String string) {
			this.field_2736 = class_1074.method_4662(string);
			this.field_2737 = class_459.this.field_19081.field_1772.method_1727(this.field_2736);
		}

		@Override
		public void method_1903(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			class_459.this.field_19081
				.field_1772
				.method_1729(this.field_2736, (float)(class_459.this.field_19081.field_1755.width / 2 - this.field_2737 / 2), (float)(j + m - 9 - 1), 16777215);
		}

		@Override
		public boolean isPartOfFocusCycle() {
			return false;
		}

		@Override
		public List<? extends class_364> children() {
			return Collections.emptyList();
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract static class class_461 extends class_4265.class_4266<class_459.class_461> {
	}

	@Environment(EnvType.CLIENT)
	public class class_462 extends class_459.class_461 {
		private final class_304 field_2740;
		private final String field_2741;
		private final class_4185 field_2739;
		private final class_4185 field_2743;

		private class_462(class_304 arg2) {
			this.field_2740 = arg2;
			this.field_2741 = class_1074.method_4662(arg2.method_1431());
			this.field_2739 = new class_4185(0, 0, 75, 20, class_1074.method_4662(arg2.method_1431()), arg2x -> class_459.this.field_2735.field_2727 = arg2);
			this.field_2743 = new class_4185(0, 0, 50, 20, class_1074.method_4662("controls.reset"), arg2x -> {
				class_459.this.field_19081.field_1690.method_1641(arg2, arg2.method_1429());
				class_304.method_1426();
			});
		}

		@Override
		public void method_1903(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
			boolean bl2 = class_459.this.field_2735.field_2727 == this.field_2740;
			class_459.this.field_19081.field_1772.method_1729(this.field_2741, (float)(k + 90 - class_459.this.field_2733), (float)(j + m / 2 - 9 / 2), 16777215);
			this.field_2743.x = k + 190;
			this.field_2743.y = j;
			this.field_2743.active = !this.field_2740.method_1427();
			this.field_2743.render(n, o, f);
			this.field_2739.x = k + 105;
			this.field_2739.y = j;
			this.field_2739.setMessage(this.field_2740.method_16007());
			boolean bl3 = false;
			if (!this.field_2740.method_1415()) {
				for (class_304 lv : class_459.this.field_19081.field_1690.field_1839) {
					if (lv != this.field_2740 && this.field_2740.method_1435(lv)) {
						bl3 = true;
						break;
					}
				}
			}

			if (bl2) {
				this.field_2739.setMessage(class_124.field_1068 + "> " + class_124.field_1054 + this.field_2739.getMessage() + class_124.field_1068 + " <");
			} else if (bl3) {
				this.field_2739.setMessage(class_124.field_1061 + this.field_2739.getMessage());
			}

			this.field_2739.render(n, o, f);
		}

		@Override
		public List<? extends class_364> children() {
			return ImmutableList.of(this.field_2739, this.field_2743);
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			return this.field_2739.mouseClicked(d, e, i) ? true : this.field_2743.mouseClicked(d, e, i);
		}

		@Override
		public boolean mouseReleased(double d, double e, int i) {
			return this.field_2739.mouseReleased(d, e, i) || this.field_2743.mouseReleased(d, e, i);
		}
	}
}
